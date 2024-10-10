package com.grappim.aipal.android.core

import android.content.Context
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.vosk.ModelRetriever
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.recognition.android.AndroidSttManager
import com.grappim.aipal.android.recognition.android.AndroidSpeechRecognizerWrapper
import com.grappim.aipal.android.recognition.android.AndroidSpeechRecognizerWrapperImpl
import com.grappim.aipal.android.recognition.factory.AndroidSTTFactory
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoder
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoderImpl
import com.grappim.aipal.android.recognition.vosk.VoskSttManager
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.STTFactory
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val recognizersModule =
    module {
        single<AndroidSpeechRecognizerWrapper> {
            val context = get<Context>()
            AndroidSpeechRecognizerWrapperImpl(context)
        }

        factory<RecognitionMessageDecoder> { RecognitionMessageDecoderImpl(get<Json>()) }

        single { AndroidSttManager(get<AndroidSpeechRecognizerWrapper>(), get<LocalDataStorage>()) }
        single {
            VoskSttManager(
                get<RecognitionMessageDecoder>(),
                get<LocalDataStorage>(),
                get<FolderPathManager>(),
                get<VoskModelCheck>(),
                get<ModelRetriever>()
            )
        }

        single<STTFactory> {
            AndroidSTTFactory(
                get<AndroidSttManager>(),
                get<VoskSttManager>()
            )
        }
    }
