package com.example.chartManager

import Repository
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class ChartViewModel(private val repository: Repository) : ViewModel() {
    fun getLatestId() = viewModelScope.launch(Dispatchers.IO) {
        if (repository.getLatestId()?.question_id?.rem(floor(repository.getLatestId()?.question_id!!)) == 0f) {
            latestId.value = repository.getLatestId()?.question_id?.plus(0.1f) ?: 1f
        } else {
            latestId.value = repository.getLatestId()?.question_id?.plus(1) ?: 1f
        }
    }

    fun getDataFromQuestionId(id: Float) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getDataFromQuestionId(id)
        if (data.isNullOrEmpty()) {
            record.value = Record(-1, 0f, 0, 0, 0,0, 0)
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

    fun getReview(minDate: Long, maxDate: Long) = viewModelScope.launch(Dispatchers.IO) {
        var currentDate = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val requiredMinDate = currentDate.plusDays(minDate).format(formatter).toLong()
        val requiredMaxDate = currentDate.plusDays(maxDate).format(formatter).toLong()

        val data = repository.getReview(requiredMinDate, requiredMaxDate)
        if (!data.isNullOrEmpty()) {
            reviewList.addAll(data)
        }
    }

    var latestId = mutableStateOf(1f)
    var record = mutableStateOf(Record(0, 0f, 0, 0, 0, 0,0))
    var history = mutableStateListOf<Record>()
    var reviewList = mutableStateListOf<Record>()
    var timeCount = mutableStateOf("00:00")
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