package com.neki.android.feature.sample.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.SampleRepository
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
