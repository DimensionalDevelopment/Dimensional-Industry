package org.dimdev.dimindustry.neoforge.create;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.DefaultTargets;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimindustry.common.DimensionalIndustry;
import org.dimdev.dimindustry.common.IDimensionalIndustrySided;
import org.dimdev.dimindustry.neoforge.DimensionalIndustryNeoForge;
import org.dimdev.dimindustry.neoforge.create.target.EmptyKineticTarget;
import org.dimdev.dimindustry.neoforge.create.target.KineticTarget;

public final class CreateCompat {
    private CreateCompat() {
    }

    public static void init(IDimensionalIndustrySided<?> sided) {
        DefaultTargets.registerDefaultTarget(KineticTarget.class, EmptyKineticTarget.INSTANCE);
        sided.registerRunnable(Registries.BLOCK, CreateCompatBlocks::init);
        DimensionalIndustry.getSided().registerRunnable(Registries.BLOCK_ENTITY_TYPE, CreateCompatBlockEntityTypes::init);
        // Sliding entrance rifts lived in DimDoors before the compat moved here; keep old chunks loading.
        BuiltInRegistries.BLOCK_ENTITY_TYPE.addAlias(ResourceLocation.fromNamespaceAndPath(DimensionalDoors.MOD_ID, "sliding_entrance_rift"), DimensionalIndustry.id("sliding_entrance_rift"));

        BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) -> state.getBlock() instanceof RiftProvider<?> ? CheckResult.SUCCESS : CheckResult.PASS);
        BlockMovementChecks.registerMovementNecessaryCheck((state, world, pos) -> state.getBlock() instanceof RiftProvider<?> ? CheckResult.SUCCESS : CheckResult.PASS);

        DimensionalDoors.getDimensionalDoorBlockRegistrar().registerCustomDoorProduction((id, block) -> block instanceof SlidingDoorBlock, new DimensionalDoorBlockRegistrar.DoorProduction() {
            @Override
            public Block createDoor(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, DoorBlock originalBlock) {
                return new SlidingDimensionalDoorBlock(context.properties(), (DoorSoundProvider) originalBlock);
            }

            @Override
            public Block createTrapdoor(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, TrapDoorBlock originalBlock) {
                return null;
            }

            @Override
            public void onBlockRegistered(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, Block generatedBlock) {
                CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT.addBlock(generatedBlock);
            }
        });
    }
}
