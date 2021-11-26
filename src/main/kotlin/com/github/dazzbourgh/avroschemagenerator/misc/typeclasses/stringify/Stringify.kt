package com.github.dazzbourgh.avroschemagenerator.misc.typeclasses.stringify

interface Stringify<T> {
    fun T.stringify(): String
}