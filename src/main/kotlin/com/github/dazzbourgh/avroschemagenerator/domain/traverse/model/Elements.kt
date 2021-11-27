package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

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

data class EnumElement(
    val docName: String,
    val namespace: String,
    val name: String?,
    val values: List<String>,
    val mode: Mode
) : PrimitiveElement()