package dev.autometrics.plugin

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

abstract class AutometricsAction: DumbAwareAction() {

    override fun update(e: AnActionEvent) {
        val psiElement: PsiElement? = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = psiElement is PsiMethod || psiElement is KtNamedFunction
    }

    fun getMethodInformation(
        psiElement: PsiElement?,
        e: AnActionEvent
    ) = when (psiElement) {
        is PsiMethod -> {
            AutometricsRequestRate.MethodInformation(
                methodName = psiElement.name,
                className = psiElement.containingClass?.name ?: "UNKNOWN",
                classPackage = AutometricsIntellij.getPackage(e.dataContext)
            )
        }

        is KtNamedFunction -> {
            AutometricsRequestRate.MethodInformation(
                methodName = psiElement.name ?: "ERROR",
                className = psiElement.containingClass()?.name ?: "UNKNOWN",
                classPackage = AutometricsIntellij.getPackage(e.dataContext)
            )
        }

        else -> {
            throw IllegalStateException("Unknown PSI element type: ${psiElement?.javaClass?.name}")
        }
    }
}