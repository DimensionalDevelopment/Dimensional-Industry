package org.dimdev.dimindustry.items;

import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.dimindustry.ModBlocks;
import org.dimdev.dimindustry.blocks.RiftPlaceableBlock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RiftPlaceableItemBlock extends BasicItemBlock {
	
	public RiftPlaceableItemBlock(String registryName, RiftPlaceableBlock block) {
		super(registryName, block);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!(world.getBlockState(pos).getBlock().equals(ModBlocks.RIFT))) {
			if(world.isRemote) {
				DimDoors.chat(player, "chat.place.not_on_rift");
				TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
			}
			return EnumActionResult.FAIL;
		}
		if(world.isRemote) {
			return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
		}
		
		TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(pos);
		rift.setUnregisterDisabled(true);
		
		EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
		System.out.println(result.name());
		if (result == EnumActionResult.SUCCESS) {
			TileEntityEntranceRift newRift = (TileEntityEntranceRift) world.getTileEntity(pos);
			newRift.copyFrom(rift);
			newRift.updateType();
		} else if(rift != null) {
			rift.setUnregisterDisabled(false);
		}
		return result;
	}

}
