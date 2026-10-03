package org.dimdev.dimindustry.neoforge.client

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.client.NeoForgeClientSided
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.common.client.DimensionalIndustryClient
import org.dimdev.dimindustry.common.client.IDimensionalIndustryClientSided
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlocks
import org.dimdev.dimindustry.neoforge.create.SlidingEntranceRiftBlockEntity
import org.dimdev.dimindustry.neoforge.create.SlidingEntranceRiftBlockEntityRenderer
import org.dimdev.dimindustry.neoforge.create.block.LiminalCouplingBlockEntity
import org.dimdev.dimindustry.neoforge.create.client.CreateCompatClient
import java.util.function.Consumer

@Mod(value = DimensionalIndustry.MOD_ID, dist = [Dist.CLIENT])
class DimensionalIndustryNeoForgeClient(bus: IEventBus, container: ModContainer) : NeoForgeClientSided<DimensionalIndustryNeoForgeClient, DimensionalIndustryClient>(
        bus,
        DimensionalIndustryClient
    ), IDimensionalIndustryClientSided<DimensionalIndustryNeoForgeClient> {
    init {
        if (platform.isModLoaded("create")) {
            bus.addListener<FMLClientSetupEvent>({ event -> event.enqueueWork(CreateCompatClient::init) })

            bus.addListener(::registerCreateBlockEntityRenderers)
            bus.addListener(::registerCreateClientExtensions)
        }
    }

    companion object {
        private fun registerCreateBlockEntityRenderers(event: RegisterRenderers) {
            event.registerBlockEntityRenderer(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, ::SlidingEntranceRiftBlockEntityRenderer)
            event.registerBlockEntityRenderer(CreateCompatBlockEntityTypes.LIMINAL_COUPLING, ::LiminalCouplerBlockEntityRenderer)
        }

        private fun registerCreateClientExtensions(event: RegisterClientExtensionsEvent) {
            event.registerItem(LiminalCouplingItemRenderer.EXTENSIONS, CreateCompatBlocks.LIMINAL_COUPLING.asItem())
        }
    }
}
