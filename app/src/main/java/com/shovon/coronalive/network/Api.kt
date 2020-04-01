package com.shovon.coronalive.network
import com.shovon.coronalive.model.*
import retrofit2.Call
import retrofit2.http.*

interface Api {

@FormUrlEncoded
@POST("corona/index.php")
fun worldInfo(
    @Field("all") all:String
):Call<WorldResponse>

    @FormUrlEncoded
    @POST("corona/index.php")
    fun countriesInfo(
        @Field("countries") countries:String
    ):Call<List<CountriesResponse>>

    @FormUrlEncoded
    @POST("corona/index.php")
    fun searchInfo(
        @Field("countryName") countryName:String
    ):Call<SearchResponse>

    @FormUrlEncoded
    @POST("corona/faq.php")
    fun faqInfo(
        @Field("table") table:String
    ):Call<List<FaqResponse>>

    @FormUrlEncoded
    @POST("corona/register.php")
    fun register(
        @Field("name") name:String,
        @Field("phone") phone:String
    ):Call<RegisterResponse>

    @FormUrlEncoded
    @POST("corona/update_location.php")
    fun updateLocation(
        @Field("phone") phone:String,
        @Field("lat") lat:String,
        @Field("lon") lon:String
    ):Call<UpdateLocationResponse>

    @FormUrlEncoded
    @POST("corona/get_location.php")
    fun getLocation(
        @Field("phone") phone:String
    ):Call<GetLocationResponse>

}

