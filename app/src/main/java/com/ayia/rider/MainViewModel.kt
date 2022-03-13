package com.ayia.rider

import androidx.lifecycle.*
import com.ayia.rider.api.RidesApiResponse
import com.ayia.rider.data.DataRepository
import com.ayia.rider.model.Ride
import com.ayia.rider.model.User
import timber.log.Timber

class MainViewModel (private val repository : DataRepository,
                     private val savedStateHandle: SavedStateHandle) : ViewModel()  {

    private val TAG: String =
        GLOBAL_TAG + " " + MainViewModel::class.java.simpleName


    private val defaultFilter = arrayOf("", "")

    val filterQuery = MutableLiveData(defaultFilter)

    val userResponse = repository.getUserMutableLiveData()



    private val ridesApiResponse : MutableLiveData<RidesApiResponse> =  repository.getRidesMutableLiveData()

    val nearestRides : MutableLiveData<RidesApiResponse> = userResponse.switchMap { userResponse ->

        ridesApiResponse.switchMap { ridesResponse ->

            if (ridesResponse.rides != null) {

                Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                val list = mutableListOf<Ride>()

                if (userResponse.user != null) {

                    Timber.tag(TAG).d(
                        "user != null true Name ${userResponse.user.name} ProfileUrl ${userResponse.user.url} StationCode ${userResponse.user.station_code}")

                    for (r in ridesResponse.rides){

                        if (r.station_path.contains(userResponse.user.station_code)) {
                            list.add(r)
                        }

                    }

                }

                MutableLiveData(RidesApiResponse(list))

            }
            else {
                Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                MutableLiveData(ridesResponse)
            }

        }

    } as MutableLiveData<RidesApiResponse>

    val futureRides : MutableLiveData<RidesApiResponse> = userResponse.switchMap { userResponse ->

        ridesApiResponse.switchMap { ridesResponse ->

            if (ridesResponse.rides != null) {

                Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                val list = mutableListOf<Ride>()

                if (userResponse.user != null) {

                    Timber.tag(TAG).d(
                        "user != null true Name ${userResponse.user.name} ProfileUrl ${userResponse.user.url} StationCode ${userResponse.user.station_code}")

                  val sortedRides =   ridesResponse.rides.sortedBy { getMilliFromDate(it.date) }

                    for (r in sortedRides){

                        if (getMilliFromDate(r.date) > System.currentTimeMillis()) {
                            list.add(r)
                        }

                    }

                }

                MutableLiveData(RidesApiResponse(list))

            }
            else {
                Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                MutableLiveData(ridesResponse)
            }

        }

    } as MutableLiveData<RidesApiResponse>

    val pastRides : MutableLiveData<RidesApiResponse> = userResponse.switchMap { userResponse ->

        ridesApiResponse.switchMap { ridesResponse ->

            if (ridesResponse.rides != null) {

                Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                val list = mutableListOf<Ride>()

                if (userResponse.user != null) {

                    Timber.tag(TAG).d(
                        "user != null true Name ${userResponse.user.name} ProfileUrl ${userResponse.user.url} StationCode ${userResponse.user.station_code}")

                    val sortedRides =   ridesResponse.rides.sortedBy { getMilliFromDate(it.date) }

                    for (r in sortedRides){

                        if (getMilliFromDate(r.date) < System.currentTimeMillis()) {
                            list.add(r)
                        }

                    }

                }

                MutableLiveData(RidesApiResponse(list))

            }
            else {
                Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                MutableLiveData(ridesResponse)
            }

        }

    } as MutableLiveData<RidesApiResponse>



    private val _index = MutableLiveData<Int>()

    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}