package com.github.dazzbourgh.avroschemagenerator.domain.traverse.model

sealed class Mode
object Nullable : Mode()
object Repeated : Mode()
object NonNull : Mode()