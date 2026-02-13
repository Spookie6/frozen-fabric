package dev.frozencloud.frozen.compat

import net.irisshaders.iris.api.v0.*

object IrisCompatibility {

    fun init() {
        IrisApi.getInstance().apply {
//            assignPipeline()
        }
    }
}