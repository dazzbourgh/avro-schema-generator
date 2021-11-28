package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Mode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiGetElementDeclaration.getElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getAllDescendantsOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.isCollection
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiTypeElement

/**
 * Psi get mode
 */
object PsiGetMode : GetMode<PsiElement> {
    /**
     * Gets mode for a [PsiField], throws for everything else, unless a [java.util.Collection] instance is found,
     * in that case the mode is [Repeated].
     *
     * For primitive types mode is [NonNull], for complex types [Nullable].
     *
     * @return [Mode] for this [PsiField].
     */
    override fun PsiElement.getMode(): Mode =
        when (this) {
            is PsiClass -> if (isCollection()) Repeated else Nullable
            is PsiField -> {
                when (val ref = getFirstDescendantOfType<PsiJavaCodeReferenceElement>()) {
                    null -> when (getAllDescendantsOfType<PsiTypeElement>().size) {
                        0, 1 -> NonNull
                        else -> Repeated
                    }
                    else -> {
                        val resolvedRef = ref.getElementDeclaration()
                        when {
                            resolvedRef == null -> Nullable
                            resolvedRef.isCollection() -> Repeated
                            else -> Nullable
                        }
                    }
                }
            }
            else -> throw IllegalArgumentException("Mode is only defined for PsiField types")
        }
}