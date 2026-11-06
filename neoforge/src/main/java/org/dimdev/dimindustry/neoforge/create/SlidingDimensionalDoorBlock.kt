package org.dimdev.dimindustry.neoforge.create

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorShapes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.block.DoorSoundProvider
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.block.entity.Rift

class SlidingDimensionalDoorBlock(settings: Properties, originalBlock: DoorSoundProvider) : DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock<SlidingEntranceRiftBlockEntity>(settings, originalBlock) {
    val isFoldingDoor: Boolean
        get() = (originalBlock as SlidingDoorBlock).isFoldingDoor()

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.ENTITYBLOCK_ANIMATED
    }

    /*    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SlidingDoorBlock.VISIBLE);
    }*/
    public override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        if (!state.getValue(OPEN) && state.getValue(SlidingDoorBlock.VISIBLE)) {
            return super.getShape(state, level, pos, context)
        }

        val direction = state.getValue(FACING)
        val hinge = state.getValue(HINGE) == DoorHingeSide.RIGHT
        return SlidingDoorShapes.get(
            direction, hinge,
            this.isFoldingDoor
        )
    }

    override fun getInteractionShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): VoxelShape {
        return getShape(blockState, blockGetter, blockPos, CollisionContext.empty())
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val stateForPlacement = super.getStateForPlacement(ctx)
        if (stateForPlacement != null && stateForPlacement.getValue(OPEN)) {
            return stateForPlacement.setValue(OPEN, false).setValue(POWERED, false)
        }
        return stateForPlacement
    }


    public override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        isMoving: Boolean
    ) {
        if (!oldState.`is`(this)) deferUpdate(level, pos)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val updatedState = super.updateShape(state, direction, neighborState, world, pos, neighborPos)
        if (updatedState.isAir) {
            return updatedState
        }
        if (!updatedState.`is`(this)) {
            return updatedState
        }

        val half = updatedState.getValue(HALF)
        if (direction.axis === Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return if (neighborState.`is`(this) && neighborState.getValue(HALF) != half)
                updatedState.setValue(SlidingDoorBlock.VISIBLE, neighborState.getValue(SlidingDoorBlock.VISIBLE))
            else
                Blocks.AIR.defaultBlockState()
        }

        return updatedState
    }

    override fun setOpen(entity: Entity?, level: Level, state: BlockState, pos: BlockPos, open: Boolean) {
        if (!state.`is`(this) || state.getValue(OPEN) == open) {
            return
        }

        var changedState = state.setValue(OPEN, open)

        if (open) {
            changedState = changedState.setValue(SlidingDoorBlock.VISIBLE, false)
        }

        level.setBlock(pos, changedState, UPDATE_CLIENTS or UPDATE_IMMEDIATE)

        val hinge = changedState.getValue(HINGE)
        val facing = changedState.getValue(FACING)
        val otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing)
        val otherDoor = level.getBlockState(otherPos)

        if (SlidingDoorInterop.isCompatibleDoubleDoor(changedState, hinge, facing, otherDoor)) {
            if (otherDoor.`is`(this)) {
                setOpen(entity, level, otherDoor, otherPos, open)
            } else {
                val changedOtherDoor = SlidingDoorInterop.setOpen(otherDoor, open, open)
                level.setBlock(otherPos, changedOtherDoor, UPDATE_CLIENTS or UPDATE_IMMEDIATE)
                SlidingDoorInterop.scheduleWaterTickIfNeeded(level, otherPos, changedOtherDoor)
            }
        }

        this.playSound(entity, level, pos, open)
        level.gameEvent(entity, if (open) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)
    }

    public override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        val lower = state.getValue(HALF) == DoubleBlockHalf.LOWER
        val powered: Boolean = isDoorPowered(level, pos, state)
        if (defaultBlockState().`is`(block) || powered == state.getValue(POWERED)) {
            return
        }

        val blockEntity = getSlidingBlockEntity(level, if (lower) pos else pos.below())
        if (blockEntity != null && blockEntity.deferUpdate) {
            return
        }

        var changedState = state.setValue(POWERED, powered).setValue(OPEN, powered)
        if (powered) {
            changedState = changedState.setValue(SlidingDoorBlock.VISIBLE, false)
        }

        if (powered != state.getValue(OPEN)) {
            this.playSound(null, level, pos, powered)
            level.gameEvent(null, if (powered) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)

            val hinge = changedState.getValue<DoorHingeSide>(HINGE)
            val facing = changedState.getValue<Direction>(FACING)
            val otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing)
            var otherDoor = level.getBlockState(otherPos)

            if (SlidingDoorInterop.isCompatibleDoubleDoor(changedState, hinge, facing, otherDoor)) {
                otherDoor = SlidingDoorInterop.setPoweredOpen(otherDoor, powered)
                level.setBlock(otherPos, otherDoor, UPDATE_CLIENTS)
                SlidingDoorInterop.scheduleWaterTickIfNeeded(level, otherPos, otherDoor)
            }
        }

        level.setBlock(pos, changedState, UPDATE_CLIENTS)
        SlidingDoorInterop.scheduleWaterTickIfNeeded(level, pos, changedState)
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        var state = state
        state = state.cycle(OPEN)
        val open = state.getValue(OPEN)
        if (open) {
            state = state.setValue(SlidingDoorBlock.VISIBLE, false)
        }
        world.setBlock(pos, state, UPDATE_CLIENTS or UPDATE_IMMEDIATE)
        SlidingDoorInterop.scheduleWaterTickIfNeeded(world, pos, state)
        world.gameEvent(player, if (isOpen(state)) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)

        val hinge = state.getValue(HINGE)
        val facing = state.getValue(FACING)
        val otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing)
        val otherDoor = world.getBlockState(otherPos)
        if (SlidingDoorInterop.isCompatibleDoubleDoor(state, hinge, facing, otherDoor)) {
            if (otherDoor.`is`(this)) {
                useWithoutItem(otherDoor, world, otherPos, player, hitResult)
            } else {
                val changedOtherDoor = SlidingDoorInterop.setOpen(otherDoor, open, open)
                world.setBlock(otherPos, changedOtherDoor, UPDATE_CLIENTS or UPDATE_IMMEDIATE)
                SlidingDoorInterop.scheduleWaterTickIfNeeded(world, otherPos, changedOtherDoor)
                if (open) {
                    this.playSound(player, world, pos, true)
                    world.gameEvent(player, GameEvent.BLOCK_OPEN, pos)
                }
            }
        } else if (open) {
            this.playSound(player, world, pos, true)
            world.gameEvent(player, GameEvent.BLOCK_OPEN, pos)
        }

        return InteractionResult.sidedSuccess(world.isClientSide)
    }

    override fun postTraverseEffect(level: Level, pos: BlockPos, state: BlockState, rift: Rift<*>) {
        super.postTraverseEffect(level, pos, state, rift)
    }


    override fun closeDoorBehind(world: Level, pos: BlockPos) {
        val state = world.getBlockState(pos)
        if (!state.`is`(this)) {
            return
        }

        val closedState = state.setValue(OPEN, false)
            .setValue(SlidingDoorBlock.VISIBLE, false)
        world.setBlock(pos, closedState, UPDATE_ALL)
        SlidingDoorInterop.scheduleWaterTickIfNeeded(world, pos, closedState)
        closeOtherDoorBehind(world, pos, closedState)
    }

//    override fun playSound(source: Entity?, level: Level, pos: BlockPos, isOpening: Boolean) {
//        level.playSound(
//            source,
//            pos,
//            if (isOpening) this.type().doorOpen() else this.type().doorClose(),
//            SoundSource.BLOCKS,
//            1.0f,
//            level.getRandom().nextFloat() * 0.1f + 0.9f
//        )
//    }

    private fun closeOtherDoorBehind(level: Level, pos: BlockPos, closedState: BlockState) {
        val hinge = closedState.getValue<DoorHingeSide>(HINGE)
        val facing = closedState.getValue<Direction>(FACING)
        val otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing)
        val otherDoor = level.getBlockState(otherPos)

        if (!SlidingDoorInterop.isCompatibleDoubleDoor(closedState, hinge, facing, otherDoor)) {
            return
        }

        val closedOtherDoor = SlidingDoorInterop.setOpen(otherDoor, false, true)
        level.setBlock(otherPos, closedOtherDoor, UPDATE_ALL)
        SlidingDoorInterop.scheduleWaterTickIfNeeded(level, otherPos, closedOtherDoor)
    }

    override val riftBlockEnityType: BlockEntityType<SlidingEntranceRiftBlockEntity>
        get() = CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT

    private fun deferUpdate(level: LevelAccessor?, pos: BlockPos) {
        if (level is Level) {
            val blockEntity = getSlidingBlockEntity(level, pos)
            if (blockEntity != null) {
                blockEntity.deferUpdate = true
            }
        }
    }

    private fun getSlidingBlockEntity(level: Level, pos: BlockPos): SlidingEntranceRiftBlockEntity? {
        return level.getBlockEntity<SlidingEntranceRiftBlockEntity?>(
            pos,
            CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT
        )
            .orElse(null)
    }

    companion object {
        private fun isDoorPowered(level: Level, pos: BlockPos, state: BlockState): Boolean {
            val lower = state.getValue(HALF) == DoubleBlockHalf.LOWER
            val hinge = state.getValue(HINGE)
            val facing = state.getValue(FACING)
            val otherPos = SlidingDoorInterop.getOtherDoorPos(pos, hinge, facing)
            val otherDoor = level.getBlockState(otherPos)

            if (SlidingDoorInterop.isCompatibleDoubleDoor(
                    state.cycle(OPEN),
                    hinge,
                    facing,
                    otherDoor
                ) && (level.hasNeighborSignal(otherPos)
                        || level.hasNeighborSignal(otherPos.relative(if (lower) Direction.UP else Direction.DOWN)))
            ) {
                return true
            }

            return level.hasNeighborSignal(pos)
                    || level.hasNeighborSignal(pos.relative(if (lower) Direction.UP else Direction.DOWN))
        }
    }
}
