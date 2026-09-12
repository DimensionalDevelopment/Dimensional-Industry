package org.dimdev.dimindustry.common;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimcore.api.ModCommon;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.IDimensionalDoorsSided;

import java.util.function.Consumer;

public class DimensionalIndustry implements ModCommon<IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>>> {
    public static final String MOD_ID = "dimindustry";

    @Override
    public void init(IDimensionalIndustrySided<? extends IDimensionalIndustrySided<?>> sided) {
        sided.onPlayerJoin(new Consumer<ServerPlayer>() {
            @Override
            public void accept(ServerPlayer player) {
                player.sendSystemMessage(Component.literal("Merp"));
            }
        });
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
