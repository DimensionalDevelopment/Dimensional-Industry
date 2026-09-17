package org.dimdev.dimindustry.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimindustry.neoforge.create.block.LiminalCouplingBlockEntity;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class LiminalCouplerBlockEntityRenderer extends KineticBlockEntityRenderer<LiminalCouplingBlockEntity> {

    private static final Map<Direction, ModelPart.Cube> CUBES = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction direction : Direction.values()) {
            map.put(direction, createCube(direction));
        }
    });

    public LiminalCouplerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LiminalCouplingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        var facing = be.getBlockState().getValue(DirectionalKineticBlock.FACING);

        DimensionalDoorsClient.detector.wrap(type -> renderModelWithPortalShader(CUBES.get(facing), ms, buffer, light, overlay));

        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
    }

    public static void renderModelWithPortalShader(ModelPart.Cube model, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, int overlay) {

        matrixStack.pushPose();
        try {
            DimensionalDoorsClient.detector.wrap(type -> model.compile(matrixStack.last(), vertexConsumerProvider.getBuffer(type), light, overlay, 0x000000));
        } finally {
            matrixStack.popPose();
        }
    }

    @Override
    protected SuperByteBuffer getRotatedModel(LiminalCouplingBlockEntity be, BlockState state) {
        Direction direction = state.getValue(DirectionalKineticBlock.FACING);
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, direction);
    }

    public static ModelPart.Cube createCube(Direction direction) {
        return createCube(direction, 0, 0);
    }

    public static ModelPart.Cube createCube(Direction direction, int texU, int texV) {
        float[] origin = {2, 2, 2};
        float[] size = {12, 12, 12};
        int axis = direction.getAxis().ordinal(); // X=0, Y=1, Z=2

        size[axis] = 11;
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            origin[axis] = 3;
        }

        return new ModelPart.Cube(texU, texV,
                origin[0], origin[1], origin[2],
                size[0], size[1], size[2],
                0, 0, 0, false, 16, 16,
                EnumSet.of(direction));
    }
}
