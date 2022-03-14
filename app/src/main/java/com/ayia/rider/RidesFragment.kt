package com.ayia.rider

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.widget.PopupWindowCompat
import androidx.lifecycle.Observer
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

    var tabPosition = 0


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

        val viewPager: ViewPager2 = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter




        TabLayoutMediator(tabs, viewPager) { tab: TabLayout.Tab, position: Int ->

            tabPosition = position

           // tab.text = getString(TAB_TITLES[position], 0)

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


    private fun countRidesInAllSections(){

        mainViewModel.nearestRides.observe(viewLifecycleOwner, {

        })

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

        val anchor =  requireActivity().findViewById<View>(R.id.tvFiltersLabel) // set the menuOption as anchor so that the popup will display TOP RIGHT of the screen

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(view, width, height, focusable)
        popupWindow.elevation = 10f

        PopupWindowCompat.showAsDropDown(popupWindow, anchor, 0, 0, Gravity.CENTER)
        PopupWindowCompat.setWindowLayoutType( popupWindow , WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val filterCity = view.findViewById<View>(R.id.cardFilterCity)
        val filterState = view.findViewById<View>(R.id.cardFilterState)

        filterCity.setOnClickListener {

            Timber.tag(TAG).d("filterCityOnClick")

            val listPopupWindow = showFilterList(FILTER_TYPE_CITY)

            listPopupWindow.elevation = 15f
            listPopupWindow.isFocusable = true

            PopupWindowCompat.showAsDropDown(listPopupWindow, filterCity, 0, 0, Gravity.CENTER)
            PopupWindowCompat.setWindowLayoutType(listPopupWindow , WindowManager.LayoutParams.FLAG_FULLSCREEN)


        }

        filterState.setOnClickListener {
            Timber.tag(TAG).d("filterStateOnClick")

            val listPopupWindow = showFilterList(FILTER_TYPE_STATE)

            listPopupWindow.elevation = 15f
            listPopupWindow.isFocusable = true

            PopupWindowCompat.showAsDropDown(listPopupWindow, filterState, 0, 0, Gravity.CENTER)
            PopupWindowCompat.setWindowLayoutType(listPopupWindow , WindowManager.LayoutParams.FLAG_FULLSCREEN)

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
                    if  (mainViewModel.filter.state == "") {
                        options.add(Option(FILTER_TYPE_CITY, l.city!!))
                    }
                    else{
                        if (l.state == mainViewModel.filter.state){
                            options.add(Option(FILTER_TYPE_CITY, l.city!!))
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

            Timber.tag(TAG).d("onClickFilter Type: ${option.type} Value: ${option.value}")

            Toast.makeText(requireContext(), "${ if (option.type == FILTER_TYPE_CITY) getString(R.string.label_city) else getString(R.string.label_state)} ${option.value}"  , Toast.LENGTH_SHORT).show()

            mainViewModel.setFilter(tabPosition, option)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}