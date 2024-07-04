package com.grappim.aipal.utils

inline fun <reified T> Any.runAs(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}
