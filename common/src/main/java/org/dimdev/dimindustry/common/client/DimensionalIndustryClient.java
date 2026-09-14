package org.dimdev.dimindustry.common.client;

import org.dimdev.dimcore.api.client.ModClient;

public class DimensionalIndustryClient implements ModClient<IDimensionalIndustryClientSided<?>> {
    @Override
    public void init(IDimensionalIndustryClientSided<?> sided) {

    }

    @Override
    public String getModId() {
        return "dimindustry";
    }
}
