package org.dimdev.dimindustry.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.dimdev.dimcore.api.transfer.Handle
import org.dimdev.dimcore.api.transfer.TransferType
import org.dimdev.dimcore.api.transfer.Unit
import org.dimdev.dimdoors.api.rift.target.TransferTarget
import org.dimdev.dimdoors.block.entity.RiftBlockEntity

/**
 * A rift that carries a resource. Two roles, one object:
 * 
 *  * As the rift's [TransferTarget] (what the far end resolves to), it is the block it faces.
 *  * [.remote] is what local neighbours should be handed: the far end's target.
 * 
 * Subclasses implement the per-resource `K` so `getTarget().as(K)` lands on them.
 */
abstract class TransferRiftBlockEntity<T : Unit<T>, K : TransferTarget<T>, TE : TransferRiftBlockEntity<T, K, TE>>(
    type: BlockEntityType<TE>,
    pos: BlockPos,
    state: BlockState
) : RiftBlockEntity<TE>(type, pos, state), Handle<T> {
    val facing: Direction
        get() = blockState.getValue(BlockStateProperties.FACING)

    /** The block this rift faces on its own side.  */
    fun getNeighborHandle(direction: Direction): Handle<T> {
        val handle = this.transferType.find(level, worldPosition.relative(direction), direction.opposite)
        return handle ?: Handle.empty<T>()
    }

    /** What is on the other side of the rift.  */
    fun remote(): K? {
        return target.`as`(this.targetClass)
    }

    abstract val targetClass: Class<K>
    abstract val transferType: TransferType<T>

    override fun extract(unit: T, simulate: Boolean): Long {
        return getNeighborHandle(this.facing).extract(unit, simulate)
    }

    override fun insert(unit: T, simulate: Boolean): Long {
        return getNeighborHandle(this.facing).insert(unit, simulate)
    }

    override fun contents(): List<T> {
        return getNeighborHandle(this.facing).contents()
    }

    abstract fun handle(direction: Direction?): Handle<T>?
}
