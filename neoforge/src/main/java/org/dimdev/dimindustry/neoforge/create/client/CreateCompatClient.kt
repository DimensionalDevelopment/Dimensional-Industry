package org.dimdev.dimindustry.neoforge.create.client

import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.model.Models
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.core.Direction
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes

object CreateCompatClient {
    fun init() {
        if (platform.isModLoaded("create")) {
            SimpleBlockEntityVisualizer.builder(CreateCompatBlockEntityTypes.LIMINAL_COUPLING)
                .factory { ctx, blockEntity, partialTick: Float ->
                    val direction = blockEntity.blockState.getValue(DirectionalKineticBlock.FACING)
                    OrientedRotatingVisual(ctx, blockEntity, partialTick, Direction.SOUTH, direction, Models.partial(AllPartialModels.SHAFT_HALF))
                }.neverSkipVanillaRender().apply()
        }
    }
}
