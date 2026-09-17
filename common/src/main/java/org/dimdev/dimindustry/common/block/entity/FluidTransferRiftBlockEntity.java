package org.dimdev.dimindustry.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimcore.api.transfer.TransferType;
import org.dimdev.dimdoors.api.rift.target.FluidTarget;

public abstract class FluidTransferRiftBlockEntity<T extends FluidTransferRiftBlockEntity<T>> extends TransferRiftBlockEntity<FluidUnit, FluidTarget, T> implements FluidTarget {
    public FluidTransferRiftBlockEntity(BlockEntityType<T> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Class<FluidTarget> getTargetClass() {
        return FluidTarget.class;
    }

    @Override
    public TransferType<FluidUnit> getTransferType() {
        return TransferType.FLUID;
    }
}
