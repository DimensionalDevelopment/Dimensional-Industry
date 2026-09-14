package org.dimdev.dimindustry.neoforge.create.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.CustomBreakHandling;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiminaCouplerBlock extends DirectionalKineticBlock implements IBE<LiminalCouplerBlockEntity>, RiftProvider<LiminalCouplerBlockEntity>, CustomBreakHandling {

    public LiminaCouplerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT)) {
            return null;
        }

        Direction preferred = getPreferredFacing(context);
        if (preferred != null && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown())) {
            return defaultBlockState().setValue(FACING, preferred);
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public @Nullable Boolean customDestroy(Level level, BlockPos pos, BlockState state, int flags, int recursionLeft) {
        var rift = getRift(level, pos, state);
        if (rift == null) {
            return null;
        }

        rift.detach();
        return true;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        var rift = getRift(level, pos, state);
        if (rift == null) {
            super.onBlockExploded(state, level, pos, explosion);
            return;
        }

        rift.detach();
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<LiminalCouplerBlockEntity> getBlockEntityClass() {
        return LiminalCouplerBlockEntity.class;
    }

    @Override
    public BlockEntityType<LiminalCouplerBlockEntity> getBlockEntityType() {
        return CreateCompatBlockEntityTypes.LIMINAL_COUPLING;
    }

    @Override
    public BlockEntityType<LiminalCouplerBlockEntity> getRiftBlockEnityType() {
        return getBlockEntityType();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return IBE.super.newBlockEntity(pos, state);
    }

    @Override
    public String providerType() {
        return "Liminal Coupling";
    }
}
