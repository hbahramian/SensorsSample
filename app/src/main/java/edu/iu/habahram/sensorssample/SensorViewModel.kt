package edu.iu.habahram.sensorssample

import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SensorViewModel
    : ViewModel() {
    private val TAG = "SensorViewModel"
    private lateinit var lightSensor: MeasurableSensor
    private lateinit var accelerometerSensor: MeasurableSensor

    private var accelerometerData = floatArrayOf(
        SensorManager.GRAVITY_EARTH, SensorManager.GRAVITY_EARTH, 0.0F
    )


    private var _isDark: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDark: LiveData<Boolean>
        get() = _isDark


    fun initializeSensors(sLight: MeasurableSensor, sAccelerometer: MeasurableSensor) {
        //initialize light sensor
        lightSensor = sLight
        lightSensor.startListening()
        lightSensor.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            _isDark.value = lux < 60f
        }

        //initialize accelerometer sensor
        accelerometerSensor = sAccelerometer
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { a ->
            val x: Float = a[0]
            val y: Float = a[1]
            val z: Float = a[2]
            accelerometerData[1] = accelerometerData[0]
            accelerometerData[0] = Math.sqrt((x * x).toDouble() + y * y + z * z).toFloat()
            val delta: Float = accelerometerData[0] - accelerometerData[1]
            accelerometerData[2] = accelerometerData[2] * 0.9f + delta


            if (accelerometerData[2] > 12) {
                Log.i(TAG, "Do not shake the phone!")
            }



        }

    }
}