package org.dimdev.dimindustry.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimcore.api.ModCommon;
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks;
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities;

import java.awt.*;

public class DimensionalIndustry implements ModCommon<IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>>> {
    public static final String MOD_ID = "dimindustry";
    public static final DimensionalIndustry INSTANCE = new DimensionalIndustry();

    private IDimensionalIndustrySided<?> sided;

    @Override
    public void init(IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>> sided) {
        this.sided = sided;

        sided.checkCompat();

        sided.registerRunnable(Registries.BLOCK, DimensionalIndustryBlocks::init);
        sided.registerRunnable(Registries.BLOCK_ENTITY_TYPE, DimensionalIndustryBlockEntities::init);
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    public static IDimensionalIndustrySided<?> getSided() {
        return INSTANCE.sided;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
