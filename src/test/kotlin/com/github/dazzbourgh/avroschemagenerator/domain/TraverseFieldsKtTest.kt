package com.github.dazzbourgh.avroschemagenerator.domain

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.TraverseModule
import com.intellij.testFramework.UsefulTestCase

class TraverseFieldsKtTest : UsefulTestCase() {
    private val getType = GetType<Class<*>> {
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
    private val getTypeGeneric = GetGenericType<Class<*>> { StringType }
    private val getDocName = GetDocName<Class<*>> { simpleName }
    private val getNamespaceName = GetNamespaceName<Class<*>> { packageName }
    private val getProperties = GetProperties<Class<*>> { declaredFields.toList().map { it.type } }
    private val getPropertyNames = GetPropertyNames<Class<*>> { declaredFields.toList().map { it.name } }


    val runTest = { clazz: Class<*> ->
        traverseFields(
            clazz,
            TraverseModule(getType, getTypeGeneric, getDocName, getNamespaceName, getProperties, getPropertyNames)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
    }
}