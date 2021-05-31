package com.example.travelapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.data.PhotoViewModel
import com.example.travelapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    lateinit var savedUri: Uri
    private val locClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private lateinit var channel: NotificationChannel

    private lateinit var mPhotoViewModel: PhotoViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        // Request camera and tracking permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        setupOnClickListeners()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        setupNotificationChannel()
        startTrackingLocation()
    }

    private fun setupOnClickListeners() {
        // Set up the listener for take photo button
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }
        binding.settingsButton.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.flipCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        binding.allPhotosButton.setOnClickListener {
            val intent = Intent(applicationContext, PhotosActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannel(){
        val importance = NotificationManager.IMPORTANCE_HIGH
        channel = NotificationChannel("myChannel","location notifications",importance)
        channel.description = "Notifications about photos taken in this location."
    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Location (background) or camera permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun addGeofence(loci: Location) {
        val pi = PendingIntent.getBroadcast(
            applicationContext,
            1,
            Intent(this, CustomBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        LocationServices.getGeofencingClient(this)
            .addGeofences(
                generateRequest(loci), pi
            ).addOnSuccessListener {
//                Log.i("Geofence", "Geofence added")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateRequest(loci: Location): GeofencingRequest {
        val uri = savedUri.toString()
        val splitUri = uri.split("///")[1]
        val newPhotoUri = splitUri.split(".jpg")
        val editedPhotoUri = newPhotoUri[0] + "edited" + ".jpeg"

        val geofence = Geofence.Builder().setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setRequestId(editedPhotoUri)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setCircularRegion(loci.latitude, loci.longitude, Shared.radius).build()
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, msg)
//                    Log.d(
//                        TAG,
//                        "" + (Shared.location?.latitude
//                            ?: "latitude") + " " + (Shared.location?.longitude ?: "longitude")
//                    )
                    Shared.location?.let {
                        addGeofence(it)
                    }
                    val intent = Intent(applicationContext, AddCommentActivity::class.java)
                    intent.putExtra("photoUri", savedUri.toString())
                    startActivity(intent)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                this,
                "Location (background) or camera permissions not granted by the user.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        val request = LocationRequest.create().apply {
            fastestInterval = 500L
            interval = 1000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Shared.location = p0.lastLocation
            }
        }, Looper.getMainLooper())
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
    }
}