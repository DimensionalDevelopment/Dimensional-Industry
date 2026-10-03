package org.dimdev.dimindustry.common.client

import org.dimdev.dimcore.api.client.IClientSided

interface IDimensionalIndustryClientSided<T : IDimensionalIndustryClientSided<T>> : IClientSided<T> {
    fun checkCompat() {}
}
