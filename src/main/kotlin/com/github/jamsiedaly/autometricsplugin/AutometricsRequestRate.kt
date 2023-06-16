package com.github.jamsiedaly.autometricsplugin

import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.getPackage
import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class AutometricsRequestRate : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = ediTorRequiredData.caretModel
        val methodName = caretModel.currentCaret.selectedText
        val classPackage = getPackage(e.dataContext)

        val query = requestRateQuery("function", methodName)
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

    fun requestRateQuery(labelKey: String?, labelValue: String?): String? {
        return "sum by (function, module, commit, version) (rate({__name__=~\"function_calls(_count)?(_total)?\",$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
    }
}