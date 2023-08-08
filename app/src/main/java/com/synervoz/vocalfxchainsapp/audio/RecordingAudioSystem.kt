package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboard.sdk.audioengine.PerformanceMode
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.BusSelectNode
import com.synervoz.switchboard.sdk.audiographnodes.BusSplitterNode
import com.synervoz.switchboard.sdk.audiographnodes.MixerNode
import com.synervoz.switchboard.sdk.audiographnodes.MuteNode
import com.synervoz.switchboard.sdk.audiographnodes.RecorderNode
import com.synervoz.switchboard.sdk.audiographnodes.SubgraphProcessorNode
import com.synervoz.vocalfxchainsapp.Config

class RecordingAudioSystem : AudioSystem() {
    val inputSplitterNode = BusSplitterNode()
    val inputRecorderNode = RecorderNode()
    val fxChainNode = SubgraphProcessorNode()
    val busSelectNode = BusSelectNode()
    val muteNode = MuteNode()
    val beatPlayerNode = AudioPlayerNode()
    val mixerNode = MixerNode()

    val harmonizer = HarmonizerEffect()
    val radio = RadioEffect()

    var applyFXChain: Boolean = Config.applyFXChain
        set(value) {
            field = value
            busSelectNode.selectedBus = if (value) 0 else 1
            Config.applyFXChain = value
        }

    init {
        busSelectNode.selectedBus = if (applyFXChain) 0 else 1
        selectFXChain()

        audioGraph.addNode(inputSplitterNode)
        audioGraph.addNode(inputRecorderNode)
        audioGraph.addNode(fxChainNode)
        audioGraph.addNode(busSelectNode)
        audioGraph.addNode(muteNode)
        audioGraph.addNode(beatPlayerNode)
        audioGraph.addNode(mixerNode)

        audioGraph.connect(audioGraph.inputNode, inputSplitterNode)
        audioGraph.connect(inputSplitterNode, fxChainNode)
        audioGraph.connect(fxChainNode, busSelectNode)
        audioGraph.connect(inputSplitterNode, busSelectNode)
        audioGraph.connect(inputSplitterNode, inputRecorderNode)
        audioGraph.connect(busSelectNode, muteNode)
        audioGraph.connect(beatPlayerNode, mixerNode)
        audioGraph.connect(muteNode, mixerNode)
        audioGraph.connect(mixerNode, audioGraph.outputNode)

        beatPlayerNode.isLoopingEnabled = true

        muteNode.isMuted = true

        audioEngine.enableMicrophone(true)
    }

    fun enableLiveMonitoring(enable: Boolean) {
        muteNode.isMuted = !enable
    }

    fun selectFXChain() {
        when (Config.selectedFXChainIndex) {
            0 -> fxChainNode.setAudioGraph(harmonizer.audioGraph)
            else -> fxChainNode.setAudioGraph(radio.audioGraph)
        }
    }

    fun startRecording() {
        inputRecorderNode.start()
    }

    fun stopRecording() {
        val path = Config.cleanVocalRecordingFilePath
        inputRecorderNode.stop(path, Config.recordingFormat)
    }

    val isRecording: Boolean
        get() = inputRecorderNode.isRecording

    fun startBeat() {
        beatPlayerNode.play()
    }

    fun stopBeat() {
        beatPlayerNode.stop()
    }

    val isBeatPlaying: Boolean
        get() = beatPlayerNode.isPlaying
}