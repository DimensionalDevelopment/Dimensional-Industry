package org.dimdev.dimindustry.common.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.client.ClientPlatform;
import org.jetbrains.annotations.Nullable;

public final class FluidCubeRenderer {
    private FluidCubeRenderer() {}

    public static void renderFilled(PoseStack ms, MultiBufferSource buffers, Fluid fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int light, float fill, boolean renderBottom,
                                    float xMin, float yMin,
                                    float zMin, float xMax,
                                    float yMax, float zMax
    ) {
        if (fill <= 0) return;

        renderFluidBox(ms, buffers, fluid, level, pos, light, renderBottom,  xMin, yMin, zMin, xMax, yMin + (yMax - yMin) * Math.min(fill, 1), zMax);
    }

    public static void renderFluidBox(PoseStack ms, MultiBufferSource buffers, Fluid fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos,
                                      int light, boolean renderBottom, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        renderFluidBox(ms, buffers.getBuffer(RenderType.translucentMovingBlock()), fluid, level, pos, light, xMin, yMin, zMin, xMax, yMax, zMax, renderBottom);
    }

    public static void renderFluidBox(PoseStack ms, VertexConsumer builder, Fluid fluid, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos,
                                      int light, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, boolean renderBottom) {
        ClientPlatform platform = DimCore.clientPlatform();
        TextureAtlasSprite sprite = platform.fluidSprite(fluid);

        int color = platform.fluidTint(fluid, level, pos);

        int blockLight = Math.max(LightTexture.block(light), fluid.defaultFluidState().createLegacyBlock().getLightEmission());
        light = LightTexture.pack(blockLight, LightTexture.sky(light));

        PoseStack.Pose pose = ms.last();
        for (Direction side : Direction.values()) {
            if (side == Direction.DOWN && !renderBottom) continue;

            float shade = level == null ? 1 : level.getShade(side, true);
            renderFace(side, builder, pose, light, shaded(color, shade), sprite, xMin, yMin, zMin, xMax, yMax, zMax);
        }
    }

    private static void renderFace(Direction dir, VertexConsumer builder, PoseStack.Pose pose, int light, int color, TextureAtlasSprite sprite,
                                   float xMin, float yMin,
                                   float zMin, float xMax,
                                   float yMax, float zMax
    ) {
        Direction.Axis axis = dir.getAxis();
        boolean positive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        float depth = switch (axis) {
            case X -> positive ? xMax : xMin;
            case Y -> positive ? yMax : yMin;
            case Z -> positive ? zMax : zMin;
        };

        float aMin = axis == Direction.Axis.X ? zMin : xMin;
        float aMax = axis == Direction.Axis.X ? zMax : xMax;
        float bMin = axis == Direction.Axis.Y ? zMin : yMin;
        float bMax = axis == Direction.Axis.Y ? zMax : yMax;

        boolean flipU = dir == Direction.NORTH || dir == Direction.EAST;
        boolean flipV = dir != Direction.UP;

        for (float a1 = aMin; a1 < aMax; ) {
            float cellA = Mth.floor(a1);
            float a2 = Math.min(cellA + 1, aMax);
            for (float b1 = bMin; b1 < bMax; ) {
                float cellB = Mth.floor(b1);
                float b2 = Math.min(cellB + 1, bMax);
                float pa = flipU ? a2 : a1;
                float qa = flipU ? a1 : a2;
                float pb = flipV ? b2 : b1;
                float qb = flipV ? b1 : b2;
                vertex(builder, pose, dir, pa, pb, depth, uv(sprite, flipU, cellA, pa, true), uv(sprite, flipV, cellB, pb, false), color, light);
                vertex(builder, pose, dir, pa, qb, depth, uv(sprite, flipU, cellA, pa, true), uv(sprite, flipV, cellB, qb, false), color, light);
                vertex(builder, pose, dir, qa, qb, depth, uv(sprite, flipU, cellA, qa, true), uv(sprite, flipV, cellB, qb, false), color, light);
                vertex(builder, pose, dir, qa, pb, depth, uv(sprite, flipU, cellA, qa, true), uv(sprite, flipV, cellB, pb, false), color, light);
                b1 = b2;
            }
            a1 = a2;
        }
    }

    private static float uv(TextureAtlasSprite sprite, boolean flip, float cell, float c, boolean u) {
        float t = flip ? cell + 1 - c : c - cell;
        float lo = u ? sprite.getU0() : sprite.getV0();
        float hi = u ? sprite.getU1() : sprite.getV1();
        return Mth.lerp(sprite.uvShrinkRatio(), lo + (hi - lo) * t, (lo + hi) * 0.5f);
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, Direction dir, float a, float b, float depth, float u, float v, int color, int light) {
        Direction.Axis axis = dir.getAxis();
        float x = axis == Direction.Axis.X ? depth : a;
        float y = axis == Direction.Axis.Y ? depth : b;
        float z = switch (axis) {
            case X -> a;
            case Y -> b;
            case Z -> depth;
        };

        builder.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setLight(light)
                .setNormal(pose, dir.getStepX(), dir.getStepY(), dir.getStepZ());
    }

    private static int shaded(int argb, float shade) {
        int s = Math.round(shade * 255);
        return FastColor.ARGB32.multiply(argb, FastColor.ARGB32.color(255, s, s, s));
    }
}
