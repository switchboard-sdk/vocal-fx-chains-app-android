package com.synervoz.vocalfxchainsapp.ui.recording

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.synervoz.vocalfxchainsapp.ui.common.BaseViewModel
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState

sealed class RecordingViewState : ViewState {
    object Loading : RecordingViewState()
}

sealed class RecordingSideEffect : SideEffect {

}

class RecordingViewModel(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<RecordingViewState>(RecordingViewState.Loading) {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecordingViewModel(
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}