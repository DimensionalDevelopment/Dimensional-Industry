package org.dimdev.dimindustry.neoforge.create

import net.minecraft.core.BlockPos
import org.dimdev.dimdoors.api.util.Location

interface DimDoorsCreateContraption {
    fun `dimdoors$getTrackedRifts`(): MutableMap<BlockPos, Location>
}
