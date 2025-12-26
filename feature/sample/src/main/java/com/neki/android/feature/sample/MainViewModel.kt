package com.neki.android.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataapi.repository.SampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sampleRepository: SampleRepository
): ViewModel() {

    init {
        getPost(id = 1)
    }

    fun getPosts() {
        viewModelScope.launch {
            sampleRepository.getPosts()
        }
    }

    fun getPost(
        id: Int
    ) {
        viewModelScope.launch {
            sampleRepository.getPost(id = id)
        }
    }
}
