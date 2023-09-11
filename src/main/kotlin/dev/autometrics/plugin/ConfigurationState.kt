package dev.autometrics.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Storage("settings.xml")
class ConfigurationState : PersistentStateComponent<ConfigurationState> {
    var prometheusUrl = "http://localhost:9090/promotheus"
    override fun getState(): ConfigurationState {
        return this
    }

    override fun loadState(state: ConfigurationState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {

        private var instance: ConfigurationState? = null

        fun getInstance(): ConfigurationState {
            if (instance == null) {
                instance = ConfigurationState()
                return instance!!
            } else {
                return instance!!
            }
        }
    }
}