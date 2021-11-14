package com.github.dazzbourgh.avroschemagenerator.domain.psi

import com.github.dazzbourgh.avroschemagenerator.domain.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.DelegatingTraverseModule
import com.github.dazzbourgh.avroschemagenerator.domain.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.FieldType
import com.github.dazzbourgh.avroschemagenerator.domain.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.Mode
import com.github.dazzbourgh.avroschemagenerator.domain.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.ResolveElementReference
import com.github.dazzbourgh.avroschemagenerator.domain.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.boxedTypeNames
import com.github.dazzbourgh.avroschemagenerator.domain.boxedTypesMap
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverse.PsiResolveElementReference.resolveElementReference
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.getAllDescendantsOfType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.isCollection
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.isGeneric
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverseUtils.mapBoxedType
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

object PsiTraverse {
    object PsiGetType : GetType<PsiElement> {
        override fun PsiElement.getPropertyType(): FieldType =
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
                                isGeneric(psiTypeElement) && psiTypeElement.isCollection() -> with(PsiGetType) {
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
                    else -> throw IllegalArgumentException(
                        "Only PsiPrimitiveType and PsiClassReferenceType can be " +
                                "mapped to a type, received ${this::class.java} instead"
                    )
                }
                else -> throw IllegalArgumentException("Traversal can only be performed on classes and fields")
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
    }

    object PsiGetDocName : GetDocName<PsiElement> {
        override fun PsiElement.getDocName(): String =
            when (this) {
                is PsiClass -> name ?: throw IllegalArgumentException("Anonymous classes cannot have DocName")
                else -> throw IllegalArgumentException("DocName is only defined for classes")
            }
    }

    object PsiGetNamespaceName : GetNamespaceName<PsiElement> {
        override fun PsiElement.getNamespaceName(): String =
            when (this) {
                is PsiClass -> PsiUtil.getPackageName(this)!!
                else -> throw IllegalArgumentException("Package name can only be obtained for a class")
            }
    }

    object PsiGetProperties : GetProperties<PsiElement> {
        override fun PsiElement.getProperties() =
            getChildrenOfType<PsiField>().toList()
    }

    object PsiGetPropertyNames : GetPropertyNames<PsiElement> {
        override fun PsiElement.getPropertyNames() =
            getChildrenOfType<PsiField>().toList().map { it.name }
    }

    class PsiGetMode(private val getType: GetType<PsiElement>) : GetMode<PsiElement> {
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
                            val resolvedRef = ref.resolveElementReference()
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

    object PsiResolveElementReference : ResolveElementReference<PsiElement> {
        override fun PsiElement.resolveElementReference(): PsiElement? =
            when (this) {
                is PsiJavaCodeReferenceElement -> resolve()
                else -> with(PsiResolveElementReference) {
                    getFirstDescendantOfType<PsiJavaCodeReferenceElement>()
                        ?.resolveElementReference()
                }
            }
    }

    object PsiTraverseModule : DelegatingTraverseModule<PsiElement>(
        PsiGetType,
        PsiGetDocName,
        PsiGetNamespaceName,
        PsiGetProperties,
        PsiGetPropertyNames,
        PsiGetMode(PsiGetType),
        PsiResolveElementReference
    )
}
