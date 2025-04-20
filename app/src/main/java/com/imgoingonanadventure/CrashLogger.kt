package com.imgoingonanadventure

import android.content.Context
import android.util.Log
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import kotlin.system.exitProcess


class CrashLogger(private val context: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        saveCrashLog(context, throwable)
    }

    private fun saveCrashLog(context: Context, throwable: Throwable) {
        val logFile = getLogFile(context)
        try {
            FileOutputStream(logFile, true).use { fos ->
                PrintWriter(fos).use { writer ->
                    writer.println(getCurrentDate())
                    throwable.printStackTrace(writer)
                    Log.e(LOG_TAG, "Crash log saved to " + logFile.absolutePath)
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Failed to save crash log", e)
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }

    private fun getLogFile(context: Context): File {
        val file = File(context.cacheDir, LOG_FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun getCurrentDate(): String {
        return DateTime.now().toString("yyyy-MM-dd")
    }

    private companion object {
        const val LOG_FILE_NAME = "logs.log"
        const val LOG_TAG = "LOG_TAG"
    }
}