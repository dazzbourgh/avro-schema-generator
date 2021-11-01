package com.github.dazzbourgh.avroschemagenerator.domain

sealed class FieldType

sealed class PrimitiveType : FieldType()
object BooleanType : PrimitiveType()
object ByteType : PrimitiveType()
object IntType : PrimitiveType()
object LongType : PrimitiveType()
object DoubleType : PrimitiveType()
object StringType : PrimitiveType()

object ComplexType : FieldType()
object RepeatedType : FieldType()

//--------------------------------

sealed class Element
sealed class PrimitiveElement : Element()

data class BooleanElement(val name: String) : PrimitiveElement()
data class IntElement(val name: String) : PrimitiveElement()
data class LongElement(val name: String) : PrimitiveElement()
data class DoubleElement(val name: String) : PrimitiveElement()
data class ByteElement(val name: String) : PrimitiveElement()
data class StringElement(val name: String) : PrimitiveElement()

data class ComplexElement(val docName: String, val namespace: String, val name: String?, val elements: List<Element>) : Element()
data class RepeatedElement(val element: Element) : Element()

fun interface Typed<T> {
    fun T.getPropertyType(): FieldType
}

fun interface Named<T> {
    fun T.getName(): String
}

fun interface GetProperties<T> {
    fun T.getProperties(): List<T>
}

fun interface GetPropertyNames<T> {
    fun T.getPropertyNames(): List<String>
}
