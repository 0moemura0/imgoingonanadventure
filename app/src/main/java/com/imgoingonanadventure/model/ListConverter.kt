package com.imgoingonanadventure.model

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object EventListConverter {

    @TypeConverter
    fun listOfVisitsToString(data: List<Event>): String {
        return Json.encodeToString(data)
    }

    @TypeConverter
    fun stringToListOfEvents(data: String): List<Event> {
        return Json.decodeFromString<List<Event>>(data)
    }
}
