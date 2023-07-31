package com.synervoz.vocalfxchainsapp.ui.fxediting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.synervoz.vocalfxchainsapp.ui.common.BaseViewModel
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState

sealed class FXEditingViewState : ViewState {
    object Loading : FXEditingViewState()
}

sealed class FXEditingSideEffect : SideEffect {

}

class FXEditingViewModel(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<FXEditingViewState>(FXEditingViewState.Loading) {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FXEditingViewModel(
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}