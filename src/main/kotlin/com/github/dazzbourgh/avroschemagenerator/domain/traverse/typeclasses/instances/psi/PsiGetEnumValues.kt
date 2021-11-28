package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getAllDescendantsOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.resolveEnumAndGet
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier

/**
 * Psi get enum values
 */
object PsiGetEnumValues : GetEnumValues<PsiElement> {
    /**
     * Get enum values
     *
     * @return [List] of possible enum constant values as [String].
     */
    override fun PsiElement.getEnumValues(): List<String> =
        when (this) {
            is PsiClass -> getAllDescendantsOfType<PsiEnumConstant>()
                .map { it.getFirstDescendantOfType<PsiIdentifier>() }
                .mapNotNull { it?.text }
            is PsiField -> resolveEnumAndGet { getEnumValues() }
            else -> throw IllegalArgumentException("Enums must be of PsiClass type")
        }
}