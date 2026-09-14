package org.dimdev.dimindustry.common;

import org.dimdev.dimcore.api.ISided;

public interface IDimensionalIndustrySided<T extends IDimensionalIndustrySided<T>> extends ISided<T> {
    // Compat mods are loader-specific; the loader sided overrides this to wire them up.
    default void checkCompat() {
    }
}
