package org.dimdev.dimindustry.neoforge.create.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.core.Direction;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes;

public class CreateCompatClient {
    public static void init() {
        if(DimCore.platform().isModLoaded("create")) {
            SimpleBlockEntityVisualizer.builder(CreateCompatBlockEntityTypes.LIMINAL_COUPLING).factory((ctx, blockEntity, partialTick) -> {
                var direction = blockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING);

                return new OrientedRotatingVisual<>(ctx, blockEntity, partialTick, Direction.SOUTH, direction, Models.partial(AllPartialModels.SHAFT_HALF));
            }).neverSkipVanillaRender().apply();
        }
    }
}
