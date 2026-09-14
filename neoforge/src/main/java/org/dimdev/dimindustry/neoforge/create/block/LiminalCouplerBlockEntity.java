package org.dimdev.dimindustry.neoforge.create.block;

import com.mojang.serialization.DataResult;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimindustry.neoforge.create.CreateCompatBlockEntityTypes;
import org.dimdev.dimindustry.neoforge.create.target.EmptyKineticTarget;
import org.dimdev.dimindustry.neoforge.create.target.KineticTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LiminalCouplerBlockEntity extends GeneratingKineticBlockEntity implements Rift, KineticTarget {
    @NotNull
    protected RiftData data = new RiftData();
    protected boolean riftStateChanged;
    private boolean deleteRift = true;

    private float targetRotationalSpeed;
    private float targetStressCapacity;

    public LiminalCouplerBlockEntity(BlockPos pos, BlockState state) {
        this(CreateCompatBlockEntityTypes.LIMINAL_COUPLING, pos, state);
    }

    public LiminalCouplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(5);
    }

    @Override
    public void initialize() {
        super.initialize();

        if(getLevel().isClientSide()) return;
        syncTargetKinetics();
        updateGeneratedRotation();
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) {
            return;
        }

        syncTargetKinetics();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if (data.getDestination() != VirtualTarget.NoneTarget.INSTANCE && level instanceof ServerLevel serverLevel) {
            data.getDestination().setLocation(Location.ofWorld(serverLevel, worldPosition));
        }

        tag.put("data", RiftData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if (tag.contains("data")) {
            DataResult<RiftData> result = RiftData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("data"));
            data = result.result().map(RiftData::copy).orElseGet(RiftData::new);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if(!getLevel().isClientSide()) unregister();
    }

    @Override
    public float getGeneratedSpeed() {
        if (hasSource()) {
            return 0;
        }

        return targetRotationalSpeed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float speed = Math.abs(getGeneratedSpeed());
        lastCapacityProvided = speed == 0 ? 0 : targetStressCapacity / speed;
        return lastCapacityProvided;
    }

    @Override
    public float getStressCapacity() {
        return hasNetwork() && hasSource() && getSpeed() != 0 ? capacity : 0;
    }

    @Override
    public float getRotationalSpeed() {
        return hasSource() ? getSpeed() : 0;
    }

    private void syncTargetKinetics() {
        KineticTarget target = resolveTargetKinetics();
        float newSpeed = target.getRotationalSpeed();
        float newCapacity = target.getStressCapacity();

        if (targetRotationalSpeed == newSpeed && targetStressCapacity == newCapacity) {
            return;
        }

        targetRotationalSpeed = newSpeed;
        targetStressCapacity = newCapacity;
        updateGeneratedRotation();
        setChanged();
    }

    private KineticTarget resolveTargetKinetics() {
        Target target = getTarget();
        KineticTarget kineticTarget = target.as(KineticTarget.class);

        if (kineticTarget instanceof KineticBlockEntity kineticBlockEntity && isSameCreateNetwork(kineticBlockEntity)) {
            return EmptyKineticTarget.INSTANCE;
        }

        return kineticTarget;
    }

    private boolean isSameCreateNetwork(KineticBlockEntity other) {
        return level == other.getLevel()
                && hasNetwork()
                && other.hasNetwork()
                && Objects.equals(network, other.network);
    }

    public void setData(RiftData data) {
        this.data = data == null ? new RiftData() : data.copy();
    }

    public @NotNull RiftData getData() {
        return this.data;
    }

    @Override
    public void setDeleteRift(boolean deleteRift) {
        this.deleteRift = deleteRift;
    }

    @Override
    public boolean isDeleteRift() {
        return deleteRift;
    }

    @Override
    public boolean isStateDirty() {
        return riftStateChanged;
    }

    @Override
    public void setStateDirty(boolean riftState) {
        this.riftStateChanged = riftState;
    }

    @Override
    public BlockPos getRiftBlockPos() {
        return getBlockPos();
    }

    @Override
    public BlockState getRiftBlockState() {
        return getBlockState();
    }

    @Override
    public Level getRiftLevel() {
        return getLevel();
    }

    @Override
    public boolean isDetached() {
        return false;
    }

    @Override
    public void handleSourceMoved(Location location) {
        Rift.super.handleSourceMoved(location);
        syncTargetKinetics();
    }

    @Override
    public void handleTargetGone(Location location) {
        Rift.super.handleTargetGone(location);
        syncTargetKinetics();
    }

    @Override
    public void detach() {
        unregister();
    }

}