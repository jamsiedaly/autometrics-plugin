package dev.autometrics.plugin

import dev.autometrics.plugin.AutometricsIntellij.getPackage
import dev.autometrics.plugin.AutometricsIntellij.makePrometheusUrl
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class AutometricsLatency : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (psiElement is PsiMethod) {
            val methodName = psiElement.name
            val className = psiElement.containingClass?.name ?: "ERROR"
            val classPackage = getPackage(e.dataContext)

            val query = latencyQuery("function", "$classPackage.$className.$methodName")
            val url = makePrometheusUrl(query, "Function calls per minute")
            BrowserUtil.browse(url)
        }
    }

    override fun update(e: AnActionEvent) {
        val psiElement: PsiElement? = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = psiElement is PsiMethod
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