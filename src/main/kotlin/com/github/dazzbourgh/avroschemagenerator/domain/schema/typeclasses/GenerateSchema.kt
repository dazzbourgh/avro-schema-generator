package com.github.dazzbourgh.avroschemagenerator.domain.schema.typeclasses

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexElement

/**
 * A typeclass to provide operation for specific schema generation from [ComplexElement].
 *
 * @param Schema a class representing specific schema, like Avro, XML etc.
 */
fun interface GenerateSchema<Schema> {

    /**
     * Generate schema from this [ComplexElement].
     *
     * @return specific [Schema].
     */
    fun ComplexElement.generateSchema(): Schema
}