package com.ayia.rider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        binding.btnRetry.setOnClickListener {

            binding.isLoading = true
            mainViewModel.refresh()
        }
    }

    private fun submitListToUi(response: LiveData<RidesApiResponse>){

        Timber.tag(TAG).d("submitListToUi")

        response.observe(viewLifecycleOwner, {

            binding.isLoading = false

            if (it.rides != null) {
                adapter.submitList(it.rides)
                binding.isEmpty = it.rides.isEmpty()
                Timber.tag(TAG).d("rides != null false isEmpty ${it.rides.isEmpty()}")
            }
            else {

                binding.isEmpty = true
                binding.hasError = true

                Timber.tag(TAG).d("rides != null true Error ${it.error}")

                Toast.makeText(requireContext(), "Error : ${it.error}"  , Toast.LENGTH_LONG).show()

            }
        })

    }


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