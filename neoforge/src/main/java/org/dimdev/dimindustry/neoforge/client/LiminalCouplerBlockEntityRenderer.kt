package org.dimdev.dimindustry.neoforge.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.Util
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.client.DimensionalDoorsClient
import org.dimdev.dimindustry.neoforge.create.block.LiminalCouplingBlockEntity
import java.util.*

class LiminalCouplerBlockEntityRenderer(context: BlockEntityRendererProvider.Context) :
    KineticBlockEntityRenderer<LiminalCouplingBlockEntity>(context) {
    override fun renderSafe(
        be: LiminalCouplingBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val facing = be.blockState.getValue(DirectionalKineticBlock.FACING)

        DimensionalDoorsClient.detector.wrap { _ -> renderModelWithPortalShader(CUBES[facing]!!, ms, buffer, light, overlay) }

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
    }

    override fun getRotatedModel(be: LiminalCouplingBlockEntity, state: BlockState): SuperByteBuffer {
        val direction = state.getValue(DirectionalKineticBlock.FACING)
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, direction)
    }

    companion object {
        private val CUBES = Util.make(EnumMap<Direction, ModelPart.Cube>(Direction::class.java)) { map ->
            for (direction in Direction.entries) map[direction] = createCube(direction)
        }

        fun renderModelWithPortalShader(
            model: ModelPart.Cube,
            matrixStack: PoseStack,
            vertexConsumerProvider: MultiBufferSource,
            light: Int,
            overlay: Int
        ) {
            matrixStack.pushPose()
            try {
                DimensionalDoorsClient.detector.wrap { type: RenderType ->
                    model.compile(
                        matrixStack.last(),
                        vertexConsumerProvider.getBuffer(type),
                        light,
                        overlay,
                        0x000000
                    )
                }
            } finally {
                matrixStack.popPose()
            }
        }

        @JvmOverloads
        fun createCube(direction: Direction, texU: Int = 0, texV: Int = 0): ModelPart.Cube {
            val origin = floatArrayOf(2f, 2f, 2f)
            val size = floatArrayOf(12f, 12f, 12f)
            val axis = direction.axis.ordinal // X=0, Y=1, Z=2

            size[axis] = 11f
            if (direction.axisDirection == Direction.AxisDirection.NEGATIVE) {
                origin[axis] = 3f
            }

            return ModelPart.Cube(
                texU, texV,
                origin[0], origin[1], origin[2],
                size[0], size[1], size[2],
                0f, 0f, 0f, false, 16f, 16f,
                EnumSet.of(direction)
            )
        }
    }
}
