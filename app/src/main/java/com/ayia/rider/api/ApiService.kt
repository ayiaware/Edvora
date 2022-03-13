package com.ayia.rider.api

import com.ayia.rider.RIDES_ENDPOINT
import com.ayia.rider.USER_ENDPOINT
import com.ayia.rider.model.Ride
import com.ayia.rider.model.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {


    @GET(RIDES_ENDPOINT)
    fun getRides(): Call<List<Ride>?>?

    @GET(USER_ENDPOINT)
    fun getUser(): Call<User?>?


}