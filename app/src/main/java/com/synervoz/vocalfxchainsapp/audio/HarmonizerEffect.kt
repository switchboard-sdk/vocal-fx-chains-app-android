package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboard.sdk.audiographnodes.BusSplitterNode
import com.synervoz.switchboard.sdk.audiographnodes.GainNode
import com.synervoz.switchboard.sdk.audiographnodes.MixerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.AutotuneNode
import com.synervoz.switchboardsuperpowered.audiographnodes.PitchShiftNode
import com.synervoz.switchboardsuperpowered.audiographnodes.ReverbNode
import com.synervoz.vocalfxchainsapp.Config

class HarmonizerEffect : FXChain() {
    private val avpcNode = AutotuneNode()
    private val busSplitterNode = BusSplitterNode()
    private val lowPitchShiftNode = PitchShiftNode()
    private val lowPitchShiftGainNode = GainNode()
    private val highPitchShiftNode = PitchShiftNode()
    private val highPitchShiftGainNode = GainNode()
    private val mixerNode = MixerNode()
    private val reverbNode = ReverbNode()

    fun setLowPreset() {
        avpcNode.isEnabled = true
        avpcNode.speed = AutotuneNode.TunerSpeed.MEDIUM
        avpcNode.range = AutotuneNode.TunerRange.WIDE
        avpcNode.scale = AutotuneNode.TunerScale.CMAJOR

        lowPitchShiftNode.isEnabled = true
        lowPitchShiftNode.pitchShiftCents = -400
        lowPitchShiftGainNode.gain = 0.4f

        highPitchShiftNode.isEnabled = true
        highPitchShiftNode.pitchShiftCents = 400
        highPitchShiftGainNode.gain = 0.4f

        reverbNode.isEnabled = true
        reverbNode.mix = 0.008f
        reverbNode.width = 0.7f
        reverbNode.damp = 0.5f
        reverbNode.roomSize = 0.5f
        reverbNode.predelayMs = 10.0f
    }

    fun setHighPreset() {
        avpcNode.isEnabled = true
        avpcNode.speed = AutotuneNode.TunerSpeed.EXTREME
        avpcNode.range = AutotuneNode.TunerRange.WIDE
        avpcNode.scale = AutotuneNode.TunerScale.CMAJOR

        lowPitchShiftNode.isEnabled = true
        lowPitchShiftNode.pitchShiftCents = -400
        lowPitchShiftGainNode.gain = 1.0f

        highPitchShiftNode.isEnabled = true
        highPitchShiftNode.pitchShiftCents = 400
        highPitchShiftGainNode.gain = 1.0f

        reverbNode.isEnabled = true
        reverbNode.mix = 0.015f
        reverbNode.width = 0.7f
        reverbNode.damp = 0.5f
        reverbNode.roomSize = 0.75f
        reverbNode.predelayMs = 10.0f
    }

    init {
        when (Config.harmonizerPreset) {
            0 -> setLowPreset()
            else -> setHighPreset()
        }

        audioGraph.addNode(avpcNode)
        audioGraph.addNode(busSplitterNode)
        audioGraph.addNode(lowPitchShiftNode)
        audioGraph.addNode(lowPitchShiftGainNode)
        audioGraph.addNode(highPitchShiftNode)
        audioGraph.addNode(highPitchShiftGainNode)
        audioGraph.addNode(mixerNode)
        audioGraph.addNode(reverbNode)

        audioGraph.connect(audioGraph.inputNode, avpcNode)
        audioGraph.connect(avpcNode, busSplitterNode)
        audioGraph.connect(busSplitterNode, lowPitchShiftNode)
        audioGraph.connect(busSplitterNode, highPitchShiftNode)
        audioGraph.connect(busSplitterNode, mixerNode)
        audioGraph.connect(lowPitchShiftNode, lowPitchShiftGainNode)
        audioGraph.connect(lowPitchShiftGainNode, mixerNode)
        audioGraph.connect(highPitchShiftNode, highPitchShiftGainNode)
        audioGraph.connect(highPitchShiftGainNode, mixerNode)
        audioGraph.connect(mixerNode, reverbNode)
        audioGraph.connect(reverbNode, audioGraph.outputNode)

        audioGraph.start()
    }
}