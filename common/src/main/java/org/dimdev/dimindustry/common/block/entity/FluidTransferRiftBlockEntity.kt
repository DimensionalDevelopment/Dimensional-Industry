package org.dimdev.dimindustry.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.transfer.FluidUnit
import org.dimdev.dimcore.api.transfer.TransferType
import org.dimdev.dimdoors.api.rift.target.FluidTarget

abstract class FluidTransferRiftBlockEntity<T : FluidTransferRiftBlockEntity<T>>(type: BlockEntityType<T>, pos: BlockPos, state: BlockState) : TransferRiftBlockEntity<FluidUnit, FluidTarget, T>(type, pos, state), FluidTarget {
    override val targetClass: Class<FluidTarget> get() = FluidTarget::class.java

    override val transferType: TransferType<FluidUnit> get() = TransferType.FLUID
}
