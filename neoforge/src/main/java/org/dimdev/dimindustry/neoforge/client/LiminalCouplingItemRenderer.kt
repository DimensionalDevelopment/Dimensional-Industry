package org.dimdev.dimindustry.neoforge.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlocks

class LiminalCouplingItemRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val state = CreateCompatBlocks.LIMINAL_COUPLING.defaultBlockState()
            .setValue(DirectionalKineticBlock.FACING, Direction.UP)

        // Casing (the regular baked block model)
        Minecraft.getInstance().blockRenderer.renderSingleBlock(state, ms, buffer, light, overlay)

        // Static shaft, same partial the BER/visual uses
        var superBuffer = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, Direction.UP);

        superBuffer.light<SuperByteBuffer>(light)
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))

        // Portal face
        LiminalCouplerBlockEntityRenderer.renderModelWithPortalShader(PORTAL, ms, buffer, light, overlay)
    }

    companion object {
        private val PORTAL = LiminalCouplerBlockEntityRenderer.createCube(Direction.UP)

        val EXTENSIONS = object : IClientItemExtensions {
            private val renderer by lazy { LiminalCouplingItemRenderer() }

            override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = renderer
        }
    }
}
