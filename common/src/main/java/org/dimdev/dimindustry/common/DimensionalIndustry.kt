package org.dimdev.dimindustry.common

import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.ModCommon
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities

object DimensionalIndustry : ModCommon<IDimensionalIndustrySided<out IDimensionalIndustrySided<*>>> {
    private lateinit var sided: IDimensionalIndustrySided<*>

    override fun init(sided: IDimensionalIndustrySided<out IDimensionalIndustrySided<*>>) {
        this.sided = sided

        sided.checkCompat()

        DimensionalIndustryBlocks.register()
        DimensionalIndustryBlockEntities.register()
    }

    override val modId: String = MOD_ID

    const val MOD_ID = "dimindustry"

    @JvmStatic
    fun getSided(): IDimensionalIndustrySided<*> = sided

    @JvmStatic
    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(modId, path)
    }
}
