package org.dimdev.dimindustry;

import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.BlockFabricAncient;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;
import org.dimdev.dimindustry.blocks.RiftPlaceableBlock;
import org.dimdev.dimindustry.items.RiftPlaceableItemBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
    @ObjectHolder("dimindustry:rifttesseract") public static final RiftPlaceableBlock RIFT_TESSERACT = new RiftPlaceableBlock(Material.ANVIL, "rifttesseract", false);
    
    // Register Blocks
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                RIFT_TESSERACT
                // BLOCK_2
                // ...
        );
    }
    
    // Register Item Blocks
    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new RiftPlaceableItemBlock(RIFT_TESSERACT.getRegistryName().toString(), RIFT_TESSERACT)
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
