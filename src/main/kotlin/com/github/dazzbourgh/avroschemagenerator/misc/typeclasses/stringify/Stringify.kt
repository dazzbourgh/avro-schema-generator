package com.github.dazzbourgh.avroschemagenerator.misc.typeclasses.stringify

/**
 * A simple `toString`-like typeclass.
 */
interface Stringify<T> {
    /**
     * Convert this [T] into [String].
     *
     * @return [String] representation of this [T].
     */
    fun T.stringify(): String
}
