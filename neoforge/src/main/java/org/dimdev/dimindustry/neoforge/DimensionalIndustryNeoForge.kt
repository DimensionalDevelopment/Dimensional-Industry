package org.dimdev.dimindustry.neoforge

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.NeoForgeSided
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.common.IDimensionalIndustrySided
import org.dimdev.dimindustry.neoforge.create.CreateCompat

@Mod(DimensionalIndustry.MOD_ID)
class DimensionalIndustryNeoForge(bus: IEventBus) :
    NeoForgeSided<DimensionalIndustryNeoForge, DimensionalIndustry>(bus, DimensionalIndustry),
    IDimensionalIndustrySided<DimensionalIndustryNeoForge> {
    override fun checkCompat() {
        if (platform.isModLoaded("create")) {
            CreateCompat.init()
        }
    }
}
