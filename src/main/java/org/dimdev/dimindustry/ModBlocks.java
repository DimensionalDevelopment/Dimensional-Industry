package org.dimdev.dimindustry;

import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.BlockFabricAncient;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DimensionalIndustry.MODID)
public final class ModBlocks {
    // Dimensional Core Blocks
    @ObjectHolder("dimdoors:rift") public static final BlockFloatingRift RIFT = null;
    @ObjectHolder("dimdoors:fabric") public static final BlockFabric FABRIC = null;
    @ObjectHolder("dimdoors:ancient_fabric") public static final BlockFabricAncient ANCIENT_FABRIC = null;

    // Dimensional Industry Blocks

    // Register Blocks
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                // BLOCK_1
                // BLOCK_2
                // ...
        );
    }
    
    // Register Item Blocks
    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                // ITEM_1
                // ITEM_2
                // ...
        );
    }
    
    // Register Block Models
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
    	// block1.initModel();
    	// block2.initModel();
    }
}
