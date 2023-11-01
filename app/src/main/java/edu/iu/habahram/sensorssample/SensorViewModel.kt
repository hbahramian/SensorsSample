package edu.iu.habahram.sensorssample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SensorViewModel
: ViewModel() {
    private lateinit var lightSensor: MeasurableSensor

    private var _isDark: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDark: LiveData<Boolean>
        get() = _isDark


    fun initializeSensors(lSensor: MeasurableSensor) {
        lightSensor = lSensor
        lightSensor.startListening()
        lightSensor.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            _isDark.value = lux < 60f
        }
    }
}