package org.dimdev.dimindustry.items;

import org.dimdev.dimindustry.DimensionalIndustry;
import org.dimdev.dimindustry.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicItem extends Item {
	
	public BasicItem(String registryName) {
		setRegistryName(registryName);
		setUnlocalizedName(DimensionalIndustry.MODID + "." + registryName);
		setCreativeTab(ModItems.DIM_DOORS_CREATIVE_TAB);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
