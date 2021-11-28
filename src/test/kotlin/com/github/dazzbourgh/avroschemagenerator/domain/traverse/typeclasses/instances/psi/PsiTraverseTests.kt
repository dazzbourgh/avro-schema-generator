package com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverseUtils.getFirstDescendantOfType
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

internal class PsiGetTypeTest : LightJavaCodeInsightFixtureTestCase() {
    override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

    override fun setUp() {
        super.setUp()
        myFixture.configureByFiles("TypesTestClass.java", "SomeTestClass.java", "TestEnum.java")
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

    fun `test PsiGetType should support BigInteger, BigDecimal`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val fields = listOf(
            "bigInteger",
            "bigDecimal"
        ).map { clazz.findFieldByName(it, false)!! }
        val expected = listOf(
            LongType,
            DoubleType
        )

        val actual = fields.map { with(PsiGetType) { it.getPropertyType() } }

        assertThat(actual).containsExactlyElementsOf(expected)
    }

    fun `test PsiGetType should support LocalDateTime, Date`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val fields = listOf(
            "localDateTime",
            "date"
        ).map { clazz.findFieldByName(it, false)!! }
        val expected = listOf(
            DoubleType,
            DoubleType
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

    fun `test PsiGetType should support enum types for base classes`() {
        val clazz: PsiClass = myFixture.javaFacade.findClass("TestEnum")

        val actual = with(PsiGetType) { clazz.getPropertyType() }

        assertThat(actual).isEqualTo(EnumType)
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

    fun `test PsiGetType should throw for fields of interface type`() {
        val clazz: PsiClass = myFixture.javaFacade.findClass("ClassWithInterfaceField")
        val field = clazz.findFieldByName("someInterface", false)!!

        assertThatThrownBy { with(PsiGetType) { field.getPropertyType() } }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    fun `test PsiGetType should support enum types for fields`() {
        val clazz: PsiClass = myFixture.javaFacade.findClass("TypesTestClass")
        val field = clazz.findFieldByName("testEnum", false)!!

        val actual = with(PsiGetType) { field.getPropertyType() }

        assertThat(actual).isEqualTo(EnumType)
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
        val clazz = myFixture.file

        assertThatThrownBy { with(PsiGetMode) { clazz.getMode() } }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    fun `test PsiGetMode should return Nullable for resolvable complex types`() {
        val clazz: PsiClass = myFixture.findClass("TypesTestClass")
        val field = clazz.findFieldByName("someTestClass", false)!!

        val actual = with(PsiGetMode) { field.getMode() }

        assertThat(actual).isEqualTo(Nullable)
    }

    fun `test PsiGetMode should return NonNull for primitive type`() {
        val clazz: PsiClass = myFixture.findClass("TypesTestClass")
        val field = clazz.findFieldByName("boolPrimitive", false)!!

        val actual = with(PsiGetMode) { field.getMode() }

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
        val sut = PsiGetMode
        val clazz: PsiClass = myFixture.findClass("TypesTestClass")
        val field = clazz.findFieldByName("list", false)!!

        val actual = with(sut) { field.getMode() }

        assertThat(actual).isEqualTo(Repeated)
    }
}

internal class PsiGetElementDeclarationTest : LightJavaCodeInsightFixtureTestCase() {
    override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

    override fun setUp() {
        super.setUp()
        myFixture.configureByFiles("ResolveElementClass.java", "SomeTestClass.java")
    }

    fun `test PsiResolveElementReference should resolve PsiJavaCodeReferenceElement elements to their declaring PsiElement`() {
        val ref =
            myFixture.getReferenceAtCaretPositionWithAssertion("ResolveElementClass.java") as PsiJavaCodeReferenceElement
        val actual = with(PsiGetElementDeclaration) { ref.getElementDeclaration() }!!

        assertThat(actual).isInstanceOf(PsiClass::class.java)
        assertThat((actual as PsiClass).name).isEqualTo("SomeTestClass")
    }

    fun `test PsiResolveElementReference should resolve fields containing PsiJavaCodeReferenceElement to their declaring PsiElement`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val field = clazz.findFieldByName("someTestClass", false)!!
        val actual = with(PsiGetElementDeclaration) { field.getElementDeclaration() }

        assertThat(actual).isInstanceOf(PsiClass::class.java)
        assertThat((actual as PsiClass).name).isEqualTo("SomeTestClass")
    }

    fun `test PsiResolveElementReference should not resolve elements not containing PsiJavaCodeReferenceElement to their declaring PsiElement`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val field = clazz.getFirstDescendantOfType<PsiKeyword>()!!
        val actual = with(PsiGetElementDeclaration) { field.getElementDeclaration() }

        assertThat(actual).isNull()
    }
}

internal class PsiGetElementDeclarationHeavyTest : HeavyPlatformTestCase() {
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

    fun `test PsiResolveElementReference should resolve repeated fields containing complex types to the declaring PsiElement of that complex type`() {
        val clazz: PsiClass = myFixture.findClass("ResolveElementClass")
        val field = clazz.findFieldByName("someTestClassList", false)!!
        val actual = with(PsiGetElementDeclaration) { field.getElementDeclaration() }

        assertThat(actual).isInstanceOf(PsiClass::class.java)
        assertThat((actual as PsiClass).name).isEqualTo("SomeTestClass")
    }

}
