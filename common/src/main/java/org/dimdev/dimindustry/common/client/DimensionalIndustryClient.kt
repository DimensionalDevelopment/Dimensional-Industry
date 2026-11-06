package org.dimdev.dimindustry.common.client

import net.minecraft.client.renderer.RenderType
import org.dimdev.dimcore.DimCore
import org.dimdev.dimcore.api.client.EntityRendererProvider
import org.dimdev.dimcore.api.client.EntityRendererRegister
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities
import org.dimdev.dimindustry.common.client.render.HourGlassBlockEntityRenderer

object DimensionalIndustryClient : ModClient<IDimensionalIndustryClientSided<*>>, EntityRendererProvider {
    override fun init(sided: IDimensionalIndustryClientSided<*>) {
        this.sided = sided
    }

    override fun delayedInit() {
        DimCore.clientPlatform.register(RenderType.cutout(), DimensionalIndustryBlocks.HOURGLASS)
    }

    override fun registerEntityRenderers(register: EntityRendererRegister) {
        register.registerBlockEntity(DimensionalIndustryBlockEntities.HOURGLASS, ::HourGlassBlockEntityRenderer)
    }



    override val modId: String get() = "dimindustry"

    var sided: IDimensionalIndustryClientSided<*>? = null
            private set
}
