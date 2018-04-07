package org.dimdev.dimindustry.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DirectionalBlock extends BasicBlock {
	
	private boolean isFullBlock;
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	public DirectionalBlock(Material material, String registryName, boolean isFullBlock) {
		super(material, registryName);
		this.isFullBlock = isFullBlock;
		
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return isFullBlock;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return isFullBlock;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, placer)), 2);
	}
	
    public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entity) {
        return EnumFacing.getFacingFromVector(
             (float) (entity.posX - clickedBlock.getX()),
             (float) (entity.posY - clickedBlock.getY()),
             (float) (entity.posZ - clickedBlock.getZ()));
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(FACING).getIndex();
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
    	return new BlockStateContainer(this, FACING);
    }

}
