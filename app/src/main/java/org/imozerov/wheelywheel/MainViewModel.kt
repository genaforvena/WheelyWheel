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

    private val stopHandler = AtomicBoolean(true)
    private val incrementor = object : Runnable {
        override fun run() {
            if (stopHandler.get()) {
                return
            }

            val value = if (wheelValue.value != null) wheelValue.value as Int + 1 else 0
            wheelValue.postValue(value)

            if (!stopHandler.get()) {
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
            }
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

        when (newMode) {
            Mode.EDIT -> {
                stopHandler.set(true)
            }
            Mode.RANDOM -> {
                stopHandler.set(true)
                wheelValue.value = Random().nextInt(LIMIT)
            }
            Mode.TIMER -> {
                stopHandler.set(false)
                handler.post(incrementor)
            }
        }
    }

    companion object {
        private val LIMIT = 99999
    }
}

enum class Mode {
    EDIT, RANDOM, TIMER
}