package dev.autometrics.plugin

import com.intellij.openapi.options.Configurable
import dev.autometrics.plugin.ConfigurationState.Companion.getInstance
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AutometricsConfigurable : Configurable {
    private var autometricsConfigurationForm: AutometricsConfigurationForm? = null
    private val settingsState: ConfigurationState = getInstance()

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Autometrics"
    }

    override fun createComponent(): JComponent {
        autometricsConfigurationForm = AutometricsConfigurationForm()
        return autometricsConfigurationForm!!.getMainPanel()
    }

    override fun isModified(): Boolean {
        return autometricsConfigurationForm!!.getMySetting() != settingsState.prometheusUrl
    }

    override fun apply() {
        settingsState.prometheusUrl = autometricsConfigurationForm!!.getMySetting()
    }

    override fun reset() {
        autometricsConfigurationForm!!.setMySetting(settingsState.prometheusUrl)
    }
}