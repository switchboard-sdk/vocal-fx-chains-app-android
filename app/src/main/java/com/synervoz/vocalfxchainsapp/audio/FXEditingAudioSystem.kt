package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboard.sdk.audiograph.OfflineGraphRenderer
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.BusSelectNode
import com.synervoz.switchboard.sdk.audiographnodes.BusSplitterNode
import com.synervoz.switchboard.sdk.audiographnodes.GainNode
import com.synervoz.switchboard.sdk.audiographnodes.MixerNode
import com.synervoz.switchboard.sdk.audiographnodes.SubgraphProcessorNode
import com.synervoz.vocalfxchainsapp.Config
import kotlin.math.max

class FXEditingAudioSystem : AudioSystem() {
    val beatPlayerNode = AudioPlayerNode()
    val beatGainNode = GainNode()
    val vocalPlayerNode = AudioPlayerNode()
    val vocalGainNode = GainNode()
    val vocalPlayerSplitterNode = BusSplitterNode()
    val fxChainNode = SubgraphProcessorNode()
    val busSelectNode = BusSelectNode()
    val mixerNode = MixerNode()

    val offlineGraphRenderer = OfflineGraphRenderer()

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
        selectFXChainPreset()

        audioGraph.addNode(beatPlayerNode)
        audioGraph.addNode(beatGainNode)
        audioGraph.addNode(vocalPlayerNode)
        audioGraph.addNode(vocalGainNode)
        audioGraph.addNode(vocalPlayerSplitterNode)
        audioGraph.addNode(fxChainNode)
        audioGraph.addNode(busSelectNode)
        audioGraph.addNode(mixerNode)

        audioGraph.connect(beatPlayerNode, beatGainNode)
        audioGraph.connect(beatGainNode, mixerNode)
        audioGraph.connect(vocalPlayerNode, vocalGainNode)
        audioGraph.connect(vocalGainNode, vocalPlayerSplitterNode)
        audioGraph.connect(vocalPlayerSplitterNode, fxChainNode)
        audioGraph.connect(fxChainNode, busSelectNode)
        audioGraph.connect(vocalPlayerSplitterNode, busSelectNode)
        audioGraph.connect(busSelectNode, mixerNode)
        audioGraph.connect(mixerNode, audioGraph.outputNode)

        beatPlayerNode.isLoopingEnabled = true
        vocalPlayerNode.isLoopingEnabled = true

        audioEngine.enableMicrophone(false)
    }

    fun selectFXChain() {
        when (Config.selectedFXChainIndex) {
            0 -> fxChainNode.setAudioGraph(harmonizer.audioGraph)
            else -> fxChainNode.setAudioGraph(radio.audioGraph)
        }
    }

    fun selectFXChainPreset() {
        when (Config.selectedFXChainIndex) {
            0 -> {
                when (Config.harmonizerPreset) {
                    0 -> harmonizer.setLowPreset()
                    else -> harmonizer.setHighPreset()
                }
            }
            else -> {
                when (Config.radioPreset) {
                    0 -> radio.setLowPreset()
                    else -> radio.setHighPreset()
                }
            }
        }
    }

    fun startPlayback() {
        beatPlayerNode.play()
        vocalPlayerNode.play()
    }

    fun stopPlayback() {
        beatPlayerNode.stop()
        vocalPlayerNode.stop()
    }

    val isPlaying: Boolean
        get() = beatPlayerNode.isPlaying

    fun renderMix() {
        val sampleRate = max(vocalPlayerNode.getSourceSampleRate(), beatPlayerNode.getSourceSampleRate())
        vocalPlayerNode.position = 0.0
        beatPlayerNode.position = 0.0
        offlineGraphRenderer.setSampleRate(sampleRate)
        offlineGraphRenderer.setMaxNumberOfSecondsToRender(vocalPlayerNode.getDuration())

        startPlayback()
        offlineGraphRenderer.processGraph(audioGraph, Config.finalMixRecordingFile, Config.recordingFormat)
        stopPlayback()
    }
}