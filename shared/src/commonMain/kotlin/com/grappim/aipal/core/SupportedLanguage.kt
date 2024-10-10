package com.grappim.aipal.core

enum class SupportedLanguage(
    val lang: String,
    val title: String,
    val dbId: Long
) {
    GERMAN("de", "German", 2),
    ENGLISH("en", "English", 1),
    ;

    companion object {
        fun getDefault() = GERMAN
    }
}
