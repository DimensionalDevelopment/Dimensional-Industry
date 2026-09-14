package org.dimdev.dimindustry.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.dimdev.dimcore.NeoForgeSided;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.IDimensionalIndustrySided;
import org.dimdev.dimindustry.neoforge.create.CreateCompat;

@Mod(DimensionalIndustry.MOD_ID)
public class DimensionalIndustryNeoForge extends NeoForgeSided<DimensionalIndustryNeoForge, DimensionalIndustry> implements IDimensionalIndustrySided<DimensionalIndustryNeoForge> {
    public DimensionalIndustryNeoForge(IEventBus bus) {
        super(bus, DimensionalIndustry.INSTANCE);
    }

    @Override
    public void checkCompat() {
        if (isModLoaded("create")) {
            CreateCompat.init(this);
        }
    }
}
