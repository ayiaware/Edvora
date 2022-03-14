package com.ayia.rider

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.ayia.rider.data.DataRepository

class ViewModelFactory(
    private val repository: DataRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
)  : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {

        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                 MainViewModel(repository, handle)
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    } as T
}
