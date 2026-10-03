package org.dimdev.dimindustry.neoforge.create.target

import org.dimdev.dimdoors.api.rift.target.Target

interface KineticTarget : Target {
    val stressCapacity: Float
    val rotationalSpeed: Float
}