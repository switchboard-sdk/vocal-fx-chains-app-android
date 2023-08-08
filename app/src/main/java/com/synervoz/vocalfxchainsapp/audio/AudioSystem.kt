package com.synervoz.vocalfxchainsapp.audio

import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audioengine.PerformanceMode
import com.synervoz.switchboard.sdk.audiograph.AudioGraph

abstract class AudioSystem {
    val audioEngine = AudioEngine(performanceMode = PerformanceMode.LOW_LATENCY)
    val audioGraph = AudioGraph()

    fun start() {
        audioEngine.start(audioGraph)
    }

    fun stop() {
        audioEngine.stop()
    }
}