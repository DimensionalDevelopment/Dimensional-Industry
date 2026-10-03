package org.dimdev.dimindustry.common.block

import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.DimCore
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimcore.api.transfer.TransferType
import org.dimdev.dimdoors.block.ModBlocks.DRIFTWOOD_PLANKS
import org.dimdev.dimdoors.item.PlaceOnlyOnRiftBlockItem
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities
import org.dimdev.dimindustry.common.block.entity.FluidTransferRiftBlockEntity
import java.util.function.Function

object DimensionalIndustryBlocks : PlatformRegistry.BlockItemPlatformRegistry(DimensionalIndustry.getSided()) {
    @JvmField
    val HOURGLASS: Block = createWithItem("hourglass") {
        block(::HourGlassBlock)
        blockProperties(DRIFTWOOD_PLANKS) {
            noOcclusion()
        }
        item(::PlaceOnlyOnRiftBlockItem)
    }


    @JvmStatic
    fun register() {
        DimensionalIndustryBlockEntities.HOURGLASS.addBlock(HOURGLASS)

        DimCore.transfer.declare(TransferType.FLUID, DimensionalIndustryBlockEntities.HOURGLASS)
        TransferType.FLUID.expose.register({ blockEntity, side ->
            if(blockEntity?.level?.isClientSide == true) return@register null

            val handle = blockEntity?.castOrNull<FluidTransferRiftBlockEntity<*>>()?.handle(side)

            return@register handle
        })
    }
}
