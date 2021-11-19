package com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.StringType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiGetType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiResolveElementReference
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverseUtils.getFirstDescendantOfType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiKeyword
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.ModuleFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

object PsiTraverseTest {
    internal class PsiGetTypeTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("TypesTestClass.java", "SomeTestClass.java")
        }

        fun `test PsiGetType should support all primitive types`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val fields = listOf(
                "bytePrimitive",
                "shortPrimitive",
                "integerPrimitive",
                "longPrimitive",
                "floatPrimitive",
                "doublePrimitive",
                "characterPrimitive",
                "boolPrimitive"
            ).map { clazz.findFieldByName(it, false)!! }
            val expected = listOf(
                ByteType,
                ShortType,
                IntegerType,
                LongType,
                FloatType,
                DoubleType,
                CharacterType,
                BooleanType
            )

            val actual = fields.map { with(PsiGetType) { it.getPropertyType() } }

            assertThat(actual).containsExactlyElementsOf(expected)
        }

        fun `test PsiGetType should support boxed types and string`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val fields = listOf(
                "byteBoxed",
                "integerBoxed",
                "shortBoxed",
                "longBoxed",
                "floatBoxed",
                "doubleBoxed",
                "characterBoxed",
                "boolBoxed",
                "string"
            ).map { clazz.findFieldByName(it, false)!! }
            val expected = listOf(
                ByteType,
                IntegerType,
                ShortType,
                LongType,
                FloatType,
                DoubleType,
                CharacterType,
                BooleanType,
                StringType
            )

            val actual = fields.map { with(PsiGetType) { it.getPropertyType() } }

            assertThat(actual).containsExactlyElementsOf(expected)
        }

        fun `test PsiGetType should support complex types`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val fields = listOf("someTestClass").map { clazz.findFieldByName(it, false)!! }
            val expected = listOf(ComplexType)

            val actual = fields.map { with(PsiGetType) { it.getPropertyType() } }

            assertThat(actual).containsExactlyElementsOf(expected)
        }

        fun `test PsiGetType should throw for complex type interfaces`() {

        }
    }

    internal class PsiGetTypeHeavyTest : HeavyPlatformTestCase() {
        private lateinit var myFixture: JavaCodeInsightTestFixture
        private lateinit var moduleFixture: ModuleFixture
        private val testDataPath = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            val projectBuilder = JavaTestFixtureFactory.createFixtureBuilder(name)
            myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.fixture)
            myFixture.testDataPath = testDataPath

            val projectBuilderWithModule = projectBuilder.addModule(JavaModuleFixtureBuilder::class.java)

            moduleFixture = projectBuilderWithModule
                .addContentRoot(testDataPath)
                .addSourceRoot("")
                .fixture

            myFixture.setUp()
        }

        override fun tearDown() {
            myFixture.tearDown()
        }

        fun `test PsiGetType should throw for complex type outside source root`() {
            val clazz: PsiClass = myFixture.findClass("ClassWithComplexFieldOutsideSourceRoot")
            val field = clazz.findFieldByName("is", false)!!

            assertThatThrownBy { with(PsiGetType) { field.getPropertyType() } }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    internal class PsiGetDocNameTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("TypesTestClass.java", "SomeTestClass.java")
        }

        fun `test PsiGetDocName`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val expectedDocName = "TypesTestClass"
            val actualDocName = with(PsiGetDocName) { clazz.getDocName() }
            assertThat(actualDocName).isEqualTo(expectedDocName)
        }
    }

    internal class PsiGetNamespaceNameTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("ClassWithPackageName.java", "SomeTestClass.java")
        }

        fun `test PsiGetNamespaceName should return package name for PsiClass`() {
            val clazz: PsiClass = file.getChildOfType()!!

            val actual = with(PsiGetNamespaceName) { clazz.getNamespaceName() }

            assertThat(actual).isEqualTo("pkg")
        }

        fun `test PsiGetNamespaceName should throw for any non-PsiClass PsiElement`() {
            val clazz: PsiClass = myFixture.findClass("SomeTestClass")
            val field = clazz.findFieldByName("field", false)!!

            assertThatThrownBy { with(PsiGetNamespaceName) { field.getNamespaceName() } }.isInstanceOf(
                IllegalArgumentException::class.java
            )
        }
    }

    internal class PsiGetPropertiesTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("SomeTestClass.java")
        }

        fun `test PsiGetPropertyNames should return field names for PsiClass`() {
            val clazz: PsiClass = file.getChildOfType()!!

            val actual = with(PsiGetProperties) { clazz.getProperties() }

            assertThat(actual).hasSize(1)
            assertThat(actual[0]).isInstanceOf(PsiField::class.java)
            assertThat(actual[0].name).isEqualTo("field")
        }
    }

    internal class PsiGetPropertyNamesTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("SomeTestClass.java")
        }

        fun `test PsiGetPropertyNames should return field names for PsiClass`() {
            val clazz: PsiClass = file.getChildOfType()!!

            val actual = with(PsiGetPropertyNames) { clazz.getPropertyNames() }

            assertThat(actual).containsExactly("field")
        }
    }

    internal class PsiGetModeTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("TypesTestClass.java", "List.java")
        }

        fun `test PsiGetMode throw for non-PsiField types`() {
            val sut = PsiGetMode { TODO() }
            val clazz = myFixture.file

            assertThatThrownBy { with(sut) { clazz.getMode() } }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        fun `test PsiGetMode should return Nullable for resolvable complex types`() {
            val sut = PsiGetMode { ComplexType }
            val clazz: PsiClass = myFixture.findClass("TypesTestClass")
            val field = clazz.findFieldByName("someTestClass", false)!!

            val actual = with(sut) { field.getMode() }

            assertThat(actual).isEqualTo(Nullable)
        }

        fun `test PsiGetMode should return NonNull for primitive type`() {
            val sut = PsiGetMode { BooleanType }
            val clazz: PsiClass = myFixture.findClass("TypesTestClass")
            val field = clazz.findFieldByName("boolPrimitive", false)!!

            val actual = with(sut) { field.getMode() }

            assertThat(actual).isEqualTo(NonNull)
        }
    }

    internal class PsiGetModeHeavyTest : HeavyPlatformTestCase() {
        private lateinit var myFixture: JavaCodeInsightTestFixture
        private lateinit var moduleFixture: ModuleFixture
        private val testDataPath = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            val projectBuilder = JavaTestFixtureFactory.createFixtureBuilder(name)
            myFixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.fixture)
            myFixture.testDataPath = testDataPath

            val projectBuilderWithModule = projectBuilder.addModule(JavaModuleFixtureBuilder::class.java)

            moduleFixture = projectBuilderWithModule
                .addContentRoot(testDataPath)
                .addSourceRoot("")
                .fixture

            myFixture.setUp()
        }

        override fun tearDown() {
            myFixture.tearDown()
        }

        fun `test PsiGetMode should return Repeated for collection type`() {
            val sut = PsiGetMode { TODO() }
            val clazz: PsiClass = myFixture.findClass("TypesTestClass")
            val field = clazz.findFieldByName("list", false)!!

            val actual = with(sut) { field.getMode() }

            assertThat(actual).isEqualTo(Repeated)
        }
    }

    internal class PsiResolveElementReferenceTest : LightJavaCodeInsightFixtureTestCase() {
        override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

        override fun setUp() {
            super.setUp()
            myFixture.configureByFiles("ResolveElementClass.java", "SomeTestClass.java")
        }

        fun `test PsiResolveElementReference should resolve PsiJavaCodeReferenceElement elements to their declaring PsiElement`() {
            val ref =
                myFixture.getReferenceAtCaretPositionWithAssertion("ResolveElementClass.java") as PsiJavaCodeReferenceElement
            val actual = with(PsiResolveElementReference) { ref.resolveElementReference() }!!

            assertThat(actual).isInstanceOf(PsiClass::class.java)
            assertThat((actual as PsiClass).name).isEqualTo("SomeTestClass")
        }

        fun `test PsiResolveElementReference should resolve elements containing PsiJavaCodeReferenceElement to their declaring PsiElement`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val field = clazz.findFieldByName("someTestClass", false)!!
            val actual = with(PsiResolveElementReference) { field.resolveElementReference() }!!

            assertThat(actual).isInstanceOf(PsiClass::class.java)
            assertThat((actual as PsiClass).name).isEqualTo("SomeTestClass")
        }

        fun `test PsiResolveElementReference should not resolve elements not containing PsiJavaCodeReferenceElement to their declaring PsiElement`() {
            val clazz: PsiClass = file.getChildOfType()!!
            val field = clazz.getFirstDescendantOfType<PsiKeyword>()!!
            val actual = with(PsiResolveElementReference) { field.resolveElementReference() }

            assertThat(actual).isNull()
        }
    }
}
