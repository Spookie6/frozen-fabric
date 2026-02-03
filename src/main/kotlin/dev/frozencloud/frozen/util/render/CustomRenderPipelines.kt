package dev.frozencloud.frozen.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.frozencloud.frozen.Frozen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation


object CustomRenderPipelines {
    @JvmStatic
    val FILLED_THROUGH_WALLS: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(*arrayOf<RenderPipeline.Snippet?>(RenderPipelines.LINES_SNIPPET))
            .withLocation(ResourceLocation.fromNamespaceAndPath(Frozen.MOD_ID, "pipeline/debug_filled_box_through_walls"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    )
}