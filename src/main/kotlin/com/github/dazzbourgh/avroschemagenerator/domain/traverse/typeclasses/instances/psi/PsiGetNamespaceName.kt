package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.resolveEnumAndGet
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.util.PsiUtil

/**
 * Psi get namespace name
 */
object PsiGetNamespaceName : GetNamespaceName<PsiElement> {
    /**
     * Gets package name for classes and enums, throws for primitives.
     *
     * @return package name as [String].
     */
    override fun PsiElement.getNamespaceName(): String =
        when (this) {
            is PsiClass -> PsiUtil.getPackageName(this)!!
            is PsiField -> resolveEnumAndGet { getNamespaceName() }
            else -> throw IllegalArgumentException("Package name can only be obtained for a class or enum")
        }
}