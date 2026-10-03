package org.dimdev.dimindustry.neoforge.create.block

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.foundation.block.IBE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.block.CustomBreakHandling
import org.dimdev.dimdoors.block.ModBlocks.DETACHED_RIFT
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes

class LiminaCouplerBlock(properties: Properties) : DirectionalKineticBlock(properties), IBE<LiminalCouplingBlockEntity>, RiftProvider<LiminalCouplingBlockEntity>, CustomBreakHandling {
    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH))
    }

    public override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.block()
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        if (!context.level.getBlockState(context.clickedPos).`is`(DETACHED_RIFT)) {
            return null
        }

        val preferred = getPreferredFacing(context)
        if (preferred != null && (context.player == null || !context.player!!.isShiftKeyDown)) {
            return defaultBlockState().setValue(FACING, preferred)
        }

        return super.getStateForPlacement(context)
    }

    override fun customDestroy(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        flags: Int,
        recursionLeft: Int
    ): Boolean? {
        val rift = getRift(level, pos, state) ?: return null

        rift.detach()
        return true
    }

    override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) {
        val rift = getRift(level, pos, state)
        if (rift == null) {
            super.onBlockExploded(state, level, pos, explosion)
            return
        }

        rift.detach()
    }

    override fun hasShaftTowards(level: LevelReader?, pos: BlockPos?, state: BlockState, face: Direction?): Boolean {
        return face == state.getValue(FACING)
    }

    override fun getRotationAxis(state: BlockState): Direction.Axis {
        return state.getValue(FACING).axis
    }

    override fun hideStressImpact(): Boolean {
        return true
    }

    override fun isPathfindable(state: BlockState, pathComputationType: PathComputationType): Boolean {
        return false
    }

    override fun getBlockEntityClass(): Class<LiminalCouplingBlockEntity> {
        return LiminalCouplingBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<LiminalCouplingBlockEntity> {
        return CreateCompatBlockEntityTypes.LIMINAL_COUPLING
    }

    override val riftBlockEnityType: BlockEntityType<LiminalCouplingBlockEntity>
        get() = blockEntityType

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return super<IBE>.newBlockEntity(pos, state)
    }

    override fun providerType(): String {
        return "Liminal Coupling"
    }
}
