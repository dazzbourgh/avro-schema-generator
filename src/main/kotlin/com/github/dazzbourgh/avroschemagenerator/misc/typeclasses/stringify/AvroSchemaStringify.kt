package com.github.dazzbourgh.avroschemagenerator.misc.typeclasses.stringify

import org.apache.avro.Schema

object AvroSchemaStringify : Stringify<Schema> {
    override fun Schema.stringify(): String = toString(true)
}
