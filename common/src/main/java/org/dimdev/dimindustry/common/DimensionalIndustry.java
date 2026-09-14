package org.dimdev.dimindustry.common;

import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimcore.api.ModCommon;

public class DimensionalIndustry implements ModCommon<IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>>> {
    public static final String MOD_ID = "dimindustry";
    public static final DimensionalIndustry INSTANCE = new DimensionalIndustry();

    private IDimensionalIndustrySided<?> sided;

    @Override
    public void init(IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>> sided) {
        this.sided = sided;

        sided.checkCompat();
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
