package org.dimdev.dimindustry;

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
	public static final String VERSION = "1.0.0";
	
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


}
