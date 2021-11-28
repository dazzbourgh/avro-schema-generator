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
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.boxedTypeNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.boxedTypesMap
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getLastDescendantOfType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.isCollection
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.impl.source.PsiClassReferenceType

/**
 * Psi get type
 */
object PsiGetType : GetType<PsiElement> {
    /**
     * Gets [FieldType] for field.
     *
     * @return [FieldType]
     */
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
                            PsiTraverseUtils.isGeneric(psiTypeElement) && psiTypeElement.isCollection() -> with(
                                PsiGetType
                            ) {
                                psiTypeElement.getLastDescendantOfType<PsiTypeElement>()!!.getPropertyType()
                            }
                            PsiTraverseUtils.isGeneric(psiTypeElement) -> TODO("Generics are currently not supported")
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
            else -> throw IllegalArgumentException("Not a primitive type: $psiType")
        }
}