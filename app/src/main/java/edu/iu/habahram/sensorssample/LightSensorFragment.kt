package edu.iu.habahram.sensorssample

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.iu.habahram.sensorssample.databinding.FragmentLightSensorBinding


/**
 * A simple [Fragment] subclass.
 * Use the [LightSensorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LightSensorFragment : Fragment(), SensorEventListener {
    private val TAG = "MainActivity"
    private var _binding: FragmentLightSensorBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var sLight: Sensor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLightSensorBinding.inflate(inflater, container, false)
        val view = binding.root
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for (x in deviceSensors) {
            Log.i(TAG, x.toString())
        }

        sLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            val gravSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GRAVITY)
            // Use the version 3 gravity sensor.
            sensor = gravSensors.firstOrNull { it.vendor.contains("Google LLC") && it.version == 3 }
        }
        if (sensor == null) {
            // Use the accelerometer.
            sensor = if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            } else {
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
                null
            }
        }

      return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            // The light sensor returns a single value.
            // Many sensors return 3 values, one for each axis.
            val lux = event.values[0]
            if(lux < 200f) {
                binding.tvLight.text = "It is dark outside!"
                binding.tvLight.setTextColor(Color.WHITE)
                binding.root.setBackgroundColor(Color.DKGRAY)
            } else {
                binding.tvLight.text = "It is bright outside!"
                binding.tvLight.setTextColor(Color.BLACK)
                binding.root.setBackgroundColor(Color.WHITE)

            }
            Log.i(TAG, "light changed: $lux")
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onResume() {
        super.onResume()
        sLight?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }



}