package com.ayia.rider

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.PopupWindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ayia.rider.adapters.FiltersAdapter
import com.ayia.rider.databinding.FragmentRidesBinding
import com.ayia.rider.model.Option
import com.ayia.rider.ui.main.ViewPagerFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

private val TAB_TITLES = arrayOf(
    R.string.label_nearest,
    R.string.label_upcoming,
    R.string.label_past,
)


class RidesFragment : Fragment() {
    private val TAG: String =
        GLOBAL_TAG + " " + RidesFragment::class.java.simpleName

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentRidesBinding? = null

    private val binding get() = _binding!!


    lateinit var  viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainViewModel = ViewModelProvider(
            requireActivity()
        )[MainViewModel::class.java]

        _binding = FragmentRidesBinding.inflate(inflater, container, false)

        val root = binding.root

        val tabs: TabLayout = binding.tabs

        val sectionsPagerAdapter = ViewPagerFragmentAdapter(requireActivity())

        viewPager  = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter


        TabLayoutMediator(tabs, viewPager) { tab: TabLayout.Tab, position: Int ->


            // tab.text = getString(TAB_TITLES[position], 0)


            Timber.tag(TAG).d("Tab Position $position $tab")

            when(position){
                0-> {
                    tab.text = getString(TAB_TITLES[position])

                }
                1-> {
                    mainViewModel.futureRidesCount.observe(viewLifecycleOwner, {
                        tab.text = getString(TAB_TITLES[position], it)
                    })
                }
                2-> {
                    mainViewModel.pastRidesCount.observe(viewLifecycleOwner, {
                        tab.text = getString(TAB_TITLES[position], it)
                    })
                }
            }

        }.attach()



        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvFiltersLabel.setOnClickListener {
            showFilterPopUp()
        }

    }

    private fun showFilterPopUp(){

        Timber.tag(TAG).d("showFilterPopup")

        val inflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_filter, null)

        val anchor =  requireActivity().findViewById<View>(R.id.tvFiltersLabel)


        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(view, width, height, focusable)
        popupWindow.elevation = 10f

        PopupWindowCompat.showAsDropDown(popupWindow, anchor, 0, 0, Gravity.CENTER)
        PopupWindowCompat.setWindowLayoutType( popupWindow , WindowManager.LayoutParams.WRAP_CONTENT)


        val cvCity = view.findViewById<View>(R.id.cardFilterCity)
        val cvState = view.findViewById<View>(R.id.cardFilterState)

        val tvCity : TextView = view.findViewById(R.id.tvCityFilter)
        val tvState: TextView = view.findViewById(R.id.tvStateFilter)


        when(viewPager.currentItem){
            0->{

                Timber.tag(TAG).d("State ${mainViewModel.filterNearestRides.state}")

                tvCity.text = if (mainViewModel.filterNearestRides.city == "")
                    getString(R.string.label_city)
                else mainViewModel.filterNearestRides.city

                tvState.text = if (mainViewModel.filterNearestRides.state == "")
                    getString(R.string.label_state)
                else mainViewModel.filterNearestRides.state

            }
            1->{
                tvCity.text = if (mainViewModel.filterFutureRides.city == "")
                    getString(R.string.label_city)
                else mainViewModel.filterFutureRides.city

                tvState.text = if (mainViewModel.filterFutureRides.state == "")
                    getString(R.string.label_state)
                else mainViewModel.filterFutureRides.state
            }
            2->{
                tvCity.text = if (mainViewModel.filterPastRides.city == "")
                    getString(R.string.label_city)
                else mainViewModel.filterPastRides.city

                tvState.text = if (mainViewModel.filterPastRides.state == "")
                    getString(R.string.label_state)
                else mainViewModel.filterPastRides.state
            }
        }

        cvCity.setOnClickListener {

            Timber.tag(TAG).d("filterCityOnClick")

            val listPopupWindow = showFilterList(FILTER_TYPE_CITY)

            listPopupWindow.elevation = 15f
            listPopupWindow.isFocusable = true

            PopupWindowCompat.showAsDropDown(listPopupWindow, cvCity, 0, 0, Gravity.CENTER)
            PopupWindowCompat.setWindowLayoutType(listPopupWindow , WindowManager.LayoutParams.WRAP_CONTENT)

        }

        cvState.setOnClickListener {
            Timber.tag(TAG).d("filterStateOnClick")

            val listPopupWindow = showFilterList(FILTER_TYPE_STATE)

            listPopupWindow.elevation = 15f
            listPopupWindow.isFocusable = true

            PopupWindowCompat.showAsDropDown(listPopupWindow, cvState, 0, 0, Gravity.CENTER)
            PopupWindowCompat.setWindowLayoutType(listPopupWindow , WindowManager.LayoutParams.WRAP_CONTENT)

        }


    }

    private fun showFilterList(filterType: String): PopupWindow {

        Timber.tag(TAG).d("showFilterList filterType $filterType")

        val inflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_filter_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFilters)
        val adapter = FiltersAdapter(FiltersAdapter.FilterDiff(), clickCallback )

        if (filterType == FILTER_TYPE_STATE)
            mainViewModel.states.observe(viewLifecycleOwner, { states ->

                val options = mutableListOf<Option>()

                for (l in states){
                    options.add(Option(FILTER_TYPE_STATE, l.state!!))
                }

                adapter.submitList(options.distinctBy { it.value }.sortedBy { it.value })
            })
        else
            mainViewModel.cities.observe(viewLifecycleOwner, { cities->

                val options = mutableListOf<Option>()

                for (l in cities){

                    when(viewPager.currentItem){
                        0 ->{
                            if  (mainViewModel.filterNearestRides.state == "") {
                                options.add(Option(FILTER_TYPE_CITY, l.city!!))
                            }
                            else{
                                if (l.state == mainViewModel.filterNearestRides.state){
                                    options.add(Option(FILTER_TYPE_CITY, l.city!!))
                                }
                            }
                        }
                        1 ->{
                            if  (mainViewModel.filterFutureRides.state == "") {
                                options.add(Option(FILTER_TYPE_CITY, l.city!!))
                            }
                            else{
                                if (l.state == mainViewModel.filterFutureRides.state){
                                    options.add(Option(FILTER_TYPE_CITY, l.city!!))
                                }
                            }
                        }
                        2 ->{
                            if  (mainViewModel.filterPastRides.state == "") {
                                options.add(Option(FILTER_TYPE_CITY, l.city!!))
                            }
                            else{
                                if (l.state == mainViewModel.filterPastRides.state){
                                    options.add(Option(FILTER_TYPE_CITY, l.city!!))
                                }
                            }
                        }
                    }

                }

                adapter.submitList(options.distinctBy { it.value }.sortedBy { it.value })

            })

        recyclerView.adapter = adapter

        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }



    private val clickCallback : FilterClickCallback = object : FilterClickCallback{

        override fun onClick(option: Option) {

            Timber.tag(TAG).d("onClickFilter Type: ${option.type} Value: ${option.value} Position ${viewPager.currentItem}")

            Toast.makeText(requireContext(), "${ if (option.type == FILTER_TYPE_CITY) getString(R.string.label_city) else getString(R.string.label_state)} ${option.value}"  , Toast.LENGTH_SHORT).show()

            mainViewModel.setFilter(viewPager.currentItem, option)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}