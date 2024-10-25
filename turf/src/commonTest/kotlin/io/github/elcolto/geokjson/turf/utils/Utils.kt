package io.github.elcolto.geokjson.turf.utils

@Deprecated(
    "Should not be used, since it is not exhaustive on type changes",
    replaceWith = ReplaceWith(expression = "this as T"),
)
internal inline fun <reified T> Any?.asInstance(): T? = this as? T
