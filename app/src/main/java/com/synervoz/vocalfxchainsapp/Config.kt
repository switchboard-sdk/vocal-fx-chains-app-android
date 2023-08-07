package com.synervoz.vocalfxchainsapp

import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.SwitchboardSDK

object Config {
    val clientID = "Synervoz"
    val clientSecret = "VocalFXChainsApp"
    val superpoweredLicenseKey = "ExampleLicenseKey-WillExpire-OnNextUpdate"

    val cleanVocalRecordingFilePath: String
        get() = SwitchboardSDK.getTemporaryDirectoryPath() + "vocal_recording_clean.wav"
    val recordingFormat = Codec.WAV

    val finalMixRecordingFile: String
        get() = SwitchboardSDK.getTemporaryDirectoryPath() + "final_mix.wav"

    var applyFXChain = false
    var selectedFXChainIndex = 0
    var harmonizerPreset = 0
    var radioPreset = 0
}