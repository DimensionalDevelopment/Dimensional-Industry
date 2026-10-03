package org.dimdev.dimindustry.neoforge.create

import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimindustry.common.DimensionalIndustry.getSided
import org.dimdev.dimindustry.neoforge.create.block.LiminalCouplingBlockEntity

object CreateCompatBlockEntityTypes : PlatformRegistry.BlockEntityTypePlatformRegistry(getSided()) {
    @JvmField
    val SLIDING_ENTRANCE_RIFT = create("sliding_entrance_rift", ::SlidingEntranceRiftBlockEntity)
    @JvmField val LIMINAL_COUPLING = create("liminal_coupling", ::LiminalCouplingBlockEntity)
}
