package com.example.fitnesstracker.data.local.typeconverters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LatLngListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLatLngList(latLngList: List<LatLng>?): String? {
        return latLngList?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLatLngList(json: String?): List<LatLng>? {
        return json?.let {
            val type = object : TypeToken<List<LatLng>>() {}.type
            gson.fromJson(json, type)
        }
    }
}
