package org.dimdev.dimindustry.common.client;

import net.minecraft.client.renderer.RenderType;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.client.ModClient;
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks;
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities;
import org.dimdev.dimindustry.common.client.render.HourGlassBlockEntityRenderer;

public class DimensionalIndustryClient implements ModClient<IDimensionalIndustryClientSided<?>> {
    private static IDimensionalIndustryClientSided<?> sided;

    @Override
    public void init(IDimensionalIndustryClientSided<?> sided) {
        DimensionalIndustryClient.sided = sided;
    }

    @Override
    public void delayedInit() {
        DimCore.clientPlatform().register(RenderType.cutout(), DimensionalIndustryBlocks.HOURGLASS);
    }

    @Override
    public void initBlockEntityRenderers(BlockEntityRegister register) {
        register.register(DimensionalIndustryBlockEntities.HOURGLASS, HourGlassBlockEntityRenderer::new);
    }

    @Override
    public String getModId() {
        return "dimindustry";
    }

    public static IDimensionalIndustryClientSided<?> getSided() {
        return sided;
    }
}
