package org.dimdev.dimindustry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimindustry.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DimensionalIndustry.MODID, name = DimensionalIndustry.MODNAME, version = DimensionalIndustry.VERSION, useMetadata = true)
public class DimensionalIndustry {

    public static final String MODID = "dimindustry";
    public static final String MODNAME = "Dimensional Industry";
    public static final String VERSION = "${version}";
    public static final String DEPENDENCIES = "required-after:forge@[14.21.0.2320,);required-after:dimdoors";

    @SidedProxy(clientSide = "org.dimdev.dimindustry.proxy.ClientProxy", serverSide = "org.dimdev.dimindustry.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Instance
    public static DimensionalIndustry instance;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static void sendTranslatedMessage(Entity entity, String text, Object... translationArgs) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            player.sendStatusMessage(new TextComponentTranslation(text, translationArgs), ModConfig.general.useStatusBar);
        }
    }

    public static void chat(Entity entity, String text, Object... translationArgs) {
        entity.sendMessage(new TextComponentTranslation(text, translationArgs));
    }
}
