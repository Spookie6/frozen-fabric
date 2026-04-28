package dev.frozencloud.infernum.compat

import dev.frozencloud.infernum.util.render.CustomRenderPipelines
import net.irisshaders.iris.api.v0.*

object IrisCompatibility {
    fun init() {
        IrisApi.getInstance().apply {
            assignPipeline(CustomRenderPipelines.LINE_LIST, IrisProgram.LINES)
            assignPipeline(CustomRenderPipelines.LINE_LIST_ESP, IrisProgram.LINES)
            assignPipeline(CustomRenderPipelines.TRIANGLE_STRIP, IrisProgram.BASIC)
            assignPipeline(CustomRenderPipelines.TRIANGLE_STRIP_ESP, IrisProgram.BASIC)
        }
    }
}