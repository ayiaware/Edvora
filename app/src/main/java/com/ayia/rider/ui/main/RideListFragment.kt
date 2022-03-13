package com.ayia.rider.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ayia.rider.GLOBAL_TAG
import com.ayia.rider.MainViewModel
import com.ayia.rider.adapters.RidesAdapter
import com.ayia.rider.api.RidesApiResponse
import com.ayia.rider.databinding.FragmentRideListBinding
import timber.log.Timber

/**
 * A placeholder fragment containing a simple view.
 */
class RideListFragment : Fragment() {

    private val TAG: String =
        GLOBAL_TAG + " " + RideListFragment::class.java.simpleName

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentRideListBinding? = null
    private var _adapter: RidesAdapter? = null

    private val adapter get() = _adapter!!
    private val binding get() = _binding!!

    private var sectionNumber : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(
            requireActivity()
        ).get(MainViewModel::class.java)

        sectionNumber =  arguments?.getInt(ARG_SECTION_NUMBER) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRideListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.isLoading = true

        _adapter = RidesAdapter(RidesAdapter.RideDiff(), mainViewModel)

        binding.rvRides.adapter = _adapter

        submitListToUi(
            when(sectionNumber ){
                0-> mainViewModel.nearestRides
                1-> mainViewModel.futureRides
                else -> mainViewModel.pastRides
            }
        )
    }

    private fun submitListToUi(response: LiveData<RidesApiResponse>){

        Timber.tag(TAG).d("submitListToUi")

        response.observe(viewLifecycleOwner, Observer {

            binding.isLoading = false

            if (it.rides != null) {
                adapter.submitList(it.rides)
                binding.isEmpty = it.rides.isEmpty()
                Timber.tag(TAG).d("rides != null false isEmpty ${it.rides.isEmpty()}")
            }
            else {
                binding.isEmpty = true
                Timber.tag(TAG).d("rides != null true Error ${it.error}")
            }
        })

    }
//
//    private fun submitListToUi(response: LiveData<List<Ride>>){
//
//        Timber.tag(TAG).d("submitListToUi")
//
//        response.observe(viewLifecycleOwner, Observer {
//
//            if (it != null)
//                adapter.submitList(it)
//            else
//                binding.isEmpty = true
//
//        })
//
//    }
//

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): RideListFragment {
            return RideListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }
}