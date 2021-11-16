package com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.DelegatingTraverseModule
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.FieldType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.Mode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ResolveElementReference
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.boxedTypeNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.boxedTypesMap
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiResolveElementReference.resolveElementReference
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.getAllDescendantsOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.isCollection
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.isGeneric
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.mapBoxedType
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

    private val module = DelegatingTraverseModule(
        PsiGetType,
        PsiGetDocName,
        PsiGetNamespaceName,
        PsiGetProperties,
        PsiGetPropertyNames,
        PsiGetMode(PsiGetType),
        PsiResolveElementReference
    )

    object PsiTraverseModule : GetType<PsiElement> by module,
        GetDocName<PsiElement> by module,
        GetNamespaceName<PsiElement> by module,
        GetProperties<PsiElement> by module,
        GetPropertyNames<PsiElement> by module,
        GetMode<PsiElement> by module,
        ResolveElementReference<PsiElement> by module
}
