package com.synervoz.vocalfxchainsapp.ui.recording

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.utils.AssetLoader
import com.synervoz.vocalfxchainsapp.Config
import com.synervoz.vocalfxchainsapp.audio.RecordingAudioSystem
import com.synervoz.vocalfxchainsapp.ui.common.BaseViewModel
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class RecordingViewState : ViewState {
    object Loading : RecordingViewState()
    data class Ready(
        val beatButtonEnabled: Boolean,
        val beatButtonTitle: String,
        val recordingButtonEnabled: Boolean,
        val recordingButtonTitle: String,
        val fxSwitchChecked: Boolean,
        val fxSelectedIdx: Int
    ) : RecordingViewState()
}

sealed class RecordingSideEffect : SideEffect {
    object Finish : RecordingSideEffect()
}

class RecordingViewModel(
    private val audioSystem: RecordingAudioSystem,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<RecordingViewState>(RecordingViewState.Loading) {

    fun startAudioSystem(context: Context) {
        audioSystem.beatPlayerNode.load(AssetLoader.load(context, "trap130bpm.mp3"), Codec.createFromFileName("trap130bpm.mp3"))
        audioSystem.start()
        viewModelScope.launch {
            emitViewState(RecordingViewState.Ready(
                beatButtonEnabled = true,
                beatButtonTitle = "Listen to Beat",
                recordingButtonEnabled = true,
                recordingButtonTitle = "Start Recording",
                fxSwitchChecked = audioSystem.applyFXChain,
                fxSelectedIdx = Config.selectedFXChainIndex
            ))
        }
    }

    fun stopAudioSystem() {
        audioSystem.stop()
    }

    fun toggleRecording() {
        if (audioSystem.isRecording) {
            audioSystem.stopRecording()
            audioSystem.stopBeat()
            viewModelScope.launch {
                emitSideEffect(RecordingSideEffect.Finish)
            }
        } else {
            audioSystem.startRecording()
            audioSystem.startBeat()

            viewModelScope.launch {
                emitViewState(RecordingViewState.Ready(
                    beatButtonEnabled = false,
                    beatButtonTitle = "Listen to Beat",
                    recordingButtonEnabled = true,
                    recordingButtonTitle = "Stop Recording",
                    fxSwitchChecked = audioSystem.applyFXChain,
                    fxSelectedIdx = Config.selectedFXChainIndex
                ))
            }
        }
    }

    fun toggleBeat() {
        if (audioSystem.isBeatPlaying) {
            audioSystem.stopBeat()

            viewModelScope.launch {
                emitViewState(RecordingViewState.Ready(
                    beatButtonEnabled = true,
                    beatButtonTitle = "Listen to Beat",
                    recordingButtonEnabled = true,
                    recordingButtonTitle = "Start Recording",
                    fxSwitchChecked = audioSystem.applyFXChain,
                    fxSelectedIdx = Config.selectedFXChainIndex
                ))
            }
        } else {
            audioSystem.startBeat()

            viewModelScope.launch {
                emitViewState(RecordingViewState.Ready(
                    beatButtonEnabled = true,
                    beatButtonTitle = "Stop Beat",
                    recordingButtonEnabled = false,
                    recordingButtonTitle = "Start Recording",
                    fxSwitchChecked = audioSystem.applyFXChain,
                    fxSelectedIdx = Config.selectedFXChainIndex
                ))
            }
        }
    }

    fun enableFXChain(enable: Boolean) {
        audioSystem.applyFXChain = enable
    }

    fun selectFXChain(index: Int) {
        Config.selectedFXChainIndex = index
        audioSystem.selectFXChain()
    }

    fun enableLiveMonitoring(enable: Boolean) {
        audioSystem.enableLiveMonitoring(enable)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecordingViewModel(
                    audioSystem = RecordingAudioSystem(),
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}