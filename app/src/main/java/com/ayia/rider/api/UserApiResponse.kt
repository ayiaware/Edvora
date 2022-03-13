package com.ayia.rider.api

import com.ayia.rider.model.User

data class UserApiResponse(
    val user: User? = null,
    val error : Throwable? = null
)
