package org.dimdev.dimindustry.fabric;


import org.dimdev.dimcore.FabricSided;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.IDimensionalIndustrySided;

public class DimensionalIndustryFabric extends FabricSided<DimensionalIndustryFabric, DimensionalIndustry> implements IDimensionalIndustrySided<DimensionalIndustryFabric> {
    public DimensionalIndustryFabric() {
        super(new DimensionalIndustry());
    }
}