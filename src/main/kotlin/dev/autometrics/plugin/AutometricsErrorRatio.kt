package dev.autometrics.plugin

import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class AutometricsErrorRatio : AutometricsAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        val methodInfo = getMethodInformation(psiElement, e)
        val query = errorRatioQuery("function", "${methodInfo.className}.${methodInfo.methodName}")
        val url = makePrometheusUrl(query, "Function calls per minute")
        BrowserUtil.browse(url)
    }

    private fun errorRatioQuery(labelKey: String, labelValue: String): String {
        val requestRate = requestRateQuery(labelKey, labelValue)
        return "(sum by (function, module, commit, version) (rate({__name__=~\"function_calls(_total)?\",$labelKey=\"$labelValue\",result=\"error\"}[5m]) $BUILD_INFO)) / ($requestRate)"
    }

    private fun requestRateQuery(labelKey: String, labelValue: String): String {
        return "sum by (function, module, commit, version) (rate({__name__=~\"function_calls?(_total)?\",$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
    }
}