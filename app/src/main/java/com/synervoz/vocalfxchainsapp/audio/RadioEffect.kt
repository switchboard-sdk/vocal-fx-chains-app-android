package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboardsuperpowered.audiographnodes.FilterNode
import com.synervoz.switchboardsuperpowered.audiographnodes.GuitarDistortionNode
import com.synervoz.switchboardsuperpowered.audiographnodes.ReverbNode
import com.synervoz.vocalfxchainsapp.Config

class RadioEffect : FXChain() {
    private val bandpassFilterNode = FilterNode()
    private val distortionNode = GuitarDistortionNode()
    private val reverbNode = ReverbNode()

    fun setLowPreset() {
        bandpassFilterNode.isEnabled = true
        bandpassFilterNode.filterType = FilterNode.FilterType.Bandlimited_Bandpass
        bandpassFilterNode.frequency = 3648.0f
        bandpassFilterNode.octave = 0.7f

        reverbNode.isEnabled = true
        reverbNode.mix = 0.008f
        reverbNode.width = 0.7f
        reverbNode.damp = 0.5f
        reverbNode.roomSize = 0.5f
        reverbNode.predelayMs = 10.0f
    }

    fun setHighPreset() {
        bandpassFilterNode.isEnabled = true
        bandpassFilterNode.filterType = FilterNode.FilterType.Bandlimited_Bandpass
        bandpassFilterNode.frequency = 3648.0f
        bandpassFilterNode.octave = 0.3f

        reverbNode.isEnabled = true
        reverbNode.mix = 0.015f
        reverbNode.width = 0.7f
        reverbNode.damp = 0.5f
        reverbNode.roomSize = 0.75f
        reverbNode.predelayMs = 10.0f
    }

    init {
        when (Config.radioPreset) {
            0 -> setLowPreset()
            else -> setHighPreset()
        }

        audioGraph.addNode(bandpassFilterNode)
        audioGraph.addNode(distortionNode)
        audioGraph.addNode(reverbNode)

        audioGraph.connect(audioGraph.inputNode, bandpassFilterNode)
        audioGraph.connect(bandpassFilterNode, distortionNode)
        audioGraph.connect(distortionNode, reverbNode)
        audioGraph.connect(reverbNode, audioGraph.outputNode)

        audioGraph.start()
    }
}