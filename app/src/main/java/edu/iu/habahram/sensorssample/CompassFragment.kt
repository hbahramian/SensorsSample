package edu.iu.habahram.sensorssample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.iu.habahram.sensorssample.databinding.FragmentCompassBinding
import edu.iu.habahram.sensorssample.model.Compass


/**
 * A simple [Fragment] subclass.
 * Use the [CompassFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompassFragment : Fragment() {

    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    this.requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                getLocation()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewModel : SensorViewModel by activityViewModels()
        viewModel.initializeSensors(this.requireContext())
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.rotate.observe(viewLifecycleOwner, Observer {
            it?.let {
                rotateAnimation(it)
            }
        })
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext());
        requestPermissions()
        return  view
    }

    fun rotateAnimation(compass: Compass) {
        val rotateAnimation = RotateAnimation(
            -compass.oldHeading,
            -compass.heading,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        binding.imageCompass.startAnimation(rotateAnimation)
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this.requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener {

                // Got last known location. In some rare situations this can be null.
                if (it != null) {
                    var isLocationRetrieved = true
                    var latitude = it.latitude.toFloat()
                    var longitude = it.longitude.toFloat()
                    var altitude = it.altitude.toFloat()
                    var magneticDeclination =
                        CompassHelper.calculateMagneticDeclination(latitude.toDouble(),
                            longitude.toDouble(), altitude.toDouble()
                        )
                    //binding.textViewMagneticDeclination.text = "Magnetic Declination:$magneticDeclination"
                }

        }

    }

    companion object {
        val TAG = "CompassFragment"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
    }

}