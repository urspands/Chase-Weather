package com.raj.chase.viewModel

import androidx.lifecycle.ViewModel
import com.raj.chase.repository.DataRepository
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
}