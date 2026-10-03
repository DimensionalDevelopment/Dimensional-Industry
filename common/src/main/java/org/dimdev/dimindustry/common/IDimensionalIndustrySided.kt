package org.dimdev.dimindustry.common

import org.dimdev.dimcore.api.ISided

interface IDimensionalIndustrySided<T : IDimensionalIndustrySided<T>> : ISided<T> {
    // Compat mods are loader-specific; the loader sided overrides this to wire them up.
    fun checkCompat() {
    }
}
