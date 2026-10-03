package org.dimdev.dimindustry.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimcore.api.entity.MutableBlockEntityType
import org.dimdev.dimindustry.common.DimensionalIndustry
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks
import java.util.function.BiFunction

object DimensionalIndustryBlockEntities : PlatformRegistry.BlockEntityTypePlatformRegistry(DimensionalIndustry.getSided()) {
    @JvmField
    val HOURGLASS = create("hourglass", ::HourGlassBlockEntity)
}
