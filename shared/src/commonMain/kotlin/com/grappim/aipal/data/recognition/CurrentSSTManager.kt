package com.grappim.aipal.data.recognition

enum class CurrentSSTManager(
    val description: String,
) {
    Android("Uses default Android speech recognition, " +
            "it is not continuous speech recognition, requires internet, " +
            "presumably is more accurate than Vosk, " +
            "can capitalize words"),
    Vosk("Continuous speech recognition, does not require internet"),
    ;

    companion object {
        fun default(): CurrentSSTManager = Vosk
    }
}
