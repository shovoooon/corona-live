package com.shovon.coronalive.model


import com.google.gson.annotations.SerializedName

data class GetLocationResponse(
    val error: Boolean,
    val id: Int,
    @SerializedName("last_seen")
    val lastSeen: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val phone: String
)