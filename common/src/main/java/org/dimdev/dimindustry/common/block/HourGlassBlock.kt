package org.dimdev.dimindustry.common.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.DimCore
import org.dimdev.dimdoors.block.ModBlocks.DETACHED_RIFT
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.WaterLoggableBlockWithEntity
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities
import org.dimdev.dimindustry.common.block.entity.HourGlassBlockEntity


open class HourGlassBlock(settings: Properties) : WaterLoggableBlockWithEntity(settings), RiftProvider<HourGlassBlockEntity> {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun codec(): MapCodec<HourGlassBlock> = CODEC

    override val riftBlockEnityType: BlockEntityType<HourGlassBlockEntity>
        get() = DimensionalIndustryBlockEntities.HOURGLASS

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): ItemInteractionResult {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS
        if (DimCore.transfer.interactWithFluid(player, hand, level, pos, null)) return ItemInteractionResult.SUCCESS
        return super.useItemOn(stack, state, level, pos, player, hand, hit)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        if (!ctx.level.getBlockState(ctx.clickedPos).`is`(DETACHED_RIFT)) {
            return null
        }

        if (ctx.player == null || !ctx.player!!.isShiftKeyDown) {
            return defaultBlockState().setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                ctx.horizontalDirection
            )
        }

        return super.getStateForPlacement(ctx)
    }

    companion object {
        val CODEC: MapCodec<HourGlassBlock> = simpleCodec(::HourGlassBlock)
    }
}
