package com.github.dazzbourgh.avroschemagenerator.domain.traverse.modules

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverse
import com.intellij.psi.PsiElement

private val module = DelegatingTraverseModule(
    PsiTraverse.PsiGetType,
    PsiTraverse.PsiGetDocName,
    PsiTraverse.PsiGetNamespaceName,
    PsiTraverse.PsiGetProperties,
    PsiTraverse.PsiGetPropertyNames,
    PsiTraverse.PsiGetMode,
    PsiTraverse.PsiGetElementDeclaration,
    PsiTraverse.PsiGetEnumValues
)

object PsiTraverseModule : GetType<PsiElement> by module,
    GetDocName<PsiElement> by module,
    GetNamespaceName<PsiElement> by module,
    GetProperties<PsiElement> by module,
    GetPropertyNames<PsiElement> by module,
    GetMode<PsiElement> by module,
    GetElementDeclaration<PsiElement> by module,
    GetEnumValues<PsiElement> by module