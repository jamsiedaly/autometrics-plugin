package dev.autometrics.plugin

import dev.autometrics.plugin.AutometricsIntellij.getPackage
import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class AutometricsConcurrentCalls : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = ediTorRequiredData.caretModel
        val methodName = caretModel.currentCaret.selectedText
        val classPackage = getPackage(e.dataContext)

        val query = concurrentCallsQuery("function", "$classPackage.$methodName")
        val url = makePrometheusUrl(PROMETHEUS_URL, query, "Concurrent function calls")
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

    fun concurrentCallsQuery(labelKey: String, labelValue: String?): String {
        return "sum by (function, module, commit, version) (function_calls_concurrent{$labelKey=\"$labelValue\"} $BUILD_INFO)"
    }

}