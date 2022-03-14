package com.ayia.rider

import androidx.lifecycle.*
import com.ayia.rider.api.RidesApiResponse
import com.ayia.rider.data.DataRepository
import com.ayia.rider.model.Filter
import com.ayia.rider.model.Location
import com.ayia.rider.model.Option
import com.ayia.rider.model.Ride
import timber.log.Timber

class MainViewModel (repository : DataRepository,
                     private val savedStateHandle: SavedStateHandle) : ViewModel()  {

    private val TAG: String =
        GLOBAL_TAG + " " + MainViewModel::class.java.simpleName


    private val defaultFilter = arrayOf("", "")

    var filter = Filter("", "")

    val userResponse = repository.getUserMutableLiveData()

    private val ridesApiResponse =  repository.getRidesMutableLiveData()


    val nearestRides : MutableLiveData<RidesApiResponse> = savedStateHandle.getLiveData(
        FILTER_KEY_NEAREST,
        defaultFilter
    ).switchMap { filter ->

        val state = filter[0]
        val city = filter[1]

        userResponse.switchMap { userResponse ->

            val user = userResponse.user

            Timber.tag(TAG).d(
                "user != null true Name ${user?.name} ProfileUrl ${user?.url} StationCode ${user?.station_code}"
            )
            if (user == null) {

                MutableLiveData(RidesApiResponse(error = userResponse.error))

            }
            else{
                ridesApiResponse.switchMap { ridesResponse ->

                    if (ridesResponse.rides != null) {

                        Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                        val list = mutableListOf<Ride>()

                        if (city != "" && state != ""){
                            for (r in ridesResponse.rides) {
                                if(r.state ==  state && r.city == city)
                                    list.add(r)
                            }
                        }
                        else if(city != ""){
                            for (r in ridesResponse.rides) {
                                if(r.city ==  city)
                                    list.add(r)
                            }
                        }
                        else if(state != "" ){
                            for (r in ridesResponse.rides) {
                                if(r.state ==  state)
                                    list.add(r)
                            }
                        }
                        else if(city == "" && state == "") {
                            for (r in ridesResponse.rides) {
                                list.add(r)
                            }
                        }

                        MutableLiveData(RidesApiResponse(list.sortedBy { it.destination_station_code - user.station_code }))

                    } else {
                        Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                        MutableLiveData(ridesResponse)
                    }

                }

            }



        }
    } as MutableLiveData<RidesApiResponse>


    val futureRidesCount = MutableLiveData(0)

    val futureRides : MutableLiveData<RidesApiResponse> = savedStateHandle.getLiveData(
        FILTER_KEY_FUTURE,
        defaultFilter
    ).switchMap { filter ->

        val state = filter[0]
        val city = filter[1]

        userResponse.switchMap { userResponse ->

            val user = userResponse.user

            Timber.tag(TAG).d(
                "user != null true Name ${user?.name} ProfileUrl ${user?.url} StationCode ${user?.station_code}"
            )
            if (user == null) {

                MutableLiveData(RidesApiResponse(error = userResponse.error))

            }
            else{
                ridesApiResponse.switchMap { ridesResponse ->

                    if (ridesResponse.rides != null) {

                        Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                        val list = mutableListOf<Ride>()

                        for (r in ridesResponse.rides){

                            if (city != "" && state != ""){

                                if(r.state ==  state && r.city == city) {
                                    if (getMilliFromDate(r.date) > System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }

                            }
                            else if(city != ""){
                                if(r.city ==  city){
                                    if (getMilliFromDate(r.date) > System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }

                            }
                            else if(state != "" ){
                                if(r.state ==  state){
                                    if (getMilliFromDate(r.date) > System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }
                            }
                            else if(city == "" && state == "") {

                                if (getMilliFromDate(r.date) > System.currentTimeMillis()) {
                                    list.add(r)
                                }

                            }

                        }

                        futureRidesCount.value = list.size

                        MutableLiveData(RidesApiResponse(list.sortedBy { getMilliFromDate(it.date) }))



                    } else {
                        Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                        MutableLiveData(ridesResponse)
                    }

                }

            }



        }
    } as MutableLiveData<RidesApiResponse>


    val pastRidesCount = MutableLiveData(0)

    val pastRides : MutableLiveData<RidesApiResponse> = savedStateHandle.getLiveData(
        FILTER_KEY_PAST,
        defaultFilter
    ).switchMap { filter ->

        val state = filter[0]
        val city = filter[1]

        userResponse.switchMap { userResponse ->

            val user = userResponse.user

            Timber.tag(TAG).d(
                "user != null true Name ${user?.name} ProfileUrl ${user?.url} StationCode ${user?.station_code}"
            )
            if (user == null) {

                MutableLiveData(RidesApiResponse(error = userResponse.error))

            }
            else{
                ridesApiResponse.switchMap { ridesResponse ->

                    if (ridesResponse.rides != null) {

                        Timber.tag(TAG).d("Rides count ${ridesResponse.rides.size}")

                        val list = mutableListOf<Ride>()


                        for (r in ridesResponse.rides){

                            if (city != "" && state != ""){

                                if(r.state ==  state && r.city == city) {
                                    if (getMilliFromDate(r.date) < System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }

                            }
                            else if(city != ""){
                                if(r.city ==  city){
                                    if (getMilliFromDate(r.date) < System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }

                            }
                            else if(state != "" ){
                                if(r.state ==  state){
                                    if (getMilliFromDate(r.date) < System.currentTimeMillis()) {
                                        list.add(r)
                                    }
                                }
                            }
                            else if(city == "" && state == "") {

                                if (getMilliFromDate(r.date) < System.currentTimeMillis()) {
                                    list.add(r)
                                }

                            }

                        }

                        pastRidesCount.value = list.size


                        MutableLiveData(RidesApiResponse(list.sortedBy { getMilliFromDate(it.date) }))

                    } else {
                        Timber.tag(TAG).d("Rides is null Error ${ridesResponse.error}")

                        MutableLiveData(ridesResponse)
                    }

                }

            }



        }
    } as MutableLiveData<RidesApiResponse>



    val states : MutableLiveData<MutableList<Location>> = ridesApiResponse.switchMap {

        val states = mutableListOf<Location>()

        if (it.rides != null) {

            for (r in it.rides){

                states.add(Location(state = r.state))
            }
        }

        MutableLiveData(states)

    } as MutableLiveData<MutableList<Location>>


    val cities : MutableLiveData<MutableList<Location>> = ridesApiResponse.switchMap {

        val cities = mutableListOf<Location>()

        if (it.rides != null) {

            for (r in it.rides){

                cities.add(Location(state = r.state, city = r.city))
            }

        }

        MutableLiveData(cities)

    } as MutableLiveData<MutableList<Location>>



    fun setFilter(tabPosition :Int, option: Option){


        when (option.type) {
            FILTER_TYPE_STATE -> {
                filter.state = option.value
            }
            else -> {
                filter.city = option.value
            }
        }

        when(tabPosition){

            0 ->{
                savedStateHandle[FILTER_KEY_NEAREST] = arrayOf(filter.state, filter.city)
            }
            1 -> {
                savedStateHandle[FILTER_KEY_FUTURE] = arrayOf(filter.state, filter.city)
            }
            2 -> {
                savedStateHandle[FILTER_KEY_PAST]  = arrayOf(filter.state, filter.city)
            }
        }

    }



}