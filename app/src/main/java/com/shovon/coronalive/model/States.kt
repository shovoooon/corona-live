package com.shovon.coronalive.model

data class States(
    val active: Int,
    val cases: Int,
    val deaths: Int,
    val state: String,
    val tests: Int,
    val testsPerOneMillion: Int,
    val todayCases: Int,
    val todayDeaths: Int
)
