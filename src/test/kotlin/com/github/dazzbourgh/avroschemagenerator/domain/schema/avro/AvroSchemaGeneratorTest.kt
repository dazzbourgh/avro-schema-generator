package com.github.dazzbourgh.avroschemagenerator.domain.schema.avro

import com.github.dazzbourgh.avroschemagenerator.domain.TestUtils.complexElement
import com.github.dazzbourgh.avroschemagenerator.domain.schema.avro.AvroSchemaGenerator.generateSchema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class AvroSchemaGeneratorTest {
    @Test
    fun `generateSchema should create valid avro schema from Element`() {
        val input = complexElement
        //@formatter:off
        val expected = SchemaBuilder.record("TypesTestClass")
            .namespace("")
            .fields()
            .name("bytePrimitive").type().intType().noDefault()
            .name("shortPrimitive").type().intType().noDefault()
            .name("integerPrimitive").type().intType().noDefault()
            .name("longPrimitive").type().longType().noDefault()
            .name("floatPrimitive").type().floatType().noDefault()
            .name("doublePrimitive").type().doubleType().noDefault()
            .name("characterPrimitive").type().stringType().noDefault()
            .name("boolPrimitive").type().booleanType().noDefault()
            //----------------
            .name("byteBoxed").type().nullable().intType().noDefault()
            .name("integerBoxed").type().nullable().intType().noDefault()
            .name("shortBoxed").type().nullable().intType().noDefault()
            .name("longBoxed").type().nullable().longType().noDefault()
            .name("floatBoxed").type().nullable().floatType().noDefault()
            .name("doubleBoxed").type().nullable().doubleType().noDefault()
            .name("characterBoxed").type().nullable().stringType().noDefault()
            .name("boolBoxed").type().nullable().booleanType().noDefault()
            //----------------
            .name("string").type().nullable().stringType().noDefault()
            //----------------
            .name("bigInteger").type().nullable().longType().noDefault()
            .name("bigDecimal").type().nullable().doubleType().noDefault()
            .name("localDateTime").type().nullable().doubleType().noDefault()
            .name("date").type().nullable().doubleType().noDefault()
            //----------------
            .name("arr").type().array().items().intType().noDefault()
            .name("list").type().array().items().intType().noDefault()
            //----------------
            .name("someTestClass")
                .type().record("SomeTestClass").namespace("").fields()
                    .name("field").type().nullable().stringType().noDefault()
            .endRecord()
            .noDefault()
            .name("someTestClassList")
                .type().array().items().type("SomeTestClass").noDefault()
            .name("testEnum")
                .type().enumeration("TestEnum").namespace("")
                .symbols("ONE", "TWO").noDefault()
            .name("testEnumSet")
                .type().array().items().type("TestEnum").noDefault()
            .endRecord()
        //@formatter:on

        val actual = generateSchema(input)

        assertThat(actual).isEqualTo(expected)
    }
}