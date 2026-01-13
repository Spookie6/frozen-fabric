package dev.frozencloud.frozen.clickGui.settings

import dev.frozencloud.frozen.Frozen
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Setting<T>(
    val name: String,
    val description: String = ""
) : ReadWriteProperty<Module, T>, PropertyDelegateProvider<Module, ReadWriteProperty<Module, T>> {

    abstract var default: T
    abstract var value: T
    private var hidden = false

    fun hide(): Setting<T> {
        hidden = true
        return this
    }

    protected var visibilityDependency: (() -> Boolean)? = null

    open fun reset() {
        value = default
    }

    val isVisible: Boolean get() = (visibilityDependency?.invoke() ?: true) && !hidden

    override operator fun getValue(thisRef: Module, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: Module, property: KProperty<*>, value: T) {
        this.value = value
    }

    companion object {
        val JSON = Frozen.JSON

        fun <K : Setting<T>, T> K.withDependency(dependency: () -> Boolean): K {
            visibilityDependency = dependency
            return this
        }
    }
}