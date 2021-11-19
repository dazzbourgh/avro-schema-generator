package com.github.dazzbourgh.avroschemagenerator.domain.schema.avro

import com.github.dazzbourgh.avroschemagenerator.misc.Stringify
import org.apache.avro.Schema

object AvroSchemaStringify : Stringify<Schema> {
    override fun Schema.stringify(): String = toString(true)
}
