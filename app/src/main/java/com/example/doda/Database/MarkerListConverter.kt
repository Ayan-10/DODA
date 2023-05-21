package com.example.doda.Database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import androidx.room.TypeConverter
import com.example.doda.Model.Marker

class MarkerListConverter {

    var gson: Gson = Gson()

    @TypeConverter
    fun stringToMarkerList(data: String?): List<Marker?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Marker?>?>() {}.getType()
        return gson.fromJson(/* json = */ data, /* classOfT = */ listType)
    }

    @TypeConverter
    fun markerListToString(someObjects: List<Marker?>?): String? {
        return gson.toJson(someObjects)
    }
}