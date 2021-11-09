package com.github.dazzbourgh.avroschemagenerator.domain.psi

import com.github.dazzbourgh.avroschemagenerator.domain.PrimitiveType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiReferenceParameterList
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.util.*

typealias ClassName = String

object PsiTraverseUtils {
    internal inline fun <reified T : PsiElement> PsiElement.getFirstDescendantOfType(): T? {
        val queue: Queue<PsiElement> = LinkedList()
        queue.add(this)
        while (!queue.isEmpty()) {
            val current = queue.remove()
            if (current is T) return current
            else current.children.forEach { queue.add(it) }
        }
        return null
    }

    internal inline fun <reified T : PsiElement> PsiElement.getLastDescendantOfType(): T? {
        val traversalQueue: Queue<PsiElement> = LinkedList()
        val childrenQueue: Queue<PsiElement> = LinkedList()
        traversalQueue.add(this)
        while (!traversalQueue.isEmpty()) {
            val current = traversalQueue.remove()
            current.children.forEach { traversalQueue.add(it) }
            childrenQueue.add(current)
        }
        return childrenQueue.reversed().firstOrNull { it is T } as T?
    }

    internal fun mapBoxedType(psiType: PsiClassReferenceType, primitiveTypeSupplier: (ClassName) -> PrimitiveType?): PrimitiveType =
        primitiveTypeSupplier(psiType.className)
            ?: throw IllegalArgumentException("Only boxed primitive types and String are supported")

    internal fun isGeneric(psiTypeElement: PsiTypeElement): Boolean =
        (psiTypeElement
            .getChildOfType<PsiJavaCodeReferenceElement>()
            ?.getChildOfType<PsiReferenceParameterList>()
            ?.getChildrenOfType<PsiTypeElement>()
            ?.size ?: 0) > 0

    internal fun isCollection(psiTypeElement: PsiTypeElement): Boolean =
        psiTypeElement
            .getChildOfType<PsiJavaCodeReferenceElement>()
            ?.getChildOfType<PsiIdentifier>()
            ?.let {
                when (it.text) {
                    "List", "Set", "Collection" -> true
                    else -> false
                }
            } ?: false
}