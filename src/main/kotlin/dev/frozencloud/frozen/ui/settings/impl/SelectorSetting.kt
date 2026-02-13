package dev.frozencloud.frozen.ui.settings.impl

import com.google.gson.*
import dev.frozencloud.frozen.ui.settings.RenderableSetting
import dev.frozencloud.frozen.ui.settings.Saving
import java.lang.reflect.Type

class SelectorSetting(
    name: String,
    override var default: String,
    private var options: List<String>,
    desc: String
    ) : RenderableSetting<String>(name, desc), Saving {

    override var value: String = default

    private var index: Int = optionIndex(default)
        set(value) {
            field = if (value > options.size -1) 0 else if (value < 0) options.size - 1 else value
        }

    private var selected: String
        get() = options[index]
        set(value) {
            index = optionIndex(value)
        }

    override fun render(x: Float, y: Float, right: Float, mouseX: Float, mouseY: Float): Float {
        return super.render(x, y, right, mouseX, mouseY)
    }


    override fun write(gson: Gson): JsonElement = JsonPrimitive(selected)

    override fun read(element: JsonElement, gson: Gson) {
        element.asString?.let { selected = it }
    }

    private fun optionIndex(string: String): Int =
        options.map { it.lowercase() }.indexOf(string.lowercase()).coerceIn(0, options.size - 1)
}