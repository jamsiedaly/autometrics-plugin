package dev.autometrics.plugin

import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class AutometricsLatency : AutometricsAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        val methodInfo = getMethodInformation(psiElement, e)
        val query = latencyQuery("function", "${methodInfo.className}.${methodInfo.methodName}")
        val url = makePrometheusUrl(query, "Function calls per minute")
        BrowserUtil.browse(url)
    }

    private fun latencyQuery(labelKey: String, labelValue: String): String {
        val latency = "sum by (le, function, module, commit, version) (rate(function_calls_duration_bucket{$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
        return """
        label_replace(histogram_quantile(0.99, $latency), "percentile_latency", "99", "", "")
        or
        label_replace(histogram_quantile(0.95, $latency), "percentile_latency", "95", "", "")
    """.trimIndent()
    }
}