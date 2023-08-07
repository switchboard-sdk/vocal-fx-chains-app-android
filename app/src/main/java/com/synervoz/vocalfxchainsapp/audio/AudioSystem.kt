package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph

abstract class AudioSystem {
    val audioEngine = AudioEngine()
    val audioGraph = AudioGraph()

    fun start() {
        audioEngine.start(audioGraph)
    }

    fun stop() {
        audioEngine.stop()
    }
}