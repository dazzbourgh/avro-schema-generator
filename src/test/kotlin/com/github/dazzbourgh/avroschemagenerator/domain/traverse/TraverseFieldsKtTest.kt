package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.modules.DelegatingTraverseModule
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType
import com.intellij.testFramework.UsefulTestCase

class TraverseFieldsKtTest : UsefulTestCase() {
    enum class EnumClass {
        ONE, TWO
    }

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
            else -> if (simpleName.contains("EnumClass")) EnumType else ComplexType
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
    private val getElementDeclaration = GetElementDeclaration<Class<*>> { this }
    private val getEnumValues = GetEnumValues<Class<*>> { EnumClass.values().map { it.name } }

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
                getElementDeclaration,
                getEnumValues
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
            NonNull
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
            NonNull
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
            NonNull
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
            NonNull
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
            NonNull
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
            NonNull
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
            NonNull
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
            NonNull
        )
        assertEquals(expected, actual)
    }

    fun `test traverseFields should support enum types`() {
        data class TestClass(val e: EnumClass)

        val actual = runTest(TestClass::class.java)
        val expected = ComplexElement(
            "TestClass",
            namespace,
            null,
            listOf(
                EnumElement(
                    "EnumClass",
                    "com.github.dazzbourgh.avroschemagenerator.domain.traverse",
                    "e",
                    listOf("ONE", "TWO"),
                    Nullable
                )
            ),
            NonNull
        )
        assertEquals(expected, actual)
    }
}