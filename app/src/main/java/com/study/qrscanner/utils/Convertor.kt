package com.study.qrscanner.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object Convertor {

    fun <T> toJason(data: T): String {
        val gson = Gson()
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTut: String = gson.toJson(data)
        println(jsonTut)
        return gsonPretty.toJson(data)
    }

    inline fun <reified T> jsonToObject(jsonData: String): T {
        return Gson().fromJson(jsonData, T::class.java)
    }

}