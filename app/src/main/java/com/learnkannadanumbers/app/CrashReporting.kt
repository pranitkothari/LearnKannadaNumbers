package com.learnkannadanumbers.app

import android.app.Application
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Writes any uncaught exception's full stack trace to internal storage before
 * letting the crash proceed normally, so it can be displayed on the next
 * launch instead of just the generic system "app crashed" dialog.
 */
class KannadaNumbersApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))
                lastCrashFile(this).writeText(writer.toString())
            } catch (_: Throwable) {
                // Best-effort; don't let crash reporting itself block the real crash handling.
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}

fun lastCrashFile(context: android.content.Context): File =
    File(context.filesDir, "last_crash.txt")
