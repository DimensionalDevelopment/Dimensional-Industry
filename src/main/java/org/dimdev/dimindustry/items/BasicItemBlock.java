package org.dimdev.dimindustry.items;

import org.dimdev.dimindustry.DimensionalIndustry;
import org.dimdev.dimindustry.ModItems;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class BasicItemBlock extends ItemBlock {
	
	public BasicItemBlock(String registryName, Block block) {
		super(block);
		setRegistryName(registryName);
		setUnlocalizedName(DimensionalIndustry.MODID + "." + registryName);
		setCreativeTab(ModItems.DIM_DOORS_CREATIVE_TAB);
	}
}
