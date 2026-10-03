package org.dimdev.dimindustry.neoforge

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.recipe.ShapedTesselatingRecipe
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlocks
import java.util.concurrent.CompletableFuture

@EventBusSubscriber(modid = DimensionalIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DimensionalIndustryNeoForgeDataGen {
    @SubscribeEvent
    @JvmStatic
    fun gatherData(event: GatherDataEvent) {
        val pack = event.generator.packOutput
        val generator = event.generator
        val existing = event.existingFileHelper


        generator.addProvider(event.includeClient(), BlockStates(pack, existing))

        generator.addProvider(event.includeServer(), LootTableProvider(pack, mutableSetOf<ResourceKey<LootTable>>(), listOf(LootTableProvider.SubProviderEntry(::BlockLoot, LootContextParamSets.BLOCK)), event.lookupProvider))
        generator.addProvider(event.includeServer(), BlockTags(pack, event.lookupProvider, existing))
        generator.addProvider(event.includeServer(), Recipes(pack, event.lookupProvider))
    }

    private class Recipes(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
        RecipeProvider(output, registries) {
        override fun buildRecipes(output: RecipeOutput) {
            val pattern = ShapedRecipePattern.of(
                mapOf(
                    'S' to Ingredient.of("shaft".asCreateItem()),
                    'A' to Ingredient.of(ModItems.AMALGAM_LUMP),
                    'G' to Ingredient.of("andesite_casing".asCreateItem()),
                ),
                " S ", "AGA", " S "
            )
            output.accept(
                DimensionalIndustry.id("liminal_coupling"),
                ShapedTesselatingRecipe("", pattern, ItemStack(CreateCompatBlocks.LIMINAL_COUPLING), 200, true),
                null
            )
        }

        private fun String.asCreateItem(): Item =
            BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", this))
    }

    private class BlockLoot(registries: HolderLookup.Provider) :
        BlockLootSubProvider(mutableSetOf(), FeatureFlags.REGISTRY.allFlags(), registries) {
        override fun generate() {
            dropSelf(CreateCompatBlocks.LIMINAL_COUPLING)
        }

        override fun getKnownBlocks(): Iterable<Block?> {
            return listOf(CreateCompatBlocks.LIMINAL_COUPLING)
        }
    }

    private class BlockStates(output: PackOutput, existingFileHelper: ExistingFileHelper) :
        BlockStateProvider(output, DimensionalIndustry.modId, existingFileHelper) {
        override fun registerStatesAndModels() {
            directionalBlock(
                CreateCompatBlocks.LIMINAL_COUPLING,
                models().getExistingFile(modLoc("block/liminal_coupling"))
            )
            // builtin/entity routes the item to LiminalCouplingItemRenderer; display copied from block/block
            itemModels().getBuilder("liminal_coupling")
                .parent(ModelFile.UncheckedModelFile("builtin/entity"))
                .transforms()
                .transform(ItemDisplayContext.GUI).rotation(30f, 225f, 0f).scale(0.625f).end()
                .transform(ItemDisplayContext.GROUND).translation(0f, 3f, 0f).scale(0.25f).end()
                .transform(ItemDisplayContext.FIXED).scale(0.5f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75f, 45f, 0f).translation(0f, 2.5f, 0f).scale(0.375f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(75f, 45f, 0f).translation(0f, 2.5f, 0f).scale(0.375f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0f, 45f, 0f).scale(0.4f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0f, 225f, 0f).scale(0.4f).end()
                .end()
        }
    }

    private class BlockTags(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>,
        existingFileHelper: ExistingFileHelper?
    ) : BlockTagsProvider(output, lookupProvider, DimensionalIndustry.modId, existingFileHelper) {
        override fun addTags(provider: HolderLookup.Provider) {
            this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(CreateCompatBlocks.LIMINAL_COUPLING)
            this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(CreateCompatBlocks.LIMINAL_COUPLING)
        }
    }
}
