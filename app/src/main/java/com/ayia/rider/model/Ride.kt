package com.ayia.rider.model

data class Ride(
    val id:Int,
    val origin_station_code:Int,
    val station_path: List<Int>,
    val destination_station_code:Int,
    val date : String,
    val map_url : String,
    val state : String,
    val city: String
    )