package org.dimdev.dimindustry.neoforge.create;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.dimdev.dimcore.api.entity.MutableBlockEntityType;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.neoforge.create.block.LiminalCouplerBlockEntity;

import java.util.stream.Stream;

public final class CreateCompatBlockEntityTypes {
    public static final MutableBlockEntityType<SlidingEntranceRiftBlockEntity> SLIDING_ENTRANCE_RIFT = registerMutable("sliding_entrance_rift", SlidingEntranceRiftBlockEntity::new);
    public static final MutableBlockEntityType<LiminalCouplerBlockEntity> LIMINAL_COUPLING = registerMutable("liminal_coupling", LiminalCouplerBlockEntity::new);


    private static <E extends BlockEntity> MutableBlockEntityType<E> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
        return DimensionalIndustry.getSided().registerBlockEntityType(id, MutableBlockEntityType.Builder.create(factory, Stream.of(blocks).toArray(Block[]::new)).build());
    }

    public static void init() {
    }
}
