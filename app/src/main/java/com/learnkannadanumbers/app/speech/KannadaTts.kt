package com.learnkannadanumbers.app.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Offline Kannada text-to-speech via espeak-ng (bundled as a native library,
 * see README.md "Setting up offline TTS" for how to vendor it). espeak-ng
 * needs its voice/phoneme data on a real filesystem path, so on first run we
 * copy assets/espeak-ng-data/ out to internal storage.
 */
class KannadaTts(private val context: Context) {

    private var sampleRate: Int = 0
    private var ready = false

    suspend fun init() = withContext(Dispatchers.IO) {
        if (ready) return@withContext
        val dataDir = ensureDataExtracted()
        // espeak-ng expects the *parent* of an "espeak-ng-data" directory.
        sampleRate = nativeInit(dataDir.parentFile!!.absolutePath)
        check(sampleRate > 0) { "espeak-ng initialization failed" }
        val voiceResult = nativeSetVoice("kn")
        check(voiceResult == 0) { "espeak-ng has no Kannada (kn) voice available (rc=$voiceResult)" }
        ready = true
    }

    /** Synthesizes and plays [text] to completion. Call after [init]. */
    suspend fun speak(text: String) = withContext(Dispatchers.IO) {
        check(ready) { "KannadaTts.init() must complete before speak()" }
        val pcm = nativeSynthesize(text)
        if (pcm.isNotEmpty()) {
            playPcm(pcm, sampleRate)
        }
    }

    private fun playPcm(pcm: ShortArray, rate: Int) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            rate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(rate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(maxOf(minBufferSize, pcm.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        try {
            track.write(pcm, 0, pcm.size)
            track.play()
            // MODE_STATIC playback duration is deterministic; block until done
            // so callers can safely sequence multiple speak() calls.
            val durationMs = (pcm.size * 1000L) / rate
            Thread.sleep(durationMs + 100)
        } finally {
            track.stop()
            track.release()
        }
    }

    private fun ensureDataExtracted(): File {
        val destRoot = File(context.filesDir, "espeak-ng-data")
        val markerFile = File(context.filesDir, ".espeak_data_extracted")
        if (markerFile.exists() && destRoot.exists()) {
            return destRoot
        }
        destRoot.deleteRecursively()
        destRoot.mkdirs()
        copyAssetDir("espeak-ng-data", destRoot)
        markerFile.writeText("1")
        return destRoot
    }

    /** [assetPath] must be a non-empty directory under assets/. */
    private fun copyAssetDir(assetPath: String, destDir: File) {
        val entries = context.assets.list(assetPath) ?: emptyArray()
        destDir.mkdirs()
        for (entry in entries) {
            val childAssetPath = "$assetPath/$entry"
            val childDest = File(destDir, entry)
            val childEntries = context.assets.list(childAssetPath)
            if (childEntries != null && childEntries.isNotEmpty()) {
                copyAssetDir(childAssetPath, childDest)
            } else {
                context.assets.open(childAssetPath).use { input ->
                    childDest.outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
    }

    fun release() {
        if (ready) {
            nativeTerminate()
            ready = false
        }
    }

    private external fun nativeInit(dataPath: String): Int
    private external fun nativeSetVoice(voiceName: String): Int
    private external fun nativeSynthesize(text: String): ShortArray
    private external fun nativeTerminate()

    companion object {
        init {
            System.loadLibrary("espeak_bridge")
        }
    }
}
