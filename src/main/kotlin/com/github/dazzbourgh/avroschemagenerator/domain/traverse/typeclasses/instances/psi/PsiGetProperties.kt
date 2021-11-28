package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * Psi get properties
 */
object PsiGetProperties : GetProperties<PsiElement> {
    /**
     * Get fields of this class.
     *
     * @return [List] of [PsiField] for fields of this class.
     */
    override fun PsiElement.getProperties() =
        getChildrenOfType<PsiField>().toList()
}