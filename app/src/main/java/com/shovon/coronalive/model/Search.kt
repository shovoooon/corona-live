package com.shovon.coronalive.model


import com.google.gson.annotations.SerializedName

data class Search(
    val active: Int,
    val cases: Int,
    val casesPerOneMillion: Int,
    val country: String,
    val countryInfo: CountryInfoX,
    val critical: Int,
    val deaths: Int,
    val deathsPerOneMillion: Int,
    val recovered: Int,
    val tests: Int,
    val testsPerOneMillion: Int,
    val todayCases: Int,
    val todayDeaths: Int,
    val updated: Long
)
