package com.github.dazzbourgh.avroschemagenerator.domain.traverse

sealed class FieldType

sealed class PrimitiveType : FieldType()
object BooleanType : PrimitiveType()
object ByteType : PrimitiveType()
object ShortType : PrimitiveType()
object IntegerType : PrimitiveType()
object LongType : PrimitiveType()
object FloatType : PrimitiveType()
object DoubleType : PrimitiveType()
object CharacterType : PrimitiveType()
object StringType : PrimitiveType()

object ComplexType : FieldType()

//--------------------------------

sealed class Mode
object Nullable : Mode()
object Repeated : Mode()
object NonNull : Mode()

sealed class Element
sealed class PrimitiveElement : Element()

data class BooleanElement(val name: String, val mode: Mode) : PrimitiveElement()
data class IntElement(val name: String, val mode: Mode) : PrimitiveElement()
data class LongElement(val name: String, val mode: Mode) : PrimitiveElement()
data class ShortElement(val name: String, val mode: Mode) : PrimitiveElement()
data class FloatElement(val name: String, val mode: Mode) : PrimitiveElement()
data class CharacterElement(val name: String, val mode: Mode) : PrimitiveElement()
data class DoubleElement(val name: String, val mode: Mode) : PrimitiveElement()
data class ByteElement(val name: String, val mode: Mode) : PrimitiveElement()
data class StringElement(val name: String, val mode: Mode) : PrimitiveElement()

data class ComplexElement(
    val docName: String,
    val namespace: String,
    val name: String?,
    val elements: List<Element>,
    val mode: Mode
) : Element()

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

fun interface ResolveElementReference<T> {
    fun T.resolveElementReference(): T?
}

val boxedTypeNames = setOf(
    "Boolean",
    "Byte",
    "Character",
    "Float",
    "Integer",
    "Long",
    "Short",
    "Double",
    "String"
)
val boxedTypes = setOf(
    BooleanType,
    ByteType,
    CharacterType,
    FloatType,
    IntegerType,
    LongType,
    ShortType,
    DoubleType,
    StringType
)
val boxedTypesMap = boxedTypeNames.zip(boxedTypes).toMap()
