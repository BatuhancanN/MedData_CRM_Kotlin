package com.meddata.batuhan.crm.adapters

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import java.util.Date

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DataTypeAdapter  : JsonDeserializer<Date>, JsonSerializer<Date> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Tarih formatı "yyyy-MM-dd" olarak ayarlandı

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return dateFormat.parse(json!!.asString)
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(dateFormat.format(src))
    }

}