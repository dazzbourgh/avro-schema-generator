package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.intellij.testFramework.UsefulTestCase

class TraverseFieldsKtTest : UsefulTestCase() {
    private val namespace = "com.github.dazzbourgh.avroschemagenerator.domain.traverse"

    private val getType = GetType<Class<*>> {
        when (simpleName) {
            "boolean" -> BooleanType
            "byte" -> ByteType
            "int" -> IntegerType
            "long" -> LongType
            "String" -> StringType
            "double" -> DoubleType
            "Set", "List" -> StringType
            else -> ComplexType
        }
    }
    private val getDocName = GetDocName<Class<*>> { simpleName }
    private val getNamespaceName = GetNamespaceName<Class<*>> { packageName }
    private val getProperties = GetProperties<Class<*>> { declaredFields.toList().map { it.type } }
    private val getPropertyNames = GetPropertyNames<Class<*>> { declaredFields.toList().map { it.name } }
    private val getMode = GetMode<Class<*>> {
        when (this) {
            List::class.java, Set::class.java -> Repeated
            else -> Nullable
        }
    }
    private val resolveElementReference = ResolveElementReference<Class<*>> { this }

    val runTest = { clazz: Class<*> ->
        traverse(
            clazz,
            DelegatingTraverseModule(
                getType,
                getDocName,
                getNamespaceName,
                getProperties,
                getPropertyNames,
                getMode,
                resolveElementReference
            )
        )
    }

    fun `test traverseFields should support Boolean`() {
        data class TestClass(val b: Boolean)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(BooleanElement("b", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support Byte`() {
        data class TestClass(val b: Byte)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(ByteElement("b", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support Int`() {
        data class TestClass(val i: Int)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(IntElement("i", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support Long`() {
        data class TestClass(val l: Long)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(LongElement("l", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support Double`() {
        data class TestClass(val d: Double)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(DoubleElement("d", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support String`() {
        data class TestClass(val s: String)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(StringElement("s", Nullable)),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support complex types`() {
        class ChildTestClass
        data class TestClass(val c: ChildTestClass)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(
                ComplexElement(
                    "ChildTestClass",
                    namespace,
                    "c",
                    listOf(),
                    Nullable
                )
            ),
            Nullable
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support repeated types`() {
        data class TestClass(val s: Set<String>)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(
                StringElement("s", Repeated)
            ),
            Nullable
        )
        assertEquals(expected, actual)
    }
}