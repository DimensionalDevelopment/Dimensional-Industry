package org.dimdev.dimindustry.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.client.ModClient;
import org.dimdev.dimindustry.common.block.DimensionalIndustryBlocks;
import org.dimdev.dimindustry.common.block.entity.DimensionalIndustryBlockEntities;
import org.dimdev.dimindustry.common.block.entity.HourGlassBlockEntity;

public class DimensionalIndustryClient implements ModClient<IDimensionalIndustryClientSided<?>> {
    @Override
    public void init(IDimensionalIndustryClientSided<?> sided) {

    }

    @Override
    public void delayedInit() {
        DimCore.clientPlatform().register(RenderType.cutout(), DimensionalIndustryBlocks.HOURGLASS);
    }

    @Override
    public void initBlockEntityRenderers(BlockEntityRegister register) {
        register.register(DimensionalIndustryBlockEntities.HOURGLASS, new BlockEntityRendererProvider<HourGlassBlockEntity>() {
            @Override
            public BlockEntityRenderer<HourGlassBlockEntity> create(Context context) {
                return new BlockEntityRenderer<HourGlassBlockEntity>() {
                    @Override
                    public void render(HourGlassBlockEntity hourGlassBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {

                    }
                };
            }
        });
    }

    @Override
    public String getModId() {
        return "dimindustry";
    }
}
