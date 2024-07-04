package com.grappim.aipal.core

const val DEFAULT_TEMPERATURE = 0.5
const val DEFAULT_MODEL = "gpt-3.5-turbo"
const val DEFAULT_BEHAVIOR =
    "You are my German friend with whom I want to practice German. " +
            "When I finish this conversation, " +
            "show me my errors and provide ways to fix them."

const val DEFAULT_TRANSLATION_PROMPT = "Translate the next message to English, " +
        "give me only the translation and nothing else"

const val DEFAULT_SPELLING_CHECK_PROMPT = "Check the next message for any types of errors, " +
        "and provide me only the correct answer and nothing else. If the sentence is fine just send me " +
        "that sentence that I have provided"

const val DEFAULT_VOSK_SAMPLE_RATE = 16000.0f
const val SAMPLE_RATE = 44100.0f

const val DEFAULT_AI_ANSWER_FIX_PROMPT = "Please answer me without special symbols, like *, because " +
        "otherwise the TTS engine will also pronounce those special symbols."
