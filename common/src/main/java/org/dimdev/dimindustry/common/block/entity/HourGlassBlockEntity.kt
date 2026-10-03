package org.dimdev.dimindustry.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.Tag
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.DimCore
import org.dimdev.dimcore.api.transfer.FluidUnit
import org.dimdev.dimcore.api.transfer.Handle
import org.dimdev.dimdoors.block.entity.CodecRecord
import kotlin.math.max

class HourGlassBlockEntity(pos: BlockPos, state: BlockState) :
    FluidTransferRiftBlockEntity<HourGlassBlockEntity>(DimensionalIndustryBlockEntities.HOURGLASS, pos, state) {

    var buffer: FluidUnit = FluidUnit.empty()
        private set

    val fillFraction: Float get() = buffer.amount.toFloat() / CAPACITY

    override fun insert(unit: FluidUnit, simulate: Boolean): Long {
        if (!buffer.isEmpty && !buffer.sameResource(unit)) {
            return 0
        }
        val have: Long = buffer.amount
        var accepted: Long = unit.amount.coerceAtMost(CAPACITY - have)
        if (accepted > 0 && !simulate) {
            buffer = unit.withAmount(have + accepted)
            sync()
        }

        accepted = max(0, accepted)

        if (accepted < unit.amount) {
            accepted += getNeighborHandle(Direction.DOWN).insert(unit.withAmount(unit.amount - accepted), simulate)
        }
        return accepted
    }

    override fun extract(unit: FluidUnit, simulate: Boolean): Long {
        if (!buffer.sameResource(unit)) {
            return 0
        }
        var taken: Long = unit.amount.coerceAtMost(buffer.amount)
        if (taken > 0 && !simulate) {
            buffer = if (taken == buffer.amount) FluidUnit.empty() else buffer.withAmount(buffer.amount - taken)
            sync()
        }

        if (taken < unit.amount) {
            taken += getNeighborHandle(Direction.UP).extract(unit.withAmount(unit.amount - taken), simulate)
        }
        return taken
    }

    override fun contents(): MutableList<FluidUnit> {
        val contents: MutableList<FluidUnit> = mutableListOf<FluidUnit>()
        if (buffer.isNotEmpty) {
            contents.add(buffer)
        }
        contents.addAll(getNeighborHandle(Direction.UP).contents())
        return contents
    }

    override fun serialize(serialize: Serialize<Tag, HourGlassBlockEntity>) {
        super.serialize(serialize)
        serialize.put<FluidUnit>(BUFFER)
    }

    override fun deserialize(nbt: Deserialize<Tag>) {
        super.deserialize(nbt)
        buffer = nbt.get<HourGlassBlockEntity, FluidUnit>(BUFFER)
    }

    override fun handle(direction: Direction?): Handle<FluidUnit>? {
        return when (direction) {
            Direction.UP -> insertOnly
            Direction.DOWN -> extractOnly
            null -> throughRift
            else -> null
        }
    }

    private val throughRift: Handle<FluidUnit> = object : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long {
            var done = this@HourGlassBlockEntity.insert(unit, simulate)
            val far = remote()
            if (done < unit.amount && far != null) {
                done += far.insert(unit.withAmount(unit.amount - done), simulate)
            }
            return done
        }

        override fun extract(unit: FluidUnit, simulate: Boolean): Long {
            var done = this@HourGlassBlockEntity.extract(unit, simulate)
            val far = remote()
            if (done < unit.amount && far != null) {
                done += far.extract(unit.withAmount(unit.amount - done), simulate)
            }
            return done
        }

        override fun contents(): MutableList<FluidUnit> {
            val contents = this@HourGlassBlockEntity.contents().toMutableList()
            val far = remote()
            if (far != null) {
                contents.addAll(far.contents())
            }
            return contents
        }
    }

    private val insertOnly: Handle<FluidUnit> = object : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long {
            return throughRift.insert(unit, simulate)
        }

        override fun extract(unit: FluidUnit, simulate: Boolean): Long {
            return 0
        }

        override fun contents(): MutableList<FluidUnit> {
            return mutableListOf()
        }
    }

    private val extractOnly: Handle<FluidUnit> = object : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long {
            return 0
        }

        override fun extract(unit: FluidUnit, simulate: Boolean): Long {
            return throughRift.extract(unit, simulate)
        }

        override fun contents(): List<FluidUnit> {
            return throughRift.contents()
        }
    }

    companion object {
        private val CAPACITY: Long = 4 * DimCore.platform.bucketAmount()

        val BUFFER: CodecRecord<HourGlassBlockEntity, FluidUnit> = CodecRecord<HourGlassBlockEntity, FluidUnit>("buffer", FluidUnit.CODEC, FluidUnit.empty(), HourGlassBlockEntity::buffer)
    }
}
