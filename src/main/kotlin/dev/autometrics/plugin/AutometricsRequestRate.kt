package dev.autometrics.plugin

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl


class AutometricsRequestRate : AutometricsAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        val methodInfo = getMethodInformation(psiElement, e)
        val query = requestRateQuery("function", "${methodInfo.className}.${methodInfo.methodName}")
        val url = makePrometheusUrl(query, "Function calls per minute")
        BrowserUtil.browse(url)
    }

    private fun requestRateQuery(labelKey: String, labelValue: String): String {
        return "sum by (function, module, commit, version) (rate({__name__=~\"function_calls?(_total)?\",$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
    }

    data class MethodInformation(
        val methodName: String,
        val className: String,
        val classPackage: String
    )
}