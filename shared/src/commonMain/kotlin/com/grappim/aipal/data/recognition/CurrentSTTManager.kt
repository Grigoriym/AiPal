package com.grappim.aipal.data.recognition

enum class CurrentSTTManager(
    val description: String,
) {
    Android(
        "Uses default Android speech recognition, " +
                "it is not continuous speech recognition, requires internet, " +
                "presumably is more accurate than Vosk, " +
                "can capitalize words"
    ),
    Vosk(
        "Continuous speech recognition, does not require internet, " +
                "must be downloaded beforehand (~50Mb)"
    ),
    ;

    companion object {
        fun default(): CurrentSTTManager = Android
    }
}
