package com.grappim.aipal.feature.settings.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.STTFactory
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.repo.DbRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val localDataStorage: LocalDataStorage,
    private val sttFactory: STTFactory,
    private val dbRepo: DbRepo,
) : ViewModel() {

    private lateinit var sttManager: STTManager

    private val _state = MutableStateFlow(
        LanguageState(
            currentLanguage = SupportedLanguage.getDefault(),
            languages = SupportedLanguage.entries.map { it.title }.toSet(),
            onSetCurrentLanguage = ::setLanguage,
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sttManager = sttFactory.getSSTManager(localDataStorage.sttManager.first())
            localDataStorage.currentLanguage.distinctUntilChanged().collect { lng ->
                _state.update { it.copy(currentLanguage = lng) }
                sttManager.changeLanguage(lng)
            }
        }
    }

    private fun setLanguage(supportedLanguage: String) {
        viewModelScope.launch {
            val enum = SupportedLanguage.entries.find { it.title == supportedLanguage }
            localDataStorage.setCurrentLanguage(requireNotNull(enum))
        }
    }
}
