package org.dimdev.dimindustry.neoforge.create

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SlidingEntranceRiftBlockEntity(type: BlockEntityType<SlidingEntranceRiftBlockEntity>, pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<SlidingEntranceRiftBlockEntity>(type, pos, state) {
    private var animation: Float
    private var previousAnimation: Float
    var bridgeTicks: Int = 0
    var deferUpdate: Boolean = false

    constructor(pos: BlockPos, state: BlockState) : this(CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT, pos, state)

    init {
        this.animation = if (isOpen(state)) 1f else 0f
        this.previousAnimation = this.animation
    }

    override fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        if (deferUpdate && !level.isClientSide()) {
            deferUpdate = false
            val currentState = getBlockState()
            currentState.handleNeighborChanged(level, worldPosition, Blocks.AIR, worldPosition, false)
        }

        super.tick(level, pos, blockState)

        val open: Boolean = isOpen(getBlockState())
        val target = (if (open) 1 else 0).toFloat()
        val wasSettled = isAnimationSettled(target)
        previousAnimation = animation
        animation = chase(animation, target)

        if (level.isClientSide()) {
            if (bridgeTicks < 2 && open) {
                bridgeTicks++
            } else if (bridgeTicks > 0 && !open && isVisible(getBlockState())) {
                bridgeTicks--
            }
            return
        }

        if (!open && isAnimationSettled(target) && !isVisible(getBlockState())) {
            showBlockModel(!wasSettled)
        }
    }

    fun getAnimation(partialTicks: Float): Float {
        return Mth.lerp(partialTicks, previousAnimation, animation)
    }

    fun shouldRenderSliding(state: BlockState): Boolean {
        return !isVisible(state) || bridgeTicks != 0
    }

    private fun isVisible(state: BlockState): Boolean {
        return state.getOptionalValue(SlidingDoorBlock.VISIBLE).getOrNull() ?: true
    }

    private fun showBlockModel(playSound: Boolean) {
        if (level != null) {
            val state = blockState
            if (!state.hasProperty(SlidingDoorBlock.VISIBLE)) { return }

            level!!.setBlock(worldPosition, state.setValue(SlidingDoorBlock.VISIBLE, true), Block.UPDATE_ALL)
            if (playSound) {
                level!!.playSound(null, worldPosition, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, .5f, 1f)
            }
        }
    }

    private fun isAnimationSettled(target: Float): Boolean {
        return abs(animation - target) < 1.0E-4f
    }

    companion object {
        private const val ANIMATION_STEP = .15f

        private fun isOpen(state: BlockState): Boolean {
            return state.getOptionalValue(DoorBlock.OPEN).getOrNull() ?: false
        }

        private fun chase(value: Float, target: Float): Float {
            if (value < target) {
                return min(target, value + ANIMATION_STEP)
            }
            if (value > target) {
                return max(target, value - ANIMATION_STEP)
            }
            return value
        }
    }
}
