package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.resolveEnumAndGet
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField

/**
 * Extracts class name from [PsiElement] as document name for classes and enums, throws [IllegalArgumentException]
 * for everything else.
 */
object PsiGetDocName : GetDocName<PsiElement> {
    override fun PsiElement.getDocName(): String =
        when (this) {
            is PsiClass -> name ?: throw IllegalArgumentException("Anonymous classes cannot have DocName")
            is PsiField -> resolveEnumAndGet { getDocName() }
            else -> throw IllegalArgumentException("DocName is only defined for classes and enums")
        }
}