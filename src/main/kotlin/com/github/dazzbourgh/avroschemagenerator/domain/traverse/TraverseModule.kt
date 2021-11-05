package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.github.dazzbourgh.avroschemagenerator.domain.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.GetGenericType
import com.github.dazzbourgh.avroschemagenerator.domain.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.IntType
import com.github.dazzbourgh.avroschemagenerator.domain.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.RepeatedType
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDiamondType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType.BOOLEAN
import com.intellij.psi.PsiType.BYTE
import com.intellij.psi.PsiType.CHAR
import com.intellij.psi.PsiType.DOUBLE
import com.intellij.psi.PsiType.FLOAT
import com.intellij.psi.PsiType.INT
import com.intellij.psi.PsiType.LONG
import com.intellij.psi.PsiType.NULL
import com.intellij.psi.PsiType.SHORT
import com.intellij.psi.PsiType.VOID
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

class TraverseModule<T>(
    getType: GetType<T>,
    getGenericType: GetGenericType<T>,
    getDocName: GetDocName<T>,
    getNamespaceName: GetNamespaceName<T>,
    getProperties: GetProperties<T>,
    getPropertyNames: GetPropertyNames<T>,
) : GetType<T> by getType,
    GetGenericType<T> by getGenericType,
    GetDocName<T> by getDocName,
    GetNamespaceName<T> by getNamespaceName,
    GetProperties<T> by getProperties,
    GetPropertyNames<T> by getPropertyNames

val psiGetType = GetType<PsiElement> {
    when (this) {
        is PsiClass -> ComplexType
        is PsiField -> {
            when (val psiType = getChildOfType<PsiTypeElement>()!!.type) {
                BYTE -> ByteType
                CHAR -> TODO()
                DOUBLE -> DoubleType
                FLOAT -> TODO()
                INT -> IntType
                LONG -> LongType
                SHORT -> TODO()
                BOOLEAN -> BooleanType
                VOID -> TODO()
                NULL -> TODO()
                is PsiArrayType -> RepeatedType
                is PsiDiamondType -> TODO()
                else -> TODO()
            }
        }
        else -> TODO()
    }
}

val psiGetGenericType =
    GetGenericType<PsiElement> { with(psiGetType) { getChildOfType<PsiTypeElement>()?.getPropertyType()!! } }

val psiGetDocName = GetDocName<PsiElement> {
    when (this) {
        is PsiClass -> name ?: throw IllegalArgumentException("Anonymous classes cannot have DocName")
        else -> throw IllegalArgumentException("DocName is only defined for classes")
    }
}

val psiGetNamespaceName = GetNamespaceName<PsiElement> {
    when (this) {
        is PsiClass -> PsiUtil.getPackageName(this)!!
        is PsiField -> PsiUtil.getTopLevelClass(this)?.let { PsiUtil.getPackageName(it) }!!
        else -> throw IllegalArgumentException("Package name can only be obtained for either a class or a field")
    }
}
val psiGetProperties = GetProperties<PsiElement> { getChildrenOfType<PsiField>().toList() }
val psiGetPropertyNames = GetPropertyNames<PsiElement> { getChildrenOfType<PsiField>().toList().map { it.name } }