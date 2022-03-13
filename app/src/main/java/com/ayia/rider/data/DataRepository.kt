package com.ayia.rider.data

import androidx.lifecycle.MutableLiveData
import com.ayia.rider.GLOBAL_TAG
import com.ayia.rider.api.RetrofitInstance
import com.ayia.rider.api.RidesApiResponse
import com.ayia.rider.api.ApiService
import com.ayia.rider.api.UserApiResponse
import com.ayia.rider.model.Ride
import com.ayia.rider.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DataRepository {

    private val TAG: String =
        GLOBAL_TAG + " " + DataRepository::class.java.simpleName

    val ridesLiveData = MutableLiveData<RidesApiResponse>()

    val userLiveData = MutableLiveData<UserApiResponse>()


    fun getRidesMutableLiveData(): MutableLiveData<RidesApiResponse> {

        val endpoint: ApiService? = RetrofitInstance().getRetrofitService()

        val call: Call<List<Ride>?>? = endpoint?.getRides()

        call?.enqueue(object : Callback<List<Ride>?> {
            override fun onResponse(call: Call<List<Ride>?>, response: Response<List<Ride>?>) {

                Timber.tag(TAG).d("onResponse  isSuccessful ${response.isSuccessful} Response ${response}")

                if (response.isSuccessful) {

                    ridesLiveData.postValue(RidesApiResponse(rides = response.body()))

                }
            }

            override fun onFailure(call: Call<List<Ride>?>, t: Throwable) {

                Timber.tag(TAG).d("onFailure Error $t")

                ridesLiveData.postValue(RidesApiResponse(error = t))
            }
        })

        return ridesLiveData
    }


    fun getUserMutableLiveData(): MutableLiveData<UserApiResponse> {

        val endpoint: ApiService? = RetrofitInstance().getRetrofitService()

        val call: Call<User?>? = endpoint?.getUser()

        call?.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {

                Timber.tag(TAG).d("onResponse  isSuccessful ${response.isSuccessful} Response ${response}")

                if (response.isSuccessful) {

                    userLiveData.postValue(UserApiResponse(user = response.body()))

                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {

                Timber.tag(TAG).d("onFailure Error $t")

                userLiveData.postValue(UserApiResponse(error = t))
            }
        })

        return userLiveData
    }

}