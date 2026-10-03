package org.dimdev.dimindustry.neoforge.create

import com.simibubi.create.api.contraption.BlockMovementChecks
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.TrapDoorBlock
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getDimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.api.rift.target.DefaultTargets.registerDefaultTarget
import org.dimdev.dimdoors.block.DoorSoundProvider
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar
import org.dimdev.dimindustry.common.DimensionalIndustry.id
import org.dimdev.dimindustry.neoforge.create.target.EmptyKineticTarget
import org.dimdev.dimindustry.neoforge.create.target.KineticTarget

object CreateCompat {
    fun init() {
        registerDefaultTarget(KineticTarget::class.java, EmptyKineticTarget)
        CreateCompatBlocks.register()
        CreateCompatBlockEntityTypes.register()

        BuiltInRegistries.BLOCK_ENTITY_TYPE.addAlias(
            ResourceLocation.fromNamespaceAndPath(
                DimensionalDoors.MOD_ID,
                "sliding_entrance_rift"
            ), id("sliding_entrance_rift")
        )

        BlockMovementChecks.registerMovementAllowedCheck { state, _, _ -> if (state.block is RiftProvider<*>) BlockMovementChecks.CheckResult.SUCCESS else BlockMovementChecks.CheckResult.PASS }
        BlockMovementChecks.registerMovementNecessaryCheck { state, _, _ -> if (state.block is RiftProvider<*>) BlockMovementChecks.CheckResult.SUCCESS else BlockMovementChecks.CheckResult.PASS }

        getDimensionalDoorBlockRegistrar().registerCustomDoorProduction(
            { _, block -> block is SlidingDoorBlock },
            object : DimensionalDoorBlockRegistrar.DoorProduction {
                override fun createDoor(
                    context: DimensionalDoorBlockRegistrar.GeneratedDoorContext,
                    originalBlock: DoorBlock
                ): Block? {
                    return SlidingDimensionalDoorBlock(context.properties, originalBlock as DoorSoundProvider)
                }

                override fun createTrapdoor(
                    context: DimensionalDoorBlockRegistrar.GeneratedDoorContext,
                    originalBlock: TrapDoorBlock
                ): Block? {
                    return null
                }

                override fun onBlockRegistered(
                    context: DimensionalDoorBlockRegistrar.GeneratedDoorContext,
                    generatedBlock: Block
                ) {
                    CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT.addBlock(generatedBlock)
                }
            })
    }
}
