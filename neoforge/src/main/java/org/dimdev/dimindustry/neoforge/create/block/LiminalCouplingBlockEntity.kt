package org.dimdev.dimindustry.neoforge.create.block

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.block.ModBlocks.DETACHED_RIFT
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.UUIDExtensions.rift
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes
import org.dimdev.dimindustry.neoforge.create.target.EmptyKineticTarget
import org.dimdev.dimindustry.neoforge.create.target.KineticTarget
import java.util.function.Supplier
import kotlin.math.abs

class LiminalCouplingBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : GeneratingKineticBlockEntity(type, pos, state), Rift<LiminalCouplingBlockEntity>, KineticTarget {
    override var isStateDirty: Boolean = false
    override var isDeleteRift: Boolean = true

    private var targetRotationalSpeed = 0f
    private var targetStressCapacity = 0f

    constructor(pos: BlockPos, state: BlockState) : this(CreateCompatBlockEntityTypes.LIMINAL_COUPLING, pos, state)

    init {
        setLazyTickRate(5)
    }

    override fun initialize() {
        super.initialize()

        if (getLevel()!!.isClientSide()) return
        syncTargetKinetics()
        updateGeneratedRotation()
    }

    override fun tick() {
        super<GeneratingKineticBlockEntity>.tick()

        if (level == null || level!!.isClientSide) {
            return
        }

        syncTargetKinetics()
    }

    override fun write(tag: CompoundTag, registries: HolderLookup.Provider?, clientPacket: Boolean) {
        super.write(tag, registries, clientPacket)

        tag.put("data", RiftData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow())
    }

    override fun read(tag: CompoundTag, registries: HolderLookup.Provider?, clientPacket: Boolean) {
        super.read(tag, registries, clientPacket)

        if (tag.contains("data")) {
            val result = RiftData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("data"))
            data = result.result().map(RiftData::copy).orElseGet(Supplier { RiftData() })
        }
    }

    override fun detach() {
        if (level == null) {
            return
        }

        level!!.setBlockAndUpdate(worldPosition, DETACHED_RIFT.defaultBlockState())
        level!!.getBlockEntity(worldPosition, ModBlockEntityTypes.DETACHED_RIFT).ifPresent { rift -> rift.data = data }
    }

    override fun getGeneratedSpeed(): Float {
        if (hasSource()) {
            return 0f
        }

        return targetRotationalSpeed
    }

    override fun calculateAddedStressCapacity(): Float {
        val speed = abs(getGeneratedSpeed())
        lastCapacityProvided = if (speed == 0f) 0f else targetStressCapacity / speed
        return lastCapacityProvided
    }

    override val stressCapacity: Float
        get() = if (hasNetwork() && hasSource() && getSpeed() != 0f) capacity else 0f

    override val rotationalSpeed: Float
        get() = if (hasSource()) getSpeed() else 0f

    private fun syncTargetKinetics() {
        val target = resolveTargetKinetics()
        val newSpeed = target.rotationalSpeed
        val newCapacity = target.stressCapacity

        if (targetRotationalSpeed == newSpeed && targetStressCapacity == newCapacity) {
            return
        }

        targetRotationalSpeed = newSpeed
        targetStressCapacity = newCapacity
        updateGeneratedRotation()
        setChanged()
    }


    private fun resolveTargetKinetics(): KineticTarget {
        val target = target
        val kineticTarget: KineticTarget = target.`as`(KineticTarget::class.java, location.riftOrPlaceholder().rift()) ?: EmptyKineticTarget

        if (kineticTarget is KineticBlockEntity && isSameCreateNetwork(kineticTarget)) {
            return EmptyKineticTarget
        }

        return kineticTarget
    }

    private fun isSameCreateNetwork(other: KineticBlockEntity): Boolean {
        return level === other.getLevel() && hasNetwork()
                && other.hasNetwork()
                && network == other.network
    }

    override var data: RiftData = RiftData()
        set(value) { field = value.copy() }


    override val riftBlockPos: BlockPos get() = blockPos

    override val riftBlockState: BlockState get() = blockState

    override val riftLevel: Level get() = level!!

    override fun handleSourceGone(location: Location?) {
        syncTargetKinetics()
    }

    override fun handleTargetGone(location: Location) {
        super.handleTargetGone(location)
        syncTargetKinetics()
    }
}