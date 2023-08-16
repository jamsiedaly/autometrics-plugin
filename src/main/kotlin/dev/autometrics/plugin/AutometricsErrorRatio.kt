package dev.autometrics.plugin

import dev.autometrics.plugin.AutometricsIntellij.getPackage
import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class AutometricsErrorRatio : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (psiElement is PsiMethod) {
            val methodName = psiElement.name
            val className = psiElement.containingClass?.name ?: "ERROR"
            val classPackage = getPackage(e.dataContext)

            val query = errorRatioQuery("function", "$classPackage.$className.$methodName")
            val url = makePrometheusUrl(query, "Function calls per minute")
            BrowserUtil.browse(url)
        }
    }

    override fun update(e: AnActionEvent) {
        val psiElement: PsiElement? = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = psiElement is PsiMethod
    }

    private fun errorRatioQuery(labelKey: String, labelValue: String?): String {
        val requestRate = requestRateQuery(labelKey, labelValue)
        return "(sum by (function, module, commit, version) (rate({__name__=~\"function_calls(_total)?\",$labelKey=\"$labelValue\",result=\"error\"}[5m]) $BUILD_INFO)) / ($requestRate)"
    }

    private fun requestRateQuery(labelKey: String?, labelValue: String?): String? {
        return "sum by (function, module, commit, version) (rate({__name__=~\"function_calls?(_total)?\",$labelKey=\"$labelValue\"}[5m]) $BUILD_INFO)"
    }
}