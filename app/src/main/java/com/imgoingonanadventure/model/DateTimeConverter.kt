package com.imgoingonanadventure.model

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

object DateTimeConverter {
    private val formatter: DateTimeFormatter =
        ISODateTimeFormat.date() //!!!!! in database only day is using!!!!!!

    @TypeConverter
    @JvmStatic
    fun toDateTime(data: String?): DateTime? {
        return data?.let {
            formatter.parseDateTime(data)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromDateTime(data: DateTime?): String? {
        return data?.toString(formatter)
    }
}
