package com.ayia.rider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.ayia.rider.databinding.FragmentRidesBinding
import com.ayia.rider.ui.main.ViewPagerFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val TAB_TITLES = arrayOf(
    R.string.label_nearest,
    R.string.label_upcoming,
    R.string.label_past,
)


class RidesFragment : Fragment() {


    private var _binding: FragmentRidesBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRidesBinding.inflate(inflater, container, false)

        val root = binding.root

        val tabs: TabLayout = binding.tabs

        val sectionsPagerAdapter = ViewPagerFragmentAdapter(requireActivity())


        val viewPager: ViewPager2 = binding.viewPager

        viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()


        return root
    }



}