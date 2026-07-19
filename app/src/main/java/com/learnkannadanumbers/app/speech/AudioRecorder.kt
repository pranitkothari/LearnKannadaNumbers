package com.learnkannadanumbers.app.speech

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/**
 * Records a single short utterance from the mic at 16kHz mono (what Whisper expects),
 * auto-stopping once the user has spoken and then gone quiet again, or after a hard
 * time cap - whichever comes first. Caller must have RECORD_AUDIO granted already.
 */
class AudioRecorder(
    private val sampleRate: Int = 16_000,
    private val maxDurationMs: Long = 4_000,
    private val silenceTimeoutMs: Long = 1_600,
    // RMS (0..32767 scale) below this is considered silence. Number words are short and
    // loud relative to background noise, so a fairly conservative threshold works fine.
    private val silenceRmsThreshold: Double = 600.0,
) {
    @SuppressLint("MissingPermission") // caller guarantees RECORD_AUDIO is granted
    suspend fun record(): FloatArray = withContext(Dispatchers.IO) {
        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        require(minBufferSize > 0) { "Device does not support 16kHz mono PCM recording" }

        val chunkSamples = sampleRate / 20 // 50ms chunks
        val bufferSize = maxOf(minBufferSize, chunkSamples * 4)

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
        )
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            error("Failed to initialize the microphone")
        }

        val samples = ArrayList<Float>(sampleRate * 2)
        val chunk = ShortArray(chunkSamples)

        try {
            audioRecord.startRecording()

            var elapsedMs = 0L
            var silenceMs = 0L
            var hasSpoken = false
            val chunkDurationMs = (chunkSamples * 1000L) / sampleRate

            while (elapsedMs < maxDurationMs) {
                val read = audioRecord.read(chunk, 0, chunk.size)
                if (read <= 0) break

                var sumSquares = 0.0
                for (i in 0 until read) {
                    val s = chunk[i].toDouble()
                    sumSquares += s * s
                    samples.add(chunk[i] / 32768f)
                }
                val rms = sqrt(sumSquares / read)

                if (rms >= silenceRmsThreshold) {
                    hasSpoken = true
                    silenceMs = 0
                } else if (hasSpoken) {
                    silenceMs += chunkDurationMs
                }

                elapsedMs += chunkDurationMs

                if (hasSpoken && silenceMs >= silenceTimeoutMs) break
            }
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }

        samples.toFloatArray()
    }
}
