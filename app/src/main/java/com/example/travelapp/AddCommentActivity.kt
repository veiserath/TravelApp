package com.example.travelapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.data.PhotoModel
import com.example.travelapp.databinding.ActivityAddCommentBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_add_comment.*
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.thread


class AddCommentActivity : AppCompatActivity() {

    private lateinit var globalPhotoUri: String
    private val binding by lazy { ActivityAddCommentBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        extractIntentData()

        addCommentButton.setOnClickListener {
            val comment = addCommentEditText.text
            insertItemToTheDatabase(globalPhotoUri, comment.toString())
            val intent = Intent(applicationContext, PhotosActivity::class.java)
            intent.putExtra("comment", comment)
            startActivity(intent)
        }
    }

    private fun extractIntentData() {
        if (intent.getSerializableExtra("photoUri") != null) {
            var photoUri = intent.getSerializableExtra("photoUri") as String
            photoUri = photoUri.split("///")[1]

            setImage(photoUri)

            val newPhotoUri = photoUri.split(".jpg")
            val editedPhotoUri = newPhotoUri[0] + "edited" + ".jpeg"
            globalPhotoUri = editedPhotoUri

            imageViewDetails.filename = editedPhotoUri
            imageViewDetails.saveImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertItemToTheDatabase(photoUri: String, comment: String) {
        val geoCoder = Geocoder(baseContext, Locale.getDefault())
        var addressDescription = ""
        addressDescription = if (Shared.location != null) {
            val address = geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)
            "" + address[0].locality + ", " + address[0].countryName
        } else {
            "Not available"
        }
        val user = PhotoModel(
            0,
            photoUri,
            LocalDate.now().toString(),
            addressDescription,
            comment
        )
        thread{Shared.database?.photoDao?.addUser(user)
            Shared.location?.let { addGeofence(it,globalPhotoUri) }
        }
        Toast.makeText(this, "Added photo!", Toast.LENGTH_LONG).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun addGeofence(location: Location, photoUri: String) {
        val pi = PendingIntent.getBroadcast(
            applicationContext,
            1,
            Intent(this, CustomBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        LocationServices.getGeofencingClient(this)
            .addGeofences(
                generateRequest(location,photoUri), pi
            ).addOnSuccessListener {
                Log.i("Geofence", "Geofence added")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateRequest(loci: Location, photoUri: String): GeofencingRequest {
        val geofence = Geofence.Builder().setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setRequestId(photoUri)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setCircularRegion(loci.latitude, loci.longitude, Shared.radius).build()
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }
    private fun setImage(photoUri: String) {
        val bitmap = BitmapFactory.decodeFile(photoUri)
        val ei = ExifInterface(photoUri)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
        binding.imageViewDetails.bitmap = rotatedBitmap!!
        binding.imageViewDetails.text = getLocationFromGPSCoordinates()
        binding.imageViewDetails.invalidate()

    }

    private fun getLocationFromGPSCoordinates(): String {
        if (Shared.location != null) {
            val geoCoder = Geocoder(baseContext, Locale.getDefault())
            val address =
                geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)
            return "" + address[0].locality + ", " + address[0].countryName
        }
        return "Not available."
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}