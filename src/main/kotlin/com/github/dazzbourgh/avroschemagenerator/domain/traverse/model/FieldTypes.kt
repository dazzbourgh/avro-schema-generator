package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

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