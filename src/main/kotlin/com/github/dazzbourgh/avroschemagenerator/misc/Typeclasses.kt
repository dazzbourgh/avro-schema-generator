package com.github.dazzbourgh.avroschemagenerator.misc

interface Stringify<T> {
    fun T.stringify(): String
}