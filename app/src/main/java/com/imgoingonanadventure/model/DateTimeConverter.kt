package com.imgoingonanadventure.model

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

object  DateTimeConverter {
    private val formatter: DateTimeFormatter =
        ISODateTimeFormat.date() //!!!!! in database only day is using!!!!!!

    @TypeConverter
    @JvmStatic
    fun toDateTime(value: String?): DateTime? {
        return value?.let {
            formatter.parseDateTime(value)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromDateTime(date: DateTime?): String? {
        return date?.toString(formatter)
    }
}
