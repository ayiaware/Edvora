package com.ayia.rider

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.ayia.rider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                RiderApp.instance.getRepository(), this
            )
        )[MainViewModel::class.java]

        binding.viewModel = mainViewModel

        binding.lifecycleOwner = this

    }
}