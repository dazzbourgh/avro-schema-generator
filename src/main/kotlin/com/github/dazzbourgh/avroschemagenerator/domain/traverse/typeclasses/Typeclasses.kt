package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FieldType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Mode

fun interface GetType<T> {
    fun T.getPropertyType(): FieldType
}

fun interface GetDocName<T> {
    fun T.getDocName(): String
}

fun interface GetNamespaceName<T> {
    fun T.getNamespaceName(): String
}

fun interface GetProperties<T> {
    fun T.getProperties(): List<T>
}

fun interface GetPropertyNames<T> {
    fun T.getPropertyNames(): List<String>
}

fun interface GetMode<T> {
    fun T.getMode(): Mode
}

fun interface GetElementDeclaration<T> {
    fun T.getElementDeclaration(): T?
}

fun interface GetEnumValues<T> {
    fun T.getEnumValues(): List<String>
}