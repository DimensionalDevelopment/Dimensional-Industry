package org.dimdev.dimindustry.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.dimdev.dimcore.api.transfer.Handle;
import org.dimdev.dimcore.api.transfer.TransferType;
import org.dimdev.dimcore.api.transfer.Unit;
import org.dimdev.dimdoors.api.rift.target.TransferTarget;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import java.util.List;

/**
 * A rift that carries a resource. Two roles, one object:
 * <ul>
 *   <li>As the rift's {@link TransferTarget} (what the far end resolves to), it is the block it faces.</li>
 *   <li>{@link #remote()} is what local neighbours should be handed: the far end's target.</li>
 * </ul>
 * Subclasses implement the per-resource {@code K} so {@code getTarget().as(K)} lands on them.
 */
public abstract class TransferRiftBlockEntity<T extends Unit<T>, K extends TransferTarget<T>, TE extends TransferRiftBlockEntity<T, K, TE>> extends RiftBlockEntity<TE> implements Handle<T> {

    public TransferRiftBlockEntity(BlockEntityType<TE> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    /** The block this rift faces on its own side. */
    public Handle<T> getNeighborHandle(Direction direction) {
        Handle<T> handle = getTransferType().find(level, worldPosition.relative(direction), direction.getOpposite());
        return handle == null ? Handle.empty() : handle;
    }

    /** What is on the other side of the rift. */
    public K remote() {
        return getTarget().as(getTargetClass());
    }

    public abstract Class<K> getTargetClass();
    public abstract TransferType<T> getTransferType();

    @Override
    public long extract(T unit, boolean simulate) {
        return getNeighborHandle(getFacing()).extract(unit, simulate);
    }

    @Override
    public long insert(T unit, boolean simulate) {
        return getNeighborHandle(getFacing()).insert(unit, simulate);
    }

    @Override
    public List<T> contents() {
        return getNeighborHandle(getFacing()).contents();
    }

    public abstract Handle<T> handle(Direction direction);
}
