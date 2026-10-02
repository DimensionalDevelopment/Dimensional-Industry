package org.dimdev.dimindustry.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimcore.api.transfer.Handle;
import org.dimdev.dimdoors.api.rift.target.FluidTarget;
import org.dimdev.dimdoors.block.entity.CodecRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HourGlassBlockEntity extends FluidTransferRiftBlockEntity<HourGlassBlockEntity> {
    private static final long CAPACITY = 4 * DimCore.platform().bucketAmount();

    public static final CodecRecord<HourGlassBlockEntity, FluidUnit> BUFFER = new CodecRecord<>("buffer", FluidUnit.CODEC, FluidUnit.empty(), HourGlassBlockEntity::getBuffer);

    private @Nonnull FluidUnit buffer = FluidUnit.empty();

    public HourGlassBlockEntity(BlockPos pos, BlockState state) {
        super(DimensionalIndustryBlockEntities.HOURGLASS, pos, state);
    }

    public @NotNull FluidUnit getBuffer() {
        return buffer;
    }

    public float getFillFraction() {
        return (float) buffer.amount() / CAPACITY;
    }

    @Override
    public long insert(FluidUnit unit, boolean simulate) {
        if (!buffer.isEmpty() && !buffer.sameResource(unit)) {
            return 0;
        }
        long have = buffer.amount();
        long accepted = Math.min(unit.amount(), CAPACITY - have);
        if (accepted > 0 && !simulate) {
            buffer = unit.withAmount(have + accepted);
            sync();
        }

        accepted = Math.max(0, accepted);

        if (accepted < unit.amount()) {
            accepted += getNeighborHandle(Direction.DOWN).insert(unit.withAmount(unit.amount() - accepted), simulate);
        }
        return accepted;
    }

    @Override
    public long extract(FluidUnit unit, boolean simulate) {
        if (!buffer.sameResource(unit)) {
            return 0;
        }
        long taken = Math.min(unit.amount(), buffer.amount());
        if (taken > 0 && !simulate) {
            buffer = taken == buffer.amount() ? FluidUnit.empty() : buffer.withAmount(buffer.amount() - taken);
            sync();
        }

        if (taken < unit.amount()) {
            taken += getNeighborHandle(Direction.UP).extract(unit.withAmount(unit.amount() - taken), simulate);
        }
        return taken;
    }

    @Override
    public List<FluidUnit> contents() {
        List<FluidUnit> contents = new ArrayList<>();
        if (!buffer.isEmpty()) {
            contents.add(buffer);
        }
        contents.addAll(getNeighborHandle(Direction.UP).contents());
        return contents;
    }

    @Override
    public void serialize(Serialize<Tag, HourGlassBlockEntity> serialize) {
        super.serialize(serialize);
        serialize.put(BUFFER);
    }

    @Override
    public void deserialize(Deserialize<Tag> nbt) {
        super.deserialize(nbt);
        buffer = nbt.get(BUFFER);
    }

    @Override
    public Handle<FluidUnit> handle(@Nullable Direction direction) {
        return switch (direction) {
            case UP -> insertOnly;
            case DOWN -> extractOnly;
            case null -> throughRift;
            default -> null;
        };
    }

    private final Handle<FluidUnit> throughRift = new Handle<>() {
        @Override
        public long insert(FluidUnit unit, boolean simulate) {
            long done = HourGlassBlockEntity.this.insert(unit, simulate);
            FluidTarget far = remote();
            if (done < unit.amount() && far != null) {
                done += far.insert(unit.withAmount(unit.amount() - done), simulate);
            }
            return done;
        }

        @Override
        public long extract(FluidUnit unit, boolean simulate) {
            long done = HourGlassBlockEntity.this.extract(unit, simulate);
            FluidTarget far = remote();
            if (done < unit.amount() && far != null) {
                done += far.extract(unit.withAmount(unit.amount() - done), simulate);
            }
            return done;
        }

        @Override
        public List<FluidUnit> contents() {
            List<FluidUnit> contents = new ArrayList<>(HourGlassBlockEntity.this.contents());
            FluidTarget far = remote();
            if (far != null) {
                contents.addAll(far.contents());
            }
            return contents;
        }
    };

    private final Handle<FluidUnit> insertOnly = new Handle<>() {
        @Override
        public long insert(FluidUnit unit, boolean simulate) {
            return throughRift.insert(unit, simulate);
        }

        @Override
        public long extract(FluidUnit unit, boolean simulate) {
            return 0;
        }

        @Override
        public List<FluidUnit> contents() {
            return List.of();
        }
    };

    private final Handle<FluidUnit> extractOnly = new Handle<>() {
        @Override
        public long insert(FluidUnit unit, boolean simulate) {
            return 0;
        }

        @Override
        public long extract(FluidUnit unit, boolean simulate) {
            return throughRift.extract(unit, simulate);
        }

        @Override
        public List<FluidUnit> contents() {
            return throughRift.contents();
        }
    };
}
