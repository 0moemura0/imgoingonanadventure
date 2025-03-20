package com.imgoingonanadventure.data

import android.content.Context
import com.imgoingonanadventure.model.Event
import com.imgoingonanadventure.model.Route
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.InputStreamReader

class EventDataSource(private val applicationContext: Context) {

    fun getEventList(routeId: String): List<Event> {
        val fileName: String = when (routeId) {
            Route.RIVENDELL_TO_LOTHLORIEN.routeId -> {
                Route.RIVENDELL_TO_LOTHLORIEN.fileName
            }

            Route.LOTHLORIEN_TO_RAUROS.routeId -> {
                Route.LOTHLORIEN_TO_RAUROS.fileName
            }

            Route.RAUROS_TO_DOOM.routeId -> {
                Route.RAUROS_TO_DOOM.fileName
            }

            else -> {
                Route.BAG_END_TO_RIVENDELL.fileName
            }
        }
        val file: InputStream = applicationContext.assets.open(fileName)
        val jsonString = InputStreamReader(file).use {
            it.readText()
        }
        return Json.decodeFromString<List<Event>>(jsonString)
    }
}