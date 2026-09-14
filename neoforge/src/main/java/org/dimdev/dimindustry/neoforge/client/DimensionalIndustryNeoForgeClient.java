package org.dimdev.dimindustry.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.dimdev.dimcore.client.NeoForgeClientSided;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.IDimDoorsClientSided;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.IDimensionalIndustrySided;
import org.dimdev.dimindustry.common.client.DimensionalIndustryClient;
import org.dimdev.dimindustry.common.client.IDimensionalIndustryClientSided;
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes;
import org.dimdev.dimindustry.neoforge.create.SlidingEntranceRiftBlockEntityRenderer;

@Mod(value = DimensionalIndustry.MOD_ID, dist = Dist.CLIENT)
public class DimensionalIndustryNeoForgeClient extends NeoForgeClientSided<DimensionalIndustryNeoForgeClient, DimensionalIndustryClient> implements IDimensionalIndustryClientSided<DimensionalIndustryNeoForgeClient> {
    public DimensionalIndustryNeoForgeClient(IEventBus bus, ModContainer container) {
        super(bus, container, new DimensionalIndustryClient());
        if (DimensionalIndustry.getSided().isModLoaded("create")) {
            bus.addListener(DimensionalIndustryNeoForgeClient::registerCreateBlockEntityRenderers);
        }
    }

    private static void registerCreateBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, SlidingEntranceRiftBlockEntityRenderer::new);
    }
}
