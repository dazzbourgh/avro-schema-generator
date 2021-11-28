package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

/*
* Data classes representing types. Since every language and schema can have slight differences, this abstraction
* can help add support for additional languages/schemas without workarounds caused by specific language/schema limitations.
*/

sealed class FieldType
sealed class PrimitiveType : FieldType()
object BooleanType : PrimitiveType()
object ByteType : PrimitiveType()
object ShortType : PrimitiveType()
object IntegerType : PrimitiveType()
object LongType : PrimitiveType()
object FloatType : PrimitiveType()
object DoubleType : PrimitiveType()
object CharacterType : PrimitiveType()
object StringType : PrimitiveType()
object ComplexType : FieldType()
object EnumType : FieldType()