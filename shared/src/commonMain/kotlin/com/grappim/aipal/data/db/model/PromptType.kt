package com.grappim.aipal.data.db.model

enum class PromptType(val title: String) {
    BEHAVIOR("behavior"),
    TRANSLATION("translation"),
    SPELLING("spelling"),
    AI_FIX("ai_fix");

    companion object {
        fun getTypeFromString(name: String) =
            when (name) {
                BEHAVIOR.title -> BEHAVIOR
                TRANSLATION.title -> TRANSLATION
                SPELLING.title -> SPELLING
                AI_FIX.title -> AI_FIX
                else -> error("No type with name $name")
            }
    }
}
