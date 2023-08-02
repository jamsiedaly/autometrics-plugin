package dev.autometrics.plugin

import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class AutometricsConfigurationForm {
    private val mainPanel = JPanel()
    private val textField = JTextField(30)
    private val title = JLabel("Please enter the url of you Prometheus server")

    init {
        mainPanel.add(title)
        mainPanel.add(textField)
    }

    fun getMainPanel(): JPanel {
        return mainPanel
    }

    fun getMySetting(): String {
        return textField.text
    }

    fun setMySetting(newSetting: String) {
        textField.text = newSetting
    }
}