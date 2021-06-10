package com.example.mamamiadelivery.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Event<T>(private var data: T? = null) {

    var isHandled = false
        private set

    fun getIfNotHandled(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            data
        }
    }

    fun peekData() = data
}

typealias EventMutableLiveData <T> = MutableLiveData<Event<T>>
typealias EventLiveData <T> = LiveData<Event<T>>

fun <T> EventMutableLiveData<T>.call(data: T) {
    value = Event(data)
}

fun <T> EventLiveData<T>.observeEvent(owner: LifecycleOwner, onEvent: (T) -> Unit) {
    observe(owner) { event ->
        event.getIfNotHandled()?.let { data ->
            onEvent(data)
        }
    }
}