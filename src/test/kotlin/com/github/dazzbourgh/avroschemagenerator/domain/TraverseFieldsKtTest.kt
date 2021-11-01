package com.github.dazzbourgh.avroschemagenerator.domain

import com.intellij.testFramework.UsefulTestCase
import junit.framework.TestCase

class TraverseFieldsKtTest : UsefulTestCase() {
    private val typed = Typed<Class<*>> {
        when (simpleName) {
            "boolean" -> BooleanType
            "byte" -> ByteType
            "int" -> IntType
            "long" -> LongType
            "String" -> StringType
            "double" -> DoubleType
            "Set", "List" -> RepeatedType
            else -> ComplexType
        }
    }
    private val typedGeneric = Typed<Class<*>> { StringType }
    private val docNamed = Named<Class<*>> { simpleName }
    private val namespaceNamed = Named<Class<*>> { packageName }
    private val getProperties = GetProperties<Class<*>> { declaredFields.toList().map { it.type } }
    private val getPropertyNames = GetPropertyNames<Class<*>> { declaredFields.toList().map { it.name } }

    private val runTest = { clazz: Class<*> ->
        traverseFields(
            clazz,
            typed,
            typedGeneric,
            docNamed,
            namespaceNamed,
            getProperties,
            getPropertyNames
        )
    }

    fun `test traverseFields should support Boolean`() {
        data class TestClass(val b: Boolean)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(BooleanElement("b"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support Byte`() {
        data class TestClass(val b: Byte)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(ByteElement("b"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support Int`() {
        data class TestClass(val i: Int)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(IntElement("i"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support Long`() {
        data class TestClass(val l: Long)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(LongElement("l"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support Double`() {
        data class TestClass(val d: Double)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(DoubleElement("d"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support String`() {
        data class TestClass(val s: String)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(StringElement("s"))
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support complex types`() {
        class ChildTestClass
        data class TestClass(val c: ChildTestClass)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(
                ComplexElement(
                    "ChildTestClass",
                    "com.github.dazzbourgh.avroschemagenerator.domain",
                    "c",
                    listOf()
                )
            )
        )
        TestCase.assertEquals(expected, actual)
    }

    fun `test traverseFields should support repeated types`() {
        data class TestClass(val s: Set<String>)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            "com.github.dazzbourgh.avroschemagenerator.domain",
            null,
            listOf(
                RepeatedElement(StringElement("s"))
            )
        )
        TestCase.assertEquals(expected, actual)
    }
}