package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * Psi get property names
 */
object PsiGetPropertyNames : GetPropertyNames<PsiElement> {
    /**
     * Gets [List] of field names for this class.
     *
     * @return [List] of field names as [String].
     */
    override fun PsiElement.getPropertyNames() =
        getChildrenOfType<PsiField>().toList().map { it.name }
}