package com.shovon.coronalive.model

data class CountriesResponse(
    val active: Int,
    val cases: Int,
    val casesPerOneMillion: Float,
    val country: String,
    val countryInfo: CountryInfo,
    val critical: Int,
    val deaths: Int,
    val deathsPerOneMillion: Float,
    val recovered: Int,
    val tests: Int,
    val testsPerOneMillion: Int,
    val todayCases: Int,
    val todayDeaths: Int,
    val updated: Long
)
