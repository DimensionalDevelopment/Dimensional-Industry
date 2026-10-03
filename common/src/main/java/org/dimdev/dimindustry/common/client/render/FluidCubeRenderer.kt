package org.dimdev.dimindustry.common.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.FastColor
import net.minecraft.util.Mth
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.DimCore.clientPlatform
import org.dimdev.dimcore.api.client.ClientPlatform
import kotlin.math.max
import kotlin.math.min

object FluidCubeRenderer {
    @JvmStatic
    fun renderFilled(
        ms: PoseStack,
        buffers: MultiBufferSource,
        fluid: Fluid,
        level: BlockAndTintGetter?,
        pos: BlockPos?,
        light: Int,
        fill: Float,
        renderBottom: Boolean,
        xMin: Float,
        yMin: Float,
        zMin: Float,
        xMax: Float,
        yMax: Float,
        zMax: Float
    ) {
        if (fill <= 0) return

        renderFluidBox(
            ms,
            buffers,
            fluid,
            level,
            pos,
            light,
            renderBottom,
            xMin,
            yMin,
            zMin,
            xMax,
            yMin + (yMax - yMin) * min(fill, 1f),
            zMax
        )
    }

    fun renderFluidBox(
        ms: PoseStack, buffers: MultiBufferSource, fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?,
        light: Int, renderBottom: Boolean, xMin: Float, yMin: Float, zMin: Float, xMax: Float, yMax: Float, zMax: Float
    ) {
        renderFluidBox(
            ms,
            buffers.getBuffer(RenderType.translucentMovingBlock()),
            fluid,
            level,
            pos,
            light,
            xMin,
            yMin,
            zMin,
            xMax,
            yMax,
            zMax,
            renderBottom
        )
    }

    fun renderFluidBox(
        ms: PoseStack, builder: VertexConsumer, fluid: Fluid, level: BlockAndTintGetter?, pos: BlockPos?,
        light: Int, xMin: Float, yMin: Float, zMin: Float, xMax: Float, yMax: Float, zMax: Float, renderBottom: Boolean
    ) {
        var light = light
        val platform: ClientPlatform = clientPlatform
        val sprite = platform.fluidSprite(fluid)

        val color = platform.fluidTint(fluid, level, pos)

        val blockLight =
            max(LightTexture.block(light), fluid.defaultFluidState().createLegacyBlock().lightEmission)
        light = LightTexture.pack(blockLight, LightTexture.sky(light))

        val pose = ms.last()
        for (side in Direction.entries) {
            if (side == Direction.DOWN && !renderBottom) continue

            val shade = level?.getShade(side, true) ?: 1f
            renderFace(side, builder, pose, light, shaded(color, shade), sprite, xMin, yMin, zMin, xMax, yMax, zMax)
        }
    }

    private fun renderFace(
        dir: Direction,
        builder: VertexConsumer,
        pose: PoseStack.Pose,
        light: Int,
        color: Int,
        sprite: TextureAtlasSprite,
        xMin: Float,
        yMin: Float,
        zMin: Float,
        xMax: Float,
        yMax: Float,
        zMax: Float
    ) {
        val axis = dir.getAxis()
        val positive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE
        val depth = when (axis) {
            Direction.Axis.X -> if (positive) xMax else xMin
            Direction.Axis.Y -> if (positive) yMax else yMin
            Direction.Axis.Z -> if (positive) zMax else zMin
        }

        val aMin = if (axis === Direction.Axis.X) zMin else xMin
        val aMax = if (axis === Direction.Axis.X) zMax else xMax
        val bMin = if (axis === Direction.Axis.Y) zMin else yMin
        val bMax = if (axis === Direction.Axis.Y) zMax else yMax

        val flipU = dir == Direction.NORTH || dir == Direction.EAST
        val flipV = dir != Direction.UP

        var a1 = aMin
        while (a1 < aMax) {
            val cellA = Mth.floor(a1).toFloat()
            val a2 = min(cellA + 1, aMax)
            var b1 = bMin
            while (b1 < bMax) {
                val cellB = Mth.floor(b1).toFloat()
                val b2 = min(cellB + 1, bMax)
                val pa = if (flipU) a2 else a1
                val qa = if (flipU) a1 else a2
                val pb = if (flipV) b2 else b1
                val qb = if (flipV) b1 else b2
                vertex(
                    builder,
                    pose,
                    dir,
                    pa,
                    pb,
                    depth,
                    uv(sprite, flipU, cellA, pa, true),
                    uv(sprite, flipV, cellB, pb, false),
                    color,
                    light
                )
                vertex(
                    builder,
                    pose,
                    dir,
                    pa,
                    qb,
                    depth,
                    uv(sprite, flipU, cellA, pa, true),
                    uv(sprite, flipV, cellB, qb, false),
                    color,
                    light
                )
                vertex(
                    builder,
                    pose,
                    dir,
                    qa,
                    qb,
                    depth,
                    uv(sprite, flipU, cellA, qa, true),
                    uv(sprite, flipV, cellB, qb, false),
                    color,
                    light
                )
                vertex(
                    builder,
                    pose,
                    dir,
                    qa,
                    pb,
                    depth,
                    uv(sprite, flipU, cellA, qa, true),
                    uv(sprite, flipV, cellB, pb, false),
                    color,
                    light
                )
                b1 = b2
            }
            a1 = a2
        }
    }

    private fun uv(sprite: TextureAtlasSprite, flip: Boolean, cell: Float, c: Float, u: Boolean): Float {
        val t = if (flip) cell + 1 - c else c - cell
        val lo = if (u) sprite.getU0() else sprite.getV0()
        val hi = if (u) sprite.getU1() else sprite.getV1()
        return Mth.lerp(sprite.uvShrinkRatio(), lo + (hi - lo) * t, (lo + hi) * 0.5f)
    }

    private fun vertex(
        builder: VertexConsumer,
        pose: PoseStack.Pose,
        dir: Direction,
        a: Float,
        b: Float,
        depth: Float,
        u: Float,
        v: Float,
        color: Int,
        light: Int
    ) {
        val axis = dir.getAxis()
        val x = if (axis === Direction.Axis.X) depth else a
        val y = if (axis === Direction.Axis.Y) depth else b
        val z = when (axis) {
            Direction.Axis.X -> a
            Direction.Axis.Y -> b
            Direction.Axis.Z -> depth
        }

        builder.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setLight(light)
            .setNormal(pose, dir.getStepX().toFloat(), dir.getStepY().toFloat(), dir.getStepZ().toFloat())
    }

    private fun shaded(argb: Int, shade: Float): Int {
        val s = Math.round(shade * 255)
        return FastColor.ARGB32.multiply(argb, FastColor.ARGB32.color(255, s, s, s))
    }
}
