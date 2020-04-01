package com.shovon.coronalive.model

data class CountriesResponse(
    val active: Int,
    val cases: Int,
    val casesPerOneMillion: Int,
    val country: String,
    val critical: Int,
    val deaths: Int,
    val recovered: Int,
    val todayCases: Int,
    val todayDeaths: Int
)