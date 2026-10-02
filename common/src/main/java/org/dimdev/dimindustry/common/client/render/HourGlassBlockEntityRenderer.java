package org.dimdev.dimindustry.common.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimindustry.common.block.entity.HourGlassBlockEntity;
import org.jetbrains.annotations.NotNull;

public class HourGlassBlockEntityRenderer implements BlockEntityRenderer<HourGlassBlockEntity> {
    private static final float CUBE_W = 5.8f / 16f;
    private static final float CUBE_H = 4.8f / 16f;
    private static final float CUBE_X = 5.1f / 16f;
    private static final float CUBE_Y1 = 2.6f / 16f;
    private static final float CUBE_Y2 = 8.6f / 16f;
    private static final float CUBE_Z = 5.1f / 16f;

    public HourGlassBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(HourGlassBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light, int overlay) {
        FluidUnit unit = blockEntity.getBuffer();
        if (unit.isEmpty()) return;

        var fluid = unit.fluid();
        var level = blockEntity.getLevel();
        var pos = blockEntity.getBlockPos();

        var percentage = blockEntity.getFillFraction();
        var fraction = percentage == 1.0f ? 1.0f : (percentage % 0.5f) * 2f;

        var topPercent = 0.0f;
        var bottomPercent = 0.0f;

        if(percentage >= 0.5) {
            topPercent = fraction;
            bottomPercent = 1.0f;
        } else {
            topPercent = 0f;
            bottomPercent = fraction;
        }

        renderFluid(poseStack, buffers, level, pos, light, fluid, true, topPercent);
        renderFluid(poseStack, buffers, level, pos, light, fluid, false, bottomPercent);
    }

    private void renderFluid(PoseStack stack, @NotNull MultiBufferSource buffers, Level level, BlockPos pos, int light, Fluid fluid, boolean top, float percentage) {
        if(percentage <= 0.0f) return;
        var cubeY = top ? CUBE_Y2 : CUBE_Y1;

        FluidCubeRenderer.renderFilled(stack, buffers, fluid, level, pos, light, percentage,

                false,
                CUBE_X,
                cubeY,
                CUBE_Z,
                CUBE_X + CUBE_W,
                cubeY + CUBE_H,
                CUBE_Z + CUBE_W
        );
    }
}
