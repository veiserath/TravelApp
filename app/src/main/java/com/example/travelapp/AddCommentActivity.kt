package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.data.PhotoModel
import com.example.travelapp.data.PhotoViewModel
import com.example.travelapp.databinding.ActivityAddCommentBinding
import kotlinx.android.synthetic.main.activity_add_comment.*
import java.time.LocalDate
import java.util.*


class AddCommentActivity : AppCompatActivity() {

    private lateinit var mPhotoViewModel: PhotoViewModel
    private lateinit var globalPhotoUri: String
    private val binding by lazy { ActivityAddCommentBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        extractIntentData()
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

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
        val address =
            geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)

        val user = PhotoModel(
            0,
            photoUri,
            LocalDate.now().toString(),
            "" + address[0].locality + ", " + address[0].countryName,
            comment
        )
        mPhotoViewModel.addUser(user)
        Toast.makeText(this, "Added photo!", Toast.LENGTH_LONG).show()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
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
        val geoCoder = Geocoder(baseContext, Locale.getDefault())
        val address =
            geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)
        return "" + address[0].locality + ", " + address[0].countryName
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