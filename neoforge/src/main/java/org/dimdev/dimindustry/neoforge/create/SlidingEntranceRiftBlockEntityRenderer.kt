package org.dimdev.dimindustry.neoforge.create

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer.renderDimensionalPortal
import org.dimdev.dimdoors.client.RiftBlockEntityRenderer

class SlidingEntranceRiftBlockEntityRenderer(context: BlockEntityRendererProvider.Context) : RiftBlockEntityRenderer<SlidingEntranceRiftBlockEntity>(context) {
    override fun render(
        rift: SlidingEntranceRiftBlockEntity,
        tickDelta: Float,
        matrices: PoseStack,
        multiBufferSource: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        super.render(rift, tickDelta, matrices, multiBufferSource, light, overlay)

        val blockState = rift.blockState
        val renderState = rift.renderBlockState

        renderDimensionalPortal(blockState, matrices, multiBufferSource, light, overlay)

        if (rift.shouldRenderSliding(blockState)) {
            renderSlidingDoor(
                rift,
                renderState!!,
                tickDelta,
                matrices,
                multiBufferSource,
                light,
                overlay
            )
        } else {
            renderDoorBlockState(
                renderState!!,
                rift.getLevel()!!.getRandom(),
                matrices,
                multiBufferSource,
                light,
                overlay
            )
        }
    }

    private fun renderDoorBlockState(
        state: BlockState,
        random: RandomSource,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        renderBlockState(state, random, matrixStack, vertexConsumerProvider, light, overlay)
        if (state.block is DoorBlock) {
            matrixStack.pushPose()
            matrixStack.translate(0f, 1f, 0f)
            renderBlockState(
                state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER),
                random,
                matrixStack,
                vertexConsumerProvider,
                light,
                overlay
            )
            matrixStack.popPose()
        }
    }

    private fun renderSlidingDoor(
        blockEntity: SlidingEntranceRiftBlockEntity,
        renderState: BlockState,
        partialTicks: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val facing = renderState.getValue(DoorBlock.FACING)
        var movementDirection = facing.clockWise

        if (renderState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT) {
            movementDirection = movementDirection.opposite
        }

        val value = blockEntity.getAnimation(partialTicks)
        val value2 = Mth.clamp(value * 10, 0f, 1f)

        val offset = Vec3.atLowerCornerOf(movementDirection.normal)
            .scale((value * value * 13 / 16f).toDouble())
            .add(
                Vec3.atLowerCornerOf(facing.normal)
                    .scale((value2 * 1 / 32f).toDouble())
            )

        val vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped())
        if ((renderState.block as SlidingDoorBlock).isFoldingDoor) {
            val flip = renderState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT
            for (left in booleanArrayOf(true, false)) {
                val f = (if (flip) -1 else 1).toFloat()
                val model = getFoldingModel(renderState, left xor flip)

                matrixStack.pushPose()
                matrixStack.translate(0f, -1 / 512f, 0f)
                val facingOffset = Vec3.atLowerCornerOf(facing.normal).scale((value2 * 1 / 32f).toDouble())
                matrixStack.translate(facingOffset.x, facingOffset.y, facingOffset.z)
                rotateCenteredY(matrixStack, facing.clockWise.toYRot())

                if (flip) {
                    matrixStack.translate(0f, 0f, 1f)
                }
                matrixStack.mulPose(Axis.YP.rotationDegrees(91 * f * value * value))

                if (!left) {
                    matrixStack.translate(0f, 0f, f / 2f)
                    matrixStack.mulPose(Axis.YP.rotationDegrees(-181 * f * value * value))
                }

                if (flip) {
                    matrixStack.translate(0f, 0f, -1 / 2f)
                }

                renderModel(
                    model,
                    renderState,
                    blockEntity.getLevel()!!.getRandom(),
                    matrixStack,
                    vertexConsumer,
                    light,
                    overlay
                )
                matrixStack.popPose()
            }

            return
        }

        val random = blockEntity.getLevel()!!.getRandom()
        for (half in DoubleBlockHalf.entries) {
            matrixStack.pushPose()
            matrixStack.translate(
                offset.x,
                offset.y + (if (half == DoubleBlockHalf.UPPER) 1 - 1 / 512f else 0f),
                offset.z
            )
            renderBlockState(
                renderState.setValue(DoorBlock.OPEN, false).setValue(DoorBlock.HALF, half),
                random,
                matrixStack,
                buffer,
                light,
                overlay
            )
            matrixStack.popPose()
        }
    }

    private fun getFoldingModel(renderState: BlockState, right: Boolean): BakedModel {
        val blockId = BuiltInRegistries.BLOCK.getKey(renderState.block)
        val modelId = ResourceLocation.fromNamespaceAndPath(blockId.namespace, "block/${blockId.path}${if (right) "/fold_right" else "/fold_left"}")
        return Minecraft.getInstance().modelManager.getModel(ModelResourceLocation.standalone(modelId))
    }

    private fun rotateCenteredY(matrixStack: PoseStack, degrees: Float) {
        matrixStack.translate(.5f, .5f, .5f)
        matrixStack.mulPose(Axis.YP.rotationDegrees(degrees))
        matrixStack.translate(-.5f, -.5f, -.5f)
    }

//    private fun getTransformer(blockEntity: SlidingEntranceRiftBlockEntity): Transformer? {
//        if (blockEntity.getBlockState().getBlock() is DimensionalTrapDoorBlock<*>) {
//            return if (blockEntity.getBlockState()
//                    .getValue<Half?>(TrapDoorBlock.HALF) == Half.TOP
//            ) DefaultTransformation.TOP_TRAPDOOR else DefaultTransformation.BOTTOMM_TRAPDOOR
//        }
//
//        return fromDirection(blockEntity.orientation)
//    }

    private fun renderBlockState(
        renderState: BlockState,
        random: RandomSource,
        matrixStack: PoseStack,
        vertexConsumerProvider: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val model = Minecraft.getInstance().modelManager.blockModelShaper.getBlockModel(renderState)
        val renderType = ItemBlockRenderTypes.getRenderType(renderState, false)
        val vertexConsumer = vertexConsumerProvider.getBuffer(renderType)

        renderModel(model, renderState, random, matrixStack, vertexConsumer, light, overlay)
    }

    private fun renderModel(
        model: BakedModel,
        renderState: BlockState?,
        random: RandomSource,
        matrixStack: PoseStack,
        vertexConsumer: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        for (direction in Direction.entries) {
            val quads = model.getQuads(renderState, direction, random)
            renderQuads(matrixStack, vertexConsumer, quads, light, overlay)
        }

        val quads = model.getQuads(renderState, null, random)
        renderQuads(matrixStack, vertexConsumer, quads, light, overlay)
    }

    private fun renderQuads(
        stack: PoseStack,
        consumer: VertexConsumer,
        quads: MutableList<BakedQuad>,
        light: Int,
        overlay: Int
    ) {
        val pose = stack.last()
        for (quad in quads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, 1.0f, light, overlay)
        }
    }
}
