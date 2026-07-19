package com.learnkannadanumbers.app.speech

import android.content.Context
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig

/**
 * Offline speech-to-text via sherpa-onnx running a quantized, multilingual
 * Whisper-tiny model. Model files are read from assets/sherpa-onnx-whisper-tiny/
 * (see README.md for how to obtain them - they are not committed to the repo).
 */
class SpeechRecognizerManager(context: Context) {

    private val recognizer: OfflineRecognizer

    init {
        val modelDir = "sherpa-onnx-whisper-tiny"
        val config = OfflineRecognizerConfig(
            modelConfig = OfflineModelConfig(
                whisper = OfflineWhisperModelConfig(
                    encoder = "$modelDir/tiny-encoder.int8.onnx",
                    decoder = "$modelDir/tiny-decoder.int8.onnx",
                    language = "kn",
                    task = "transcribe",
                ),
                tokens = "$modelDir/tiny-tokens.txt",
                modelType = "whisper",
                numThreads = 2,
                provider = "cpu",
            ),
        )
        recognizer = OfflineRecognizer(context.assets, config)
    }

    /** Blocking; call from a background thread/coroutine. */
    fun transcribe(samples: FloatArray, sampleRate: Int = 16_000): String {
        val stream = recognizer.createStream()
        try {
            stream.acceptWaveform(samples, sampleRate)
            recognizer.decode(stream)
            return recognizer.getResult(stream).text
        } finally {
            stream.release()
        }
    }

    fun release() {
        recognizer.release()
    }
}
