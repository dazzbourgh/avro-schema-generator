package com.github.dazzbourgh.avroschemagenerator.domain.psi

import com.github.dazzbourgh.avroschemagenerator.domain.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.GetDeclared
import com.github.dazzbourgh.avroschemagenerator.domain.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.boxedTypeNames
import com.github.dazzbourgh.avroschemagenerator.domain.boxedTypesMap
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.isCollection
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.isGeneric
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.mapBoxedType
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiReferenceParameterList
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

object PsiTraverse {
    val psiGetType: GetType<PsiElement>
        get() = GetType {
            when (this) {
                is PsiClass -> ComplexType
                is PsiField -> {
                    val psiTypeElement = getFirstDescendantOfType<PsiTypeElement>()!!
                    when (val descendantType = psiTypeElement.type) {
                        is PsiPrimitiveType -> mapPrimitiveType(descendantType)
                        is PsiArrayType -> mapPrimitiveType(psiTypeElement.getLastDescendantOfType<PsiTypeElement>()!!.type)
                        is PsiClassReferenceType -> when {
                            boxedTypeNames.contains(descendantType.name) -> mapBoxedType(descendantType) { boxedTypesMap[it] }
                            else -> when {
                                isGeneric(psiTypeElement) && isCollection(psiTypeElement) -> with(psiGetType) {
                                    psiTypeElement.getLastDescendantOfType<PsiTypeElement>()!!.getPropertyType()
                                }
                                isGeneric(psiTypeElement) -> TODO("Generics are currently not supported")
                                else -> ComplexType
                            }
                        }
                        else -> throw IllegalArgumentException()
                    }
                }
                is PsiTypeElement -> when (val t = type) {
                    is PsiPrimitiveType -> mapPrimitiveType(t)
                    is PsiClassReferenceType -> mapBoxedType(t) { boxedTypesMap[it] }
                    else -> TODO()
                }
                else -> throw IllegalArgumentException("Traversal can only be performed on classes and fields")
            }
        }

    private fun mapPrimitiveType(psiType: PsiType): PrimitiveType =
        when (psiType) {
            PsiType.BYTE -> ByteType
            PsiType.SHORT -> ShortType
            PsiType.INT -> IntegerType
            PsiType.LONG -> LongType
            PsiType.FLOAT -> FloatType
            PsiType.DOUBLE -> DoubleType
            PsiType.CHAR -> CharacterType
            PsiType.BOOLEAN -> BooleanType
            PsiType.VOID -> throw IllegalArgumentException("Void (Unit) types should not be present in data classes")
            PsiType.NULL -> TODO()
            else -> throw IllegalArgumentException("Not a primitive type: $psiType")
        }

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

    val psiGetMode = GetMode<PsiElement> { TODO() }

    val psiGetDeclared = GetDeclared<PsiElement> { reference?.resolve()!! }
}

