package org.dimdev.dimindustry.neoforge.create;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.neoforge.create.block.LiminaCouplerBlock;

import static net.minecraft.world.level.block.Blocks.IRON_BLOCK;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS;

public final class CreateCompatBlocks {
    public static final LiminaCouplerBlock LIMINAL_COUPLING = register("liminal_coupling", new LiminaCouplerBlock(ofFullCopy(IRON_BLOCK).strength(5.0F, 6.0F).noOcclusion().sound(SoundType.METAL).requiresCorrectToolForDrops()));

    private static <T extends Block> T register(String name, T block) {
        var sided = DimensionalIndustry.getSided();
        var registeredBlock = sided.register(Registries.BLOCK, name, block);
        var item = sided.register(Registries.ITEM, name, new LiminalCouplerBlockItem(registeredBlock, new Item.Properties()));
        sided.appendStack(DIMENSIONAL_DOORS, item.getDefaultInstance());
        return registeredBlock;
    }

    public static void init() {
        CreateCompatBlockEntityTypes.LIMINAL_COUPLING.addBlock(LIMINAL_COUPLING);
    }

}