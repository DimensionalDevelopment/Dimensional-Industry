package org.dimdev.dimindustry.fabric

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.Util
import net.minecraft.data.DataProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import org.dimdev.dimdoors.datagen.AbstractLanguageProvider
import org.dimdev.dimindustry.common.DimensionalIndustry.id

class DimensionalIndustryDataGen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider<DataProvider> { output, registriesFuture ->
            object : AbstractLanguageProvider(output, registriesFuture, "en_us") {
                override fun generateTranslations() {
                    block("liminal_coupling", "Liminal Coupling")
                }

                fun block(id: String, name: String?) {
                    val langId = Util.makeDescriptionId("block", id(id))

                    builder.add(langId, name)
                }
            }
        }
        pack.addProvider<ModelProvider>(::ModelProvider)

        pack.addProvider<DataProvider> { output, registriesFuture ->
            object : FabricBlockLootTableProvider(output, registriesFuture) {
                override fun generate() {
                }
            }
        }
    }

    private class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
        override fun generateBlockStateModels(generators: BlockModelGenerators?) {
        }

        override fun generateItemModels(generators: ItemModelGenerators?) {
        }
    }
}
