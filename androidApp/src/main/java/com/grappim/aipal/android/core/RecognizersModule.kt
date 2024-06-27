package com.grappim.aipal.android.core

import android.content.Context
import com.grappim.aipal.android.recognition.android.AndroidSSTManager
import com.grappim.aipal.android.recognition.android.SpeechRecognitionWrapper
import com.grappim.aipal.android.recognition.android.SpeechRecognitionWrapperImpl
import com.grappim.aipal.android.recognition.factory.SSTFactory
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoder
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoderImpl
import com.grappim.aipal.android.recognition.vosk.RecognitionModelRetriever
import com.grappim.aipal.android.recognition.vosk.RecognitionModelRetrieverImpl
import com.grappim.aipal.android.recognition.vosk.VoskSSTManager
import com.grappim.aipal.data.local.LocalDataStorage
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val recognizersModule =
    module {

        single<SpeechRecognitionWrapper> {
            val context = get<Context>()
            SpeechRecognitionWrapperImpl(context)
        }

        single { AndroidSSTManager(get<SpeechRecognitionWrapper>(), get<LocalDataStorage>()) }

        single {
            VoskSSTManager(
                get<RecognitionMessageDecoder>(),
                get<RecognitionModelRetriever>(),
            )
        }
        factory<RecognitionModelRetriever> { RecognitionModelRetrieverImpl(get<Context>()) }
        factory<RecognitionMessageDecoder> { RecognitionMessageDecoderImpl(get<Json>()) }

        single { SSTFactory(get(), get()) }
    }
