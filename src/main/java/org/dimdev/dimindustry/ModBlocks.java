package org.dimdev.dimindustry;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.dimdev.dimdoors.shared.blocks.BlockFabric;
import org.dimdev.dimdoors.shared.blocks.BlockFabricAncient;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;

@Mod.EventBusSubscriber(modid = DimensionalIndustry.MODID)
public final class ModBlocks {
    // Dimensional Core blocks
    @ObjectHolder("dimdoors:rift") public static final BlockFloatingRift RIFT = null;
    @ObjectHolder("dimdoors:fabric") public static final BlockFabric FABRIC = null;
    @ObjectHolder("dimdoors:ancient_fabric") public static final BlockFabricAncient ANCIENT_FABRIC = null;

    // Dimensional Industry blocks

    // Register blocks
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                // BLOCK_1
                // BLOCK_2
                // ...
        );
    }
}
