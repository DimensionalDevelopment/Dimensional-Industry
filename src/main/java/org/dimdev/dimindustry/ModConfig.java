package org.dimdev.dimindustry;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.common.config.Config.*;

@Config(modid = DimensionalIndustry.MODID, name = DimensionalIndustry.MODID, category = "")
@Mod.EventBusSubscriber(modid = DimensionalIndustry.MODID)
public final class ModConfig {

    public static General general = new General();

    public static class General {
        @Name("useStatusBar")
        @LangKey("dimdoors.general.useStatusBar")
        public boolean useStatusBar = true;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DimensionalIndustry.MODID)) {
            ConfigManager.sync(event.getModID(), Type.INSTANCE);
        }
    }
}