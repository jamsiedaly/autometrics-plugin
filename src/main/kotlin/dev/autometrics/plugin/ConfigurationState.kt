package dev.autometrics.plugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.xmlb.XmlSerializerUtil

class ConfigurationState : PersistentStateComponent<ConfigurationState> {
    var prometheusUrl = "http://localhost:9090"
    override fun getState(): ConfigurationState {
        return this
    }

    override fun loadState(state: ConfigurationState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): ConfigurationState {
            var settingsState = ApplicationManager.getApplication().getServiceIfCreated(
                ConfigurationState::class.java
            )
            if (settingsState == null) {
                settingsState = ConfigurationState()
                ApplicationManager.getApplication().registerServiceInstance(
                    ConfigurationState::class.java,
                    settingsState)
            }
            return settingsState
        }
    }
}