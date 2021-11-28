package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FieldType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Mode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.boxedTypeNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.boxedTypesMap
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverse.PsiGetElementDeclaration.getElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getAllDescendantsOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.isCollection
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.isGeneric
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

object PsiTraverse {
    object PsiGetType : GetType<PsiElement> {
        override fun PsiElement.getPropertyType(): FieldType =
            when (this) {
                is PsiClass -> if (isEnum) EnumType else ComplexType
                is PsiField -> {
                    val psiTypeElement = getFirstDescendantOfType<PsiTypeElement>()!!
                    when (val descendantType = psiTypeElement.type) {
                        is PsiPrimitiveType -> mapPrimitiveType(descendantType)
                        is PsiArrayType -> mapPrimitiveType(psiTypeElement.getLastDescendantOfType<PsiTypeElement>()!!.type)
                        is PsiClassReferenceType -> when {
                            boxedTypeNames.contains(descendantType.name) -> boxedTypesMap[descendantType.name]!!
                            else -> when {
                                isGeneric(psiTypeElement) && psiTypeElement.isCollection() -> with(PsiGetType) {
                                    psiTypeElement.getLastDescendantOfType<PsiTypeElement>()!!.getPropertyType()
                                }
                                isGeneric(psiTypeElement) -> TODO("Generics are currently not supported")
                                else -> {
                                    val ref = with(PsiGetElementDeclaration) { getElementDeclaration() }
                                    when {
                                        ref is PsiClass && ref.isEnum -> EnumType
                                        ref?.let { it is PsiClassImpl && it.isInterface } == true ->
                                            throw IllegalArgumentException("Data classes cannot contain interface fields")
                                        ref?.containingFile?.let { file ->
                                            ProjectFileIndex.getInstance(file.project).isInSource(file.virtualFile)
                                        } == true -> ComplexType
                                        else -> throw IllegalArgumentException(
                                            "Class fields are only supported for " +
                                                    "classes in same module sources"
                                        )
                                    }
                                }
                            }
                        }
                        else -> throw IllegalArgumentException()
                    }
                }
                is PsiTypeElement -> when (val t = type) {
                    is PsiPrimitiveType -> mapPrimitiveType(t)
                    is PsiClassReferenceType -> boxedTypesMap[t.className]
                        ?: with(PsiGetElementDeclaration) { getElementDeclaration()!!.getPropertyType() }
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
                is PsiField -> resolveEnumAndGet { getDocName() }
                else -> throw IllegalArgumentException("DocName is only defined for classes and enums")
            }
    }

    object PsiGetNamespaceName : GetNamespaceName<PsiElement> {
        override fun PsiElement.getNamespaceName(): String =
            when (this) {
                is PsiClass -> PsiUtil.getPackageName(this)!!
                is PsiField -> resolveEnumAndGet { getNamespaceName() }
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

    object PsiGetMode : GetMode<PsiElement> {
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

    object PsiGetElementDeclaration : GetElementDeclaration<PsiElement> {
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

    object PsiGetEnumValues : GetEnumValues<PsiElement> {
        override fun PsiElement.getEnumValues(): List<String> =
            when (this) {
                is PsiClass -> getAllDescendantsOfType<PsiEnumConstant>()
                    .map { it.getFirstDescendantOfType<PsiIdentifier>() }
                    .mapNotNull { it?.text }
                is PsiField -> resolveEnumAndGet { getEnumValues() }
                else -> throw IllegalArgumentException("Enums must be of PsiClass type")
            }
    }

    private fun <T> PsiField.resolveEnumAndGet(block: PsiClass.() -> T): T =
        when (val resolved =
            getLastDescendantOfType<PsiJavaCodeReferenceElement>()?.resolve()) {
            is PsiClass -> resolved.block()
            else -> throw IllegalArgumentException("Reference should only point to class or enum")
        }
}
