package org.dimdev.dimindustry.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimcore.api.client.IClientSided;
import org.jetbrains.annotations.Nullable;

public interface IDimensionalIndustryClientSided<T extends IDimensionalIndustryClientSided<T>> extends IClientSided<T> {
    default void checkCompat() {}
}
