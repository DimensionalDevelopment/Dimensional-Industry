package org.dimdev.dimindustry.neoforge;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlocks;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = DimensionalIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DimensionalIndustryNeoForgeDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var pack = event.getGenerator().getPackOutput();
        var generator = event.getGenerator();
        var existing = event.getExistingFileHelper();


        generator.addProvider(event.includeClient(), new BlockStates(pack, existing));

        generator.addProvider(event.includeServer(), new LootTableProvider(pack, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
        ), event.getLookupProvider()));
    }

    private static class BlockLoot extends BlockLootSubProvider {
        private BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            dropSelf(CreateCompatBlocks.LIMINAL_COUPLING);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return List.of(CreateCompatBlocks.LIMINAL_COUPLING);
        }
    }

    private static class BlockStates extends BlockStateProvider {
        private BlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, DimensionalIndustry.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            directionalBlock(CreateCompatBlocks.LIMINAL_COUPLING, models().getExistingFile(modLoc("block/liminal_coupling")));
            simpleBlockItem(CreateCompatBlocks.LIMINAL_COUPLING, models().getExistingFile(modLoc("block/liminal_coupling")));
        }
    }
}
