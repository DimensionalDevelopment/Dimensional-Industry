package org.dimdev.dimindustry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.items.*;

@Mod.EventBusSubscriber(modid = DimensionalIndustry.MODID)
public final class ModItems {
    // Dimensional Core Items
    @ObjectHolder("dimdoors:world_thread") public static final Item WORLD_THREAD = null;
    @ObjectHolder("dimdoors:stable_fabric") public static final Item STABLE_FABRIC = null;
    @ObjectHolder("dimdoors:rift_connection_tool") public static final ItemRiftConfigurationTool RIFT_CONNECTION_TOOL = null;
    @ObjectHolder("dimdoors:rift_blade") public static final ItemRiftBlade RIFT_BLADE = null;
    @ObjectHolder("dimdoors:rift_remover") public static final ItemRiftRemover RIFT_REMOVER = null;
    @ObjectHolder("dimdoors:rift_signature") public static final ItemRiftSignature RIFT_SIGNATURE = null;
    @ObjectHolder("dimdoors:stabilized_rift_signature") public static final ItemStabilizedRiftSignature STABILIZED_RIFT_SIGNATURE = null;

    @ObjectHolder("dimdoors:fabric") public static final ItemBlock FABRIC = null;
    @ObjectHolder("dimdoors:ancient_fabric") public static final ItemBlock ANCIENT_FABRIC = null;

    // Creative Tab
    public static final CreativeTabs DIM_DOORS_CREATIVE_TAB = new CreativeTabs("dimensional_industry_creative_tab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.ENDER_PEARL); // TODO: Change icon once we have a dimindustry item
        }
    };

    // Dimensional Industry items

    // Register Items
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                // ITEM_1
                // ITEM_2
                // ...
        );
    }
    
    // Register Item Models
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
    	// item1.initModel();
    	// item2.initModel();
    }
}
