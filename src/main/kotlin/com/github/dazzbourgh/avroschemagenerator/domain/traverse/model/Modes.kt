package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

// A mode indicates whether the field value must be present, is optional or is an array.

sealed class Mode
object Nullable : Mode()
object Repeated : Mode()
object NonNull : Mode()
