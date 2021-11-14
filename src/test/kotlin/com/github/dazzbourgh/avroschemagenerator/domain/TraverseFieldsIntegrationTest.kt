package com.github.dazzbourgh.avroschemagenerator.domain

import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverse.PsiTraverseModule
import com.intellij.psi.PsiElement
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory
import com.intellij.testFramework.fixtures.ModuleFixture
import org.assertj.core.api.Assertions.assertThat

internal class TraverseFieldsIntegrationTest : HeavyPlatformTestCase() {
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

    fun `test traverseFields should correctly build complex element from a Java class`() {
        val clazz: PsiElement = myFixture.findClass("TypesTestClass")
        val expected =
            ComplexElement(
                "TypesTestClass",
                "",
                null,
                listOf(
                    ByteElement("bytePrimitive", NonNull),
                    ShortElement("shortPrimitive", NonNull),
                    IntElement("integerPrimitive", NonNull),
                    LongElement("longPrimitive", NonNull),
                    FloatElement("floatPrimitive", NonNull),
                    DoubleElement("doublePrimitive", NonNull),
                    CharacterElement("characterPrimitive", NonNull),
                    BooleanElement("boolPrimitive", NonNull),

                    ByteElement("byteBoxed", Nullable),
                    IntElement("integerBoxed", Nullable),
                    ShortElement("shortBoxed", Nullable),
                    LongElement("longBoxed", Nullable),
                    FloatElement("floatBoxed", Nullable),
                    DoubleElement("doubleBoxed", Nullable),
                    CharacterElement("characterBoxed", Nullable),
                    BooleanElement("boolBoxed", Nullable),

                    StringElement("string", Nullable),

                    IntElement("arr", Repeated),
                    IntElement("list", Repeated),

                    ComplexElement(
                        "SomeTestClass",
                        "",
                        "someTestClass",
                        listOf(
                            StringElement("field", Nullable)
                        ),
                        Nullable
                    )
                ),
                Nullable
            )

        val actual = traverse(clazz, PsiTraverseModule)

        assertThat(actual).isEqualTo(expected)
    }
}