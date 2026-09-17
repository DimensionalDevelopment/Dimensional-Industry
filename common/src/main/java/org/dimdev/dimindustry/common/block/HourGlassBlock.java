package org.dimdev.dimindustry.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.WaterLoggableBlockWithEntity;
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities;
import org.dimdev.dimindustry.common.block.entity.HourGlassBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HourGlassBlock extends WaterLoggableBlockWithEntity implements RiftProvider<HourGlassBlockEntity> {
    public static final MapCodec<HourGlassBlock> CODEC = simpleCodec(HourGlassBlock::new);

    protected HourGlassBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<HourGlassBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<HourGlassBlockEntity> getRiftBlockEnityType() {
        return DimensionalIndustryBlockEntities.HOURGLASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (DimCore.transfer().interactWithFluid(player, hand, level, pos, null)) return ItemInteractionResult.SUCCESS;
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT)) {
            return null;
        }

        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
            return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
        }

        return super.getStateForPlacement(context);
    }
}
