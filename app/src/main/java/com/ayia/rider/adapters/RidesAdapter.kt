package com.ayia.rider.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ayia.rider.GLOBAL_TAG
import com.ayia.rider.MainViewModel
import com.ayia.rider.R
import com.ayia.rider.databinding.RowRideBinding
import com.ayia.rider.model.Ride

class RidesAdapter(
    diffCallback: DiffUtil.ItemCallback<Ride>,
    private val viewModel: MainViewModel
) : ListAdapter<Ride, RideViewHolder?>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        return RideViewHolder.create(parent, viewModel)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        holder.binding.ride = getItem(position)
        holder.binding.executePendingBindings()
    }

    class RideDiff : DiffUtil.ItemCallback<Ride>() {
        override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem.id != newItem.id
        }
    }


    companion object {
        private val TAG: String = (GLOBAL_TAG + " "
                + RidesAdapter::class.java.simpleName)
    }


}


class RideViewHolder private constructor(val binding: RowRideBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(
            parent: ViewGroup, viewModel: MainViewModel
        ): RideViewHolder {
            val binding: RowRideBinding = DataBindingUtil
                .inflate(
                    LayoutInflater.from(parent.context), R.layout.row_ride,
                    parent, false
                )
            binding.viewModel = viewModel
            return RideViewHolder(binding)
        }
    }

}