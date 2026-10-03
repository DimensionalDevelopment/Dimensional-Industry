package org.dimdev.dimindustry.common.client.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimindustry.common.block.entity.HourGlassBlockEntity
import org.dimdev.dimindustry.common.client.render.FluidCubeRenderer.renderFilled

class HourGlassBlockEntityRenderer(context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<HourGlassBlockEntity> {
    override fun render(
        blockEntity: HourGlassBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val unit = blockEntity.buffer
        if (unit.isEmpty) return

        val fluid = unit.fluid
        val level = blockEntity.getLevel()
        val pos = blockEntity.blockPos

        val percentage = blockEntity.fillFraction
        val fraction = if (percentage == 1.0f) 1.0f else (percentage % 0.5f) * 2f

        var topPercent = 0.0f
        var bottomPercent = 0.0f

        if (percentage >= 0.5) {
            topPercent = fraction
            bottomPercent = 1.0f
        } else {
            topPercent = 0f
            bottomPercent = fraction
        }

        renderFluid(poseStack, buffers, level, pos, light, fluid, true, topPercent)
        renderFluid(poseStack, buffers, level, pos, light, fluid, false, bottomPercent)
    }

    private fun renderFluid(
        stack: PoseStack,
        buffers: MultiBufferSource,
        level: Level?,
        pos: BlockPos?,
        light: Int,
        fluid: Fluid,
        top: Boolean,
        percentage: Float
    ) {
        if (percentage <= 0.0f) return
        val cubeY: Float = if (top) CUBE_Y2 else CUBE_Y1

        renderFilled(
            stack, buffers, fluid, level, pos, light, percentage,

            false,
            CUBE_X,
            cubeY,
            CUBE_Z,
            CUBE_X + CUBE_W,
            cubeY + CUBE_H,
            CUBE_Z + CUBE_W
        )
    }

    companion object {
        private val CUBE_W = 5.8f / 16f
        private val CUBE_H = 4.8f / 16f
        private val CUBE_X = 5.1f / 16f
        private val CUBE_Y1 = 2.6f / 16f
        private val CUBE_Y2 = 8.6f / 16f
        private val CUBE_Z = 5.1f / 16f
    }
}
