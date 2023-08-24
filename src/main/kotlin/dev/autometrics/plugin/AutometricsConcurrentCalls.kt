package dev.autometrics.plugin

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl


class AutometricsConcurrentCalls : AutometricsAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        val methodInfo = getMethodInformation(psiElement, e)
        val query = concurrentCallsQuery("function", "${methodInfo.classPackage}.${methodInfo.className}.${methodInfo.methodName}")
        val url = makePrometheusUrl(query, "Function calls per minute")
        BrowserUtil.browse(url)
    }

    fun concurrentCallsQuery(labelKey: String, labelValue: String?): String {
        return "sum by (function, module, commit, version) (function_calls_concurrent{$labelKey=\"$labelValue\"} $BUILD_INFO)"
    }

}