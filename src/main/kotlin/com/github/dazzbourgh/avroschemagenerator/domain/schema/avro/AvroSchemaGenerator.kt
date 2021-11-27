package com.github.dazzbourgh.avroschemagenerator.domain.schema.avro

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Element
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringElement
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import java.util.*

object AvroSchemaGenerator {
    fun generateSchema(element: ComplexElement): Schema {
        val elements: Queue<Element> = LinkedList()
        var builder = SchemaBuilder.builder()
            .record(element.docName)
            .namespace(element.namespace)
            .fields()
        element.elements.forEach { elements.add(it) }
        while (elements.isNotEmpty()) {
            when (val el = elements.remove()) {
                is BooleanElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().booleanType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().booleanType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().booleanType().noDefault()
                    }
                }
                is ByteElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().intType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().intType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().intType().noDefault()
                    }
                }
                is CharacterElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().stringType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().stringType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().stringType().noDefault()
                    }
                }
                is DoubleElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().doubleType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().doubleType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().doubleType().noDefault()
                    }
                }
                is FloatElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().floatType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().floatType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().floatType().noDefault()
                    }
                }
                is IntElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().intType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().intType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().intType().noDefault()
                    }
                }
                is LongElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().longType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().longType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().longType().noDefault()
                    }
                }
                is ShortElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().intType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().intType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().intType().noDefault()
                    }
                }
                is StringElement -> {
                    builder = when (el.mode) {
                        NonNull -> builder.name(el.name).type().stringType().noDefault()
                        Nullable -> builder.name(el.name).type().nullable().stringType().noDefault()
                        Repeated -> builder.name(el.name).type().array().items().stringType().noDefault()
                    }
                }
                is ComplexElement -> {
                    val schema = generateSchema(el)
                    builder = when (el.mode) {
                        Repeated -> builder.name(el.name).type().array().items().type(schema).noDefault()
                        else -> builder.name(el.name).type(schema).noDefault()
                    }
                }
                is EnumElement -> {
                    val enumSchema = SchemaBuilder.builder()
                        .enumeration(el.docName)
                        .namespace(el.namespace)
                        .symbols(*el.values.toTypedArray())
                    builder = when (el.mode) {
                        Repeated -> builder.name(el.name).type().array().items().type(enumSchema).noDefault()
                        else -> {
                            builder.name(el.name)
                                .type(enumSchema)
                                .noDefault()
                        }
                    }
                }
            }
        }
        return builder.endRecord()
    }
}