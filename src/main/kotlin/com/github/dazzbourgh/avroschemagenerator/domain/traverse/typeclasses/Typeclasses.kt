package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FieldType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Mode

/**
 * Get type of element.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetType<T> {
    /**
     * @return [FieldType] for this [T].
     */
    fun T.getPropertyType(): FieldType
}

/**
 * Get a name of a document that will be generated from element.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetDocName<T> {
    /**
     * @return document name. For Java classes that would be a simple class name.
     */
    fun T.getDocName(): String
}

/**
 * Get namespace for element. For Java classes that would be their package name.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetNamespaceName<T> {
    /**
     * @return namespace for this document. For Java classes that would be a package name.
     */
    fun T.getNamespaceName(): String
}

/**
 * Get properties (fields) of element.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetProperties<T> {
    /**
     * @return [List] of properties (fields), which are also instances of [T].
     */
    fun T.getProperties(): List<T>
}

/**
 * Get names of properties (fields) of element.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetPropertyNames<T> {
    /**
     * @return [List] of [String] representing properties' (fields) names.
     */
    fun T.getPropertyNames(): List<String>
}

/**
 * Get [Mode] for element. For top level elements [Mode] should be
 * [com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull].
 *
 * @param T generic element that is being traversed.
 */
fun interface GetMode<T> {
    /**
     * @return [Mode] of this element.
     */
    fun T.getMode(): Mode
}

/**
 * Navigate to declaration of current element. A declaration is something that represents the same base type and
 * allows to clearly identify, whether this element belongs to the same source root as root parent element or not.
 * For some types (like primitives) it is possible that declaration cannot be retrieved, in that case the result will
 * be `null`.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetElementDeclaration<T> {
    /**
     * @return a declaration of this element, if it's possible to resolve such, otherwise null.
     */
    fun T.getElementDeclaration(): T?
}

/**
 * Get enum values of this element. If there is no value or the type is not an enum, an empty [List] is returned.
 *
 * @param T generic element that is being traversed.
 */
fun interface GetEnumValues<T> {
    /**
     * @return [List] with values for this enum. Can be empty for non-enum types.
     */
    fun T.getEnumValues(): List<String>
}