package dev.deepslate.serverutility.utils

object TypeHelper {
    inline fun <reified T> mustBe(any: Any) =
        any as? T ?: throw IllegalArgumentException("Expected $any to be a ${T::class.java}")
}