package org.dimdev.dimindustry.common.client;

import org.dimdev.dimcore.api.client.IClientSided;
import org.dimdev.dimcore.api.client.ModClient;

public interface IDimensionalIndustryClientSided<T extends IDimensionalIndustryClientSided<T>> extends IClientSided<T> {
    default void checkCompat() {}
}
