package com.raj.chase.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.raj.chase.databinding.ActivityMainBinding
import com.raj.chase.viewModel.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private val _viewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}