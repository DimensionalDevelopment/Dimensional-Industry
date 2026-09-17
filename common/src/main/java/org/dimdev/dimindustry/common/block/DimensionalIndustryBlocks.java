package org.dimdev.dimindustry.common.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.dimdev.dimcore.api.transfer.TransferType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.PlaceOnlyOnRiftBlockItem;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities;
import org.dimdev.dimindustry.common.block.entity.FluidTransferRiftBlockEntity;

import java.util.function.Function;

import static org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS;

public class DimensionalIndustryBlocks {
    public static final Block HOURGLASS = register("hourglass", new HourGlassBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.DRIFTWOOD_PLANKS)), block -> new PlaceOnlyOnRiftBlockItem(block, new Item.Properties()));

    public static void init() {
        DimensionalIndustryBlockEntities.HOURGLASS.addBlock(HOURGLASS);
        TransferType.FLUID.declare(DimensionalIndustryBlockEntities.HOURGLASS);
        TransferType.FLUID.expose.register((blockEntity, side) -> {
            if (blockEntity instanceof FluidTransferRiftBlockEntity<?> fluidBlockEntity && !fluidBlockEntity.getLevel().isClientSide()) {
                return fluidBlockEntity.handle(side);
            } else {
                return null;
            }
        });
    }

    public static <T extends Block> T register(String name, T block, Function<Block, BlockItem> blockItemFunction) {
        var sided = DimensionalIndustry.getSided();
        var supplier = sided.register(Registries.BLOCK, name, block);
        var item = sided.register(Registries.ITEM, name, blockItemFunction.apply(block));
        sided.appendStack(DIMENSIONAL_DOORS, item.getDefaultInstance());

        return supplier;
    }
}
