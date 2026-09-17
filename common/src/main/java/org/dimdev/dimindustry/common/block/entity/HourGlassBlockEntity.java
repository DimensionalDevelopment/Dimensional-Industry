package org.dimdev.dimindustry.common.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimcore.api.transfer.Handle;
import org.dimdev.dimdoors.api.rift.target.FluidTarget;
import org.dimdev.dimdoors.block.entity.CodecRecord;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HourGlassBlockEntity extends FluidTransferRiftBlockEntity<HourGlassBlockEntity> {
    public static final Codec<FluidUnit> FLUID_UNIT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidUnit::fluid),
            DataComponentPatch.CODEC.fieldOf("components").forGetter(FluidUnit::components),
            Codec.LONG.fieldOf("amount").forGetter(FluidUnit::amount)
    ).apply(instance, FluidUnit::new));

    private static final long CAPACITY = 4 * DimCore.platform().bucketAmount();

    public static final CodecRecord<HourGlassBlockEntity, FluidUnit> BUFFER = new CodecRecord<>("buffer", FLUID_UNIT_CODEC, hourGlassBlock -> hourGlassBlock.buffer);

    public static final FluidUnit EMPTY = new FluidUnit(Fluids.EMPTY, 0);

    private @Nonnull FluidUnit buffer = EMPTY;

    public HourGlassBlockEntity(BlockPos pos, BlockState state) {
        super(DimensionalIndustryBlockEntities.HOURGLASS, pos, state);
    }

    @Override
    public long insert(FluidUnit unit, boolean simulate) {
        long done = fill(unit, simulate);
        if (done < unit.amount()) {
            done += getNeighborHandle(Direction.DOWN).insert(unit.withAmount(unit.amount() - done), simulate);
        }
        return done;
    }

    @Override
    public long extract(FluidUnit unit, boolean simulate) {
        long done = drain(unit, simulate);
        if (done < unit.amount()) {
            done += getNeighborHandle(Direction.UP).extract(unit.withAmount(unit.amount() - done), simulate);
        }
        return done;
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

    private long fill(FluidUnit unit, boolean simulate) {
        if (!buffer.isEmpty() && !buffer.sameResource(unit)) {
            return 0;
        }
        long have = buffer.amount();
        long accepted = Math.min(unit.amount(), CAPACITY - have);
        if (accepted > 0 && !simulate) {
            buffer = unit.withAmount(have + accepted);
            setChanged();
        }
        return Math.max(0, accepted);
    }

    private long drain(FluidUnit unit, boolean simulate) {
        if (!buffer.sameResource(unit)) {
            return 0;
        }
        long taken = Math.min(unit.amount(), buffer.amount());
        if (taken > 0 && !simulate) {
            buffer = taken == buffer.amount() ? EMPTY : buffer.withAmount(buffer.amount() - taken);
            setChanged();
        }
        return taken;
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

    // ---- local role: neighbours and buckets ----

    @Override
    public Handle<FluidUnit> handle(@Nullable Direction direction) {
        return switch (direction) {
            case UP -> insertOnly;
            case DOWN -> extractOnly;
            case null -> throughRift; // a held bucket: not a pipe, so it may fill or drain
            default -> null;
        };
    }

    /** Local first, then the far end. */
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
