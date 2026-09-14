package org.dimdev.dimindustry.neoforge.create.target;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.jetbrains.annotations.Nullable;

public enum EmptyKineticTarget implements KineticTarget {
    INSTANCE;

    @Override
    public float getStressCapacity() {
        return 0;
    }

    @Override
    public float getRotationalSpeed() {
        return 0;
    }

    @Override
    public @Nullable Target receiveOther() {
        return null;
    }
}
