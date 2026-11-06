package org.dimdev.dimindustry.neoforge.create

import com.simibubi.create.content.contraptions.StructureTransform
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.Location.Companion.ofWorld
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.registry.RiftRegistry

object CreateRiftMovement {
    @JvmStatic
    fun hasBlockingRiftTarget(
        level: ServerLevel,
        trackedRifts: MutableMap<BlockPos, Location>,
        transform: StructureTransform
    ): Boolean {
        if (trackedRifts.isEmpty()) {
            return false
        }

        val movingSources = trackedRifts.values.toSet()
        val registry = RiftRegistry.instance

        trackedRifts.forEach { (pos, location) ->
            val source: Location = location
            val target = ofWorld(level, transform.apply(pos))

            if (source == target) return@forEach

            if (movingSources.contains(target)) return@forEach

            val targetState = level.getBlockState(target.blockPos)
            if (stateContainsRift(targetState) || registry.isRiftAt(target)) {
                return true
            }
        }

        return false
    }

    @JvmStatic
    fun moveTrackedRifts(
        level: ServerLevel,
        trackedRifts: MutableMap<BlockPos, Location>,
        transform: StructureTransform
    ) {
        if (trackedRifts.isEmpty()) {
            return
        }

        val registry = RiftRegistry.instance
        val registryMoves = hashMapOf<Location, Location>()

        trackedRifts.forEach { (pos, location) ->
            val source = location
            val targetPos = transform.apply(pos)
            val target = ofWorld(level, targetPos)

            if (registry.isRiftAt(source)) {
                registryMoves[source] = target
            }
        }

        registry.moveRifts(registryMoves)

        trackedRifts.forEach { (pos, location) ->
            val targetPos = transform.apply(pos)
            val blockEntity = level.getBlockEntity(targetPos)
            if (blockEntity is Rift<*>) {
                if (!registry.isRiftAt(ofWorld(level, targetPos))) {
                    blockEntity.register()
                }
                blockEntity.updateType()
            }
        }
    }

    fun stateContainsRift(state: BlockState): Boolean {
        return state.block.castOrNull<RiftProvider<*>>()?.stateContainsRift(state) == true
    }
}
