package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.isCollection
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement

/**
 * Resolves fields to a file where field type is declared.
 */
object PsiGetElementDeclaration : GetElementDeclaration<PsiElement> {
    /**
     * Get element declaration
     *
     * @return [PsiElement] that is a [com.intellij.psi.PsiClass] when possible, null otherwise.
     */
    override fun PsiElement.getElementDeclaration(): PsiElement? =
        when (this) {
            is PsiField -> when {
                isCollection() -> getLastDescendantOfType()
                else -> getFirstDescendantOfType<PsiJavaCodeReferenceElement>()
            }?.getElementDeclaration()
            is PsiJavaCodeReferenceElement -> resolve()
            else -> with(PsiGetElementDeclaration) {
                getFirstDescendantOfType<PsiJavaCodeReferenceElement>()
                    ?.getElementDeclaration()
            }
        }
}