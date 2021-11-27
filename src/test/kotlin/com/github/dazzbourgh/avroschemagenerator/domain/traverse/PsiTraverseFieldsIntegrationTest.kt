package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.github.dazzbourgh.avroschemagenerator.domain.TestUtils.complexElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.instances.psi.PsiTraverse.PsiTraverseModule
import com.intellij.psi.PsiElement
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory
import com.intellij.testFramework.fixtures.ModuleFixture
import org.assertj.core.api.Assertions.assertThat

internal class PsiTraverseFieldsIntegrationTest : HeavyPlatformTestCase() {
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
        val clazz: PsiElement = myFixture.javaFacade.findClass("TypesIntegrationTestClass")
        val expected = complexElement.copy(docName = "TypesIntegrationTestClass")

        val actual = traverse(clazz, PsiTraverseModule)

        assertThat(actual).isEqualTo(expected)
    }
}
