package com.grappim.aipal.android.core

import android.content.Context
import com.grappim.aipal.android.files.path.FolderPathManager
import com.grappim.aipal.android.files.vosk.ModelRetriever
import com.grappim.aipal.android.files.vosk.VoskModelCheck
import com.grappim.aipal.android.recognition.android.AndroidSSTManager
import com.grappim.aipal.android.recognition.android.SpeechRecognitionWrapper
import com.grappim.aipal.android.recognition.android.SpeechRecognitionWrapperImpl
import com.grappim.aipal.android.recognition.factory.STTFactory
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoder
import com.grappim.aipal.android.recognition.vosk.RecognitionMessageDecoderImpl
import com.grappim.aipal.android.recognition.vosk.VoskSttManager
import com.grappim.aipal.data.local.LocalDataStorage
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val recognizersModule =
    module {

        single<SpeechRecognitionWrapper> {
            val context = get<Context>()
            SpeechRecognitionWrapperImpl(context)
        }

        factory<RecognitionMessageDecoder> { RecognitionMessageDecoderImpl(get<Json>()) }

        factory { AndroidSSTManager(get<SpeechRecognitionWrapper>(), get<LocalDataStorage>()) }
        factory {
            VoskSttManager(
                get<RecognitionMessageDecoder>(),
                get<LocalDataStorage>(),
                get<FolderPathManager>(),
                get<VoskModelCheck>(),
                get<ModelRetriever>()
            )
        }

        single {
            STTFactory(
                androidSSTManagerFactory = {
                    get<AndroidSSTManager>()
                },
                voskSttManagerFactory = {
                    get<VoskSttManager>()
                }
            )
        }
    }
