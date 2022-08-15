package com.example.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.ResultData

abstract class BaseViewModel : ViewModel() {

    val loadingErrorState = MutableLiveData<ResultData<Any?>>()

    fun handleTask(task: ResultData<Any?>, callback: () -> Unit = {}) {
        loadingErrorState.postValue(task)
        callback.invoke()
    }
}
