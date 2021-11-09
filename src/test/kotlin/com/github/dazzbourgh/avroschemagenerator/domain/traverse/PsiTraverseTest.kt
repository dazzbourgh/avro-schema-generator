package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.github.dazzbourgh.avroschemagenerator.domain.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.StringType
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverse.psiGetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.psi.PsiTraverse.psiGetType
import com.intellij.psi.PsiClass
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

internal class PsiTraverseGetTypeTest : LightJavaCodeInsightFixtureTestCase() {
    override fun getTestDataPath() = "src/test/testData/traverse/psi/getType"

    override fun setUp() {
        super.setUp()
        myFixture.configureByFiles("TypesTestClass.java", "SomeTestClass.java")
    }

    fun `test psiGetDocName`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val expectedDocName = "TypesTestClass"
        val actualDocName = with(psiGetDocName) { clazz.getDocName() }
        assertThat(actualDocName).isEqualTo(expectedDocName)
    }

    fun `test psiGetType should support all primitive types`() {
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

        val actual = fields.map { with(psiGetType) { it.getPropertyType() } }

        assertThat(actual).containsExactlyElementsOf(expected)
    }

    fun `test psiGetType should support boxed types and string`() {
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

        val actual = fields.map { with(psiGetType) { it.getPropertyType() } }

        assertThat(actual).containsExactlyElementsOf(expected)
    }

    fun `test psiGetType should support repeated types`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val fields = listOf(
            "arr",
            "list"
        ).map { clazz.findFieldByName(it, false)!! }
        val expected = listOf(
            IntegerType,
            IntegerType
        )

        val actual = fields.map { with(psiGetType) { it.getPropertyType() } }

        assertThat(actual).containsExactlyElementsOf(expected)
    }

    fun `test psiGetType should support complex types`() {
        val clazz: PsiClass = file.getChildOfType()!!
        val fields = listOf("someTestClass").map { clazz.findFieldByName(it, false)!! }
        val expected = listOf(ComplexType)

        val actual = fields.map { with(psiGetType) { it.getPropertyType() } }

        assertThat(actual).containsExactlyElementsOf(expected)
    }
}
