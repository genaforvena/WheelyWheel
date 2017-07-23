package org.imozerov.wheelywheel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val wheelValue = MutableLiveData<Int>()
    private val mode = MutableLiveData<Mode>()

    private val handler = Handler()

    private val incrementor = object : Runnable {
        override fun run() {
            val value = if (wheelValue.value != null && wheelValue.value!! < LIMIT) wheelValue.value as Int + 1 else 0
            wheelValue.postValue(value)
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
        }
    }
    private val decrementor = object : Runnable {
        override fun run() {
            val value = if (wheelValue.value != null && wheelValue.value!! > 0) wheelValue.value as Int - 1 else LIMIT
            wheelValue.postValue(value)
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
        }
    }

    init {
        wheelValue.value = Random().nextInt(LIMIT)
        mode.value = Mode.RANDOM
    }

    fun wheelValue() : LiveData<Int> = wheelValue
    fun mode() : LiveData<Mode> = mode

    fun setValue(value: Int) {
        if (mode.value != Mode.EDIT) {
            throw RuntimeException("Please set mode to edit first!")
        }
        wheelValue.value = value
    }

    fun setMode(newMode: Mode) {
        if (mode.value == newMode) {
            return
        }

        mode.value = newMode

        handler.removeCallbacksAndMessages(null)
        when (newMode) {
            Mode.EDIT -> {

            }
            Mode.RANDOM -> {
                wheelValue.value = Random().nextInt(LIMIT)
            }
            Mode.TIMER_INCREMENT -> {
                handler.post(incrementor)
            }
            Mode.TIMER_DECREMENT -> {
                handler.post(decrementor)
            }
        }
    }

    companion object {
        private val LIMIT = 99999
    }
}

enum class Mode {
    EDIT, RANDOM, TIMER_INCREMENT, TIMER_DECREMENT
}