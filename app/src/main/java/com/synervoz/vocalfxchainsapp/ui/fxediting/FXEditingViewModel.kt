package com.synervoz.vocalfxchainsapp.ui.fxediting

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
import com.synervoz.vocalfxchainsapp.R
import com.synervoz.vocalfxchainsapp.audio.FXEditingAudioSystem
import com.synervoz.vocalfxchainsapp.ui.common.BaseViewModel
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState
import com.synervoz.vocalfxchainsapp.ui.recording.RecordingViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class FXEditingViewState : ViewState {
    object Loading : FXEditingViewState()
    data class Ready(
        val playbackButtonTitle: String,
        val fxSwitchChecked: Boolean,
        val fxSelectedIdx: Int,
        val presetSelectedIdx: Int,
        val vocalVolume: Float,
        val beatVolume: Float
    ) : FXEditingViewState()
}

sealed class FXEditingSideEffect : SideEffect {
    object StartExport : FXEditingSideEffect()
    object FinishExport : FXEditingSideEffect()
}

class FXEditingViewModel(
    private val audioSystem: FXEditingAudioSystem,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<FXEditingViewState>(FXEditingViewState.Loading) {

    fun startAudioSystem(context: Context) {
        audioSystem.beatPlayerNode.load(AssetLoader.load(context, "trap130bpm.mp3"), Codec.createFromFileName("trap130bpm.mp3"))
        audioSystem.vocalPlayerNode.load(Config.cleanVocalRecordingFilePath, Config.recordingFormat)
        audioSystem.beatPlayerNode.endPosition = audioSystem.vocalPlayerNode.getDuration()
        audioSystem.start()
        viewModelScope.launch {
            emitViewState(
                FXEditingViewState.Ready(
                    playbackButtonTitle = "Start Playback",
                    fxSwitchChecked = audioSystem.applyFXChain,
                    fxSelectedIdx = Config.selectedFXChainIndex,
                    presetSelectedIdx = if (Config.selectedFXChainIndex == 0) Config.harmonizerPreset else Config.radioPreset,
                    vocalVolume = audioSystem.vocalGainNode.gain,
                    beatVolume = audioSystem.beatGainNode.gain
            ))
        }
    }

    fun stopAudioSystem() {
        audioSystem.stop()
    }

    fun togglePlayback() {
        if (audioSystem.isPlaying) {
            audioSystem.stopPlayback()

            viewModelScope.launch {
                emitViewState(
                    FXEditingViewState.Ready(
                        playbackButtonTitle = "Start Playback",
                        fxSwitchChecked = audioSystem.applyFXChain,
                        fxSelectedIdx = Config.selectedFXChainIndex,
                        presetSelectedIdx = if (Config.selectedFXChainIndex == 0) Config.harmonizerPreset else Config.radioPreset,
                        vocalVolume = audioSystem.vocalGainNode.gain,
                        beatVolume = audioSystem.beatGainNode.gain
                    ))
            }
        } else {
            audioSystem.startPlayback()

            viewModelScope.launch {
                emitViewState(
                    FXEditingViewState.Ready(
                        playbackButtonTitle = "Stop Playback",
                        fxSwitchChecked = audioSystem.applyFXChain,
                        fxSelectedIdx = Config.selectedFXChainIndex,
                        presetSelectedIdx = if (Config.selectedFXChainIndex == 0) Config.harmonizerPreset else Config.radioPreset,
                        vocalVolume = audioSystem.vocalGainNode.gain,
                        beatVolume = audioSystem.beatGainNode.gain
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

    fun selectFXChainPreset(index: Int) {
        when (Config.selectedFXChainIndex) {
            0 -> Config.harmonizerPreset = index
            else -> Config.radioPreset = index
        }
        audioSystem.selectFXChainPreset()
    }

    fun setVocalVolume(volume: Float) {
        audioSystem.vocalGainNode.gain = volume
    }

    fun setBeatVolume(volume: Float) {
        audioSystem.beatGainNode.gain = volume
    }

    fun startExport() {
        togglePlayback()
        stopAudioSystem()

        viewModelScope.launch {
            emitSideEffect(FXEditingSideEffect.StartExport)
            withContext(Dispatchers.Default) {
                audioSystem.renderMix()
            }
            emitSideEffect(FXEditingSideEffect.FinishExport)

            emitViewState(
                FXEditingViewState.Ready(
                    playbackButtonTitle = "Start Playback",
                    fxSwitchChecked = audioSystem.applyFXChain,
                    fxSelectedIdx = Config.selectedFXChainIndex,
                    presetSelectedIdx = if (Config.selectedFXChainIndex == 0) Config.harmonizerPreset else Config.radioPreset,
                    vocalVolume = audioSystem.vocalGainNode.gain,
                    beatVolume = audioSystem.beatGainNode.gain
                ))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FXEditingViewModel(
                    audioSystem = FXEditingAudioSystem(),
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}