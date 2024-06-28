package com.grappim.aipal.core

enum class SupportedLanguage(
    val lang: String,
    val title: String,
) {
    GERMAN("de", "German"),
    ENGLISH("en", "English"),
    ;

    companion object {
        fun getDefault() = GERMAN
    }
}
