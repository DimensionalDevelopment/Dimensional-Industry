package org.dimdev.dimindustry.neoforge.create

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.item.PlaceOnlyOnRiftBlockItem
import org.dimdev.dimindustry.common.DimensionalIndustry.getSided
import org.dimdev.dimindustry.neoforge.create.block.LiminaCouplerBlock

object CreateCompatBlocks : PlatformRegistry.BlockItemPlatformRegistry(getSided()) {
    val LIMINAL_COUPLING = createWithItem("liminal_coupling") {
        block(::LiminaCouplerBlock)
        blockProperties(Blocks.IRON_BLOCK) {
            strength(5.0f, 6.0f)
            noOcclusion()
            sound(SoundType.METAL)
            requiresCorrectToolForDrops()
        }
        item(::PlaceOnlyOnRiftBlockItem)
    }.also {
        ModItems.DIMENSIONAL_DOORS.add { it }
    }

    fun register() {
        CreateCompatBlockEntityTypes.LIMINAL_COUPLING.addBlock(LIMINAL_COUPLING)
    }
}