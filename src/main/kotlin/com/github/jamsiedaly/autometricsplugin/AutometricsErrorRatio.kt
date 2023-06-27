package com.github.jamsiedaly.autometricsplugin

import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.getPackage
import com.github.jamsiedaly.autometricsplugin.AutometricsPlugin.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class AutometricsErrorRatio : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = ediTorRequiredData.caretModel
        val methodName = caretModel.currentCaret.selectedText
        val classPackage = getPackage(e.dataContext)

        val query = errorRatioQuery("function", methodName)
        val url = makePrometheusUrl(PROMETHEUS_URL, query, "Function error rate")
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

    private fun errorRatioQuery(labelKey: String, labelValue: String?): String {
        val requestRate = requestRateQuery(labelKey, labelValue)
        return "(sum by (function, module, commit, version) (rate({__name__=~\"function_calls(_count)?(_total)?\",$labelKey=\"$labelValue\",result=\"error\"}[5m]) $BUILD_INFO)) / ($requestRate)"
    }

    private fun requestRateQuery(labelKey: String?, labelValue: String?): String? {
        return "sum by (function, module, commit, version) (rate({__name__=~\"function_calls(_count)?(_total)?\",$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
    }
}