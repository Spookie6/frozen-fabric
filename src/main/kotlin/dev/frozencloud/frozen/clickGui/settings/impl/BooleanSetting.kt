package dev.frozencloud.frozen.clickGui.settings.impl

import dev.frozencloud.frozen.clickGui.settings.Setting
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanSetting(override var default: Boolean, override var value: Boolean, name: String) : Setting<Boolean>(name) {
    override fun provideDelegate(
        thisRef: Module,
        property: KProperty<*>
    ): ReadWriteProperty<Module, Boolean> {
        TODO("Not yet implemented")
    }
}