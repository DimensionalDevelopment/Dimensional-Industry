package org.dimdev.dimindustry.neoforge.create.target

import org.dimdev.dimdoors.api.rift.target.Target

object EmptyKineticTarget : KineticTarget {

    override val stressCapacity: Float
        get() = 0f

    override val rotationalSpeed: Float
        get() = 0f

    override fun receiveOther(): Target? {
        return null
    }
}
