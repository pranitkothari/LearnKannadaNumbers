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

private fun breadcrumbFile(context: android.content.Context): File =
    File(context.filesDir, "last_breadcrumb.txt")

/**
 * Overwrites a small on-disk marker with [step] before a risky (often native/JNI)
 * call. Native crashes kill the process before any Java exception handler runs,
 * so this is the only way to see how far startup got when that happens - the
 * write itself completes (and is durable) before the crash, even if the crash
 * follows a few milliseconds later.
 */
fun writeBreadcrumb(context: android.content.Context, step: String) {
    try {
        breadcrumbFile(context).writeText(step)
    } catch (_: Throwable) {
        // Best-effort.
    }
}

fun readLastBreadcrumb(context: android.content.Context): String? {
    val file = breadcrumbFile(context)
    return if (file.exists()) file.readText() else null
}

fun clearBreadcrumb(context: android.content.Context) {
    breadcrumbFile(context).delete()
}
