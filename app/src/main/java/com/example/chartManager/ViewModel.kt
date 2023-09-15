package com.example.chartManager

import Repository
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(private val repository: Repository) : ViewModel() {
    fun getLatestId() = viewModelScope.launch(Dispatchers.IO) {
        latestId.value = repository.getLatestId()?.question_id ?: 1f
    }

    fun getDataFromQuestionId(id: Float) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getDataFromQuestionId(id)
        if (data.isNullOrEmpty()) {
            record.value = Record(-1, 0f, 0, 0)
        } else {
            record.value = data[0]
        }
    }

    fun getHistory(numRecords: Int) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getHistory(numRecords)
        if (!data.isNullOrEmpty()) {
            history.addAll(data)
        }
    }

    var latestId = mutableStateOf(1f)
    var record = mutableStateOf(Record(0, 0f, 0, 0))
    var history = mutableStateListOf<Record>()
}

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}