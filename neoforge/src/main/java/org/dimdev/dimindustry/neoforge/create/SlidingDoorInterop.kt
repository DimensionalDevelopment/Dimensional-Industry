package org.dimdev.dimindustry.neoforge.create

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.material.Fluids

object SlidingDoorInterop {
    @JvmStatic
    fun getOtherDoorPos(pos: BlockPos, hinge: DoorHingeSide?, facing: Direction): BlockPos {
        return pos.relative(if (hinge == DoorHingeSide.LEFT) facing.clockWise else facing.counterClockWise)
    }

    fun isCompatibleDoubleDoor(
        state: BlockState,
        hinge: DoorHingeSide?,
        facing: Direction?,
        otherDoor: BlockState
    ): Boolean {
        return hasDoorProperties(state)
                && haveCompatibleBlocks(state, otherDoor)
                && hasDoorProperties(otherDoor)
                && otherDoor.getValue(DoorBlock.HINGE) != hinge
                && otherDoor.getValue(DoorBlock.FACING) == facing
                && otherDoor.getValue(DoorBlock.OPEN) != state.getValue(DoorBlock.OPEN)
                && otherDoor.getValue(DoorBlock.HALF) == state.getValue(DoorBlock.HALF)
    }

    @JvmStatic
    fun isMixedDoubleDoor(
        state: BlockState,
        hinge: DoorHingeSide?,
        facing: Direction?,
        otherDoor: BlockState
    ): Boolean {
        return areCounterparts(state.block, otherDoor.block)
                && isCompatibleDoubleDoor(state, hinge, facing, otherDoor)
    }

    @JvmStatic
    fun setOpen(state: BlockState, open: Boolean, hideForAnimation: Boolean): BlockState {
        var state = state
        state = state.setValue(DoorBlock.OPEN, open)
        if ((open || hideForAnimation) && state.hasProperty(SlidingDoorBlock.VISIBLE)) {
            state = state.setValue(SlidingDoorBlock.VISIBLE, false)
        }
        return state
    }

    fun setPoweredOpen(state: BlockState, powered: Boolean): BlockState {
        var state = state
        state = setOpen(state.setValue(DoorBlock.POWERED, powered), powered, false)
        return state
    }

    @JvmStatic
    fun scheduleWaterTickIfNeeded(level: Level, pos: BlockPos, state: BlockState) {
        if (!level.isClientSide && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
    }

    private fun haveCompatibleBlocks(state: BlockState, otherDoor: BlockState): Boolean {
        val block = state.block
        val otherBlock = otherDoor.block
        return block === otherBlock || areCounterparts(block, otherBlock)
    }

    private fun areCounterparts(block: Block?, otherBlock: Block?): Boolean {
        if (block is SlidingDimensionalDoorBlock && block.originalBlock === otherBlock) {
            return true
        }
        return otherBlock is SlidingDimensionalDoorBlock && otherBlock.originalBlock === block
    }

    private fun hasDoorProperties(state: BlockState): Boolean {
        return state.hasProperty(DoorBlock.HINGE)
                && state.hasProperty(DoorBlock.FACING)
                && state.hasProperty(DoorBlock.OPEN)
                && state.hasProperty(DoorBlock.HALF)
    }
}
