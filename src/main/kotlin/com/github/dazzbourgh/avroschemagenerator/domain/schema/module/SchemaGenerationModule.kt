package com.github.dazzbourgh.avroschemagenerator.domain.schema.module

import com.github.dazzbourgh.avroschemagenerator.domain.schema.typeclasses.GenerateSchema
import com.github.dazzbourgh.avroschemagenerator.misc.typeclasses.stringify.Stringify

data class SchemaGenerationModule<Schema>(val generateSchema: GenerateSchema<Schema>, val stringify: Stringify<Schema>)
