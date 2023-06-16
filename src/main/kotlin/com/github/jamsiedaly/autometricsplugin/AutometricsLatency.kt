package com.github.jamsiedaly.autometricsplugin

import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.getPackage
import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class AutometricsLatency : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = ediTorRequiredData.caretModel
        val methodName = caretModel.currentCaret.selectedText
        val classPackage = getPackage(e.dataContext)

        val query = latencyQuery("function", methodName)
        val url = makePrometheusUrl(PROMETHEUS_URL, query, "Function calls per minute")
        BrowserUtil.browse(url)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = editor.caretModel
        val selectedText = caretModel.currentCaret.selectedText
        if (selectedText!!.isEmpty()) {
            e.presentation.isVisible = false
        }
    }

    fun latencyQuery(labelKey: String, labelValue: String?): String {
        val latency = "sum by (le, function, module, commit, version) (rate(function_calls_duration_bucket{$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
        return """
        label_replace(histogram_quantile(0.99, $latency), "percentile_latency", "99", "", "")
        or
        label_replace(histogram_quantile(0.95, $latency), "percentile_latency", "95", "", "")
    """.trimIndent()
    }
}