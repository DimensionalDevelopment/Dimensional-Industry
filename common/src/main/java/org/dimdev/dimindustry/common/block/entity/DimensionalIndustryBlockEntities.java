package org.dimdev.dimindustry.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimcore.api.entity.MutableBlockEntityType;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks;

import java.util.function.BiFunction;

public class DimensionalIndustryBlockEntities {
    public static final MutableBlockEntityType<HourGlassBlockEntity> HOURGLASS = register("hourglass", HourGlassBlockEntity::new);

    public static void init() {
        HOURGLASS.addBlock(DimensionalIndustryBlocks.HOURGLASS);
    }

    public static  <T extends BlockEntity> MutableBlockEntityType<T> register(String name, BiFunction<BlockPos, BlockState, T> function) {
        return DimensionalIndustry.getSided().registerBlockEntityType(name, MutableBlockEntityType.Builder.create(function::apply).build());

    }
}
