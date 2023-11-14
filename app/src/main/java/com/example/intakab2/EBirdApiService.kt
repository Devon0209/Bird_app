package com.example.intakab2

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface EBirdApiService {
    // Service for getting nearby hotspots
    @Headers("X-eBirdApiToken: tsmqqtr4glk1")
    @GET("ref/hotspot/geo")
    suspend fun getNearbyHotspots(
        @Query("back") back: Int,
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("dist") radius: Int = 25, // Default radius of 50 kilometers
        @Query("fmt") format: String = "json" // Default format is JSON
    ): List<Hotspot>?

    data class Hotspot(
        val locId: String,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val countryCode: String,
        val countryName: String,
        val subnational1Name: String,
        val subnational1Code: String,
        val subnational2Code: String,
        val subnational2Name: String,
        val isHotspot: Boolean,
        val hierarchicalName: String,
        val locID: String
    )



}
