package com.ayia.rider.api

import com.ayia.rider.model.Ride

data class RidesApiResponse(
    val rides: List<Ride>? = null,
    val error : Throwable? = null
)
