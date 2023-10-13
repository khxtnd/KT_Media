package com.mymusic.utils.extention

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun <T> ViewModel.liveData(action: suspend MediatorLiveData<T>.() -> Unit): LiveData<T> {

    val self = MediatorLiveData<T>()

    viewModelScope.launch(Dispatchers.IO) {

        action(self)
    }

    return self
}

fun <T> ViewModel.listenerSource(vararg listLiveData: LiveData<*>, action: suspend MediatorLiveData<T>.() -> Unit): LiveData<T> {

    var job: Job? = null


    val self = MediatorLiveData<T>()


    listLiveData.forEach { liveData ->

        self.addSource(liveData) { _ ->

            job?.cancel()

            job = viewModelScope.launch(Dispatchers.IO) {

                action(self)
            }
        }
    }

    return self
}