package org.dimdev.dimindustry.blocks;

import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.dimindustry.DimensionalIndustry;
import org.dimdev.dimindustry.ModBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RiftPlaceableBlock extends DirectionalBlock {
	
	public RiftPlaceableBlock(Material material, String registryName, boolean isFullBlock) {
		super(material, registryName, isFullBlock);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		System.out.println(world.getBlockState(pos).getBlock().equals(ModBlocks.RIFT));
		return world.getBlockState(pos).getBlock().equals(ModBlocks.RIFT);
		//return super.canPlaceBlockAt(world, pos);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(!hasTileEntity(state)) return;
		TileEntityEntranceRift rift = (TileEntityEntranceRift) world.getTileEntity(pos);
		super.breakBlock(world, pos, state);
		if (world.isRemote) return;
		if (rift == null)
			DimensionalIndustry.logger.error("Rift tile entity was null when breaking block at " + new Location(world, pos) + ", please report this error.");
		if (rift.isLeaveScarWhenClosed() || rift.isRegistered() && RiftRegistry.instance().getSources(new Location(rift.getWorld(), rift.getPos())).size() > 1 && !rift.isAlwaysDelete()) {
			world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
			TileEntityFloatingRift newRift = (TileEntityFloatingRift) world.getTileEntity(pos);
			newRift.copyFrom(rift);
			newRift.updateType();
		} else
			rift.unregister();
	}

}
