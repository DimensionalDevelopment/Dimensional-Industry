package org.dimdev.dimindustry.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import org.dimdev.dimdoors.datagen.AbstractLanguageProvider;
import org.dimdev.dimdoors.datagen.BlockLootTableProvider;
import org.dimdev.dimindustry.common.DimensionalIndustry;

import java.util.concurrent.CompletableFuture;

public class DimensionalIndustryDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(new FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>() {
            @Override
            public DataProvider create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
                return new AbstractLanguageProvider(output, registriesFuture, "en_us") {
                    @Override
                    protected void generateTranslations() {
                        block("liminal_coupling", "Liminal Coupling");
                    }

                    private void block(String id, String name) {
                        var langId = Util.makeDescriptionId("block", DimensionalIndustry.id(id));

                        builder.add(langId, name);
                    }
                };
            }
        });
        pack.addProvider(ModelProvider::new);

        pack.addProvider(new FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>() {
            @Override
            public DataProvider create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
                return new FabricBlockLootTableProvider(output, registriesFuture) {

                    @Override
                    public void generate() {

                    }
                }
            }
        })
    }

    private static class ModelProvider extends FabricModelProvider {
        private ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generators) {
        }

        @Override
        public void generateItemModels(ItemModelGenerators generators) {
        }

    }
}
