package org.dimdev.dimindustry.fabric

import org.dimdev.dimcore.FabricSided
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.common.IDimensionalIndustrySided


class DimensionalIndustryFabric : FabricSided<DimensionalIndustryFabric, DimensionalIndustry>(DimensionalIndustry), IDimensionalIndustrySided<DimensionalIndustryFabric> {
    override fun onInitialize() {
        super.onInitialize()
    }
}
