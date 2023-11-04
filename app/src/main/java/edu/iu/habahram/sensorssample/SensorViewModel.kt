package edu.iu.habahram.sensorssample

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.iu.habahram.sensorssample.CompassHelper.calculateHeading
import edu.iu.habahram.sensorssample.CompassHelper.convertRadtoDeg
import edu.iu.habahram.sensorssample.CompassHelper.map180to360
import edu.iu.habahram.sensorssample.model.Compass


class SensorViewModel
    : ViewModel() {
    private val TAG = "SensorViewModel"
    private  var lightSensor: MeasurableSensor? = null
    private  var accelerometerSensor: MeasurableSensor? = null
    private  var magneticfieldSensor: MeasurableSensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val isLocationRetrieved = false

    private val compass = Compass()

    private var accelerometerData = floatArrayOf(
        SensorManager.GRAVITY_EARTH, SensorManager.GRAVITY_EARTH, 0.0F
    )

    private var _rotate = MutableLiveData<Compass>()
    val rotate: LiveData<Compass>
        get() = _rotate


    private var _isDark: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDark: LiveData<Boolean>
        get() = _isDark


    fun initializeSensors(context: Context) {
        //initialize light sensor
        if (lightSensor == null) {
            initializeLightSensor(context)
        }

        //initialize accelerometer sensor
        if (accelerometerSensor == null) {
            initializeAccelerometerSensor(context)
        }

        if (magneticfieldSensor == null) {
            initializeMagneticfiledSensor(context)
        }

    }

    private fun initializeAccelerometerSensor(context: Context) {
        accelerometerSensor = AccelerometerSensor(context)
        accelerometerSensor?.startListening()
        accelerometerSensor?.setOnSensorValuesChangedListener { values ->

            CompassHelper.lowPassFilter(values, accelerometerReading);
        }
    }

    private fun initializeLightSensor(context: Context) {
        lightSensor = LightSensor(context)
        lightSensor?.startListening()
        lightSensor?.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            _isDark.value = lux < 60f
        }
    }

    private fun initializeMagneticfiledSensor(context: Context) {
        magneticfieldSensor = MagneticFiledSensor(context)
        magneticfieldSensor?.startListening()
        magneticfieldSensor?.setOnSensorValuesChangedListener { values ->
            Log.i(TAG, values.toString())
            CompassHelper.lowPassFilter(values, magnetometerReading);
            updateHeading()
        }
    }

    private fun updateHeading() {
        //oldHeading required for image rotate animation
        //oldHeading required for image rotate animation
        compass.oldHeading = compass.heading

        compass.heading = calculateHeading(accelerometerReading, magnetometerReading)
        compass.heading = convertRadtoDeg(compass.heading)
        compass.heading = map180to360(compass.heading)

        if (isLocationRetrieved) {
            compass.trueHeading = compass.heading + compass.magneticDeclination
            if (compass.trueHeading > 360) { //if trueHeading was 362 degrees for example, it should be adjusted to be 2 degrees instead
                compass.trueHeading = compass.trueHeading - 360
            }
        }
        _rotate.value = compass
    }


}