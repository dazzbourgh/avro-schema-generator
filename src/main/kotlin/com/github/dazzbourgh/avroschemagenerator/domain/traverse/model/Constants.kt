package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

val boxedTypeNames = listOf(
    "Boolean",
    "Byte",
    "Character",
    "Float",
    "Integer",
    "Long",
    "Short",
    "Double",
    "String",
    "BigInteger",
    "BigDecimal",
    "LocalDateTime",
    "Date"
)
val boxedTypes = listOf(
    BooleanType,
    ByteType,
    CharacterType,
    FloatType,
    IntegerType,
    LongType,
    ShortType,
    DoubleType,
    StringType,
    LongType,
    DoubleType,
    DoubleType,
    DoubleType
)
val boxedTypesMap = boxedTypeNames.zip(boxedTypes).toMap()