package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.media.ExifInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.data.PhotoModel
import com.example.travelapp.data.PhotoViewModel
import kotlinx.android.synthetic.main.activity_photo_details.*
import java.time.LocalDate
import java.util.*


class AddCommentActivity : AppCompatActivity() {

    private lateinit var mPhotoViewModel : PhotoViewModel
    private lateinit var globalPhotoUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)
        if (intent.getSerializableExtra("photoUri") != null) {
            var photoUri = intent.getSerializableExtra("photoUri") as String
            photoUri = photoUri.split("///")[1]
            setImage(photoUri)
            globalPhotoUri = photoUri
        }

        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        addCommentButton.setOnClickListener{
            val comment = addCommentEditText.text
            insertItemToTheDatabase(globalPhotoUri,comment.toString())
            var intent = Intent(applicationContext,PhotosActivity::class.java)
            intent.putExtra("comment", comment)
            startActivity(intent)
        }


    }
    fun insertItemToTheDatabase(photoUri: String, comment: String) {

        val geoCoder = Geocoder(baseContext, Locale.getDefault())
        val address = geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)

//        "" + (Shared.location?.latitude ?: "dupa") + " " + (Shared.location?.longitude
//            ?: "dupcia")
        val user = PhotoModel(0,photoUri, LocalDate.now().toString(), "" + address[0].locality + ", " + address[0].countryName,comment)
        mPhotoViewModel.addUser(user)
        Toast.makeText(this,"Added photo!",Toast.LENGTH_LONG).show()

        var intent = Intent(applicationContext,MainActivity::class.java)
//            intent.putExtra("index", 0)
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
        imageViewDetails.setImageBitmap(rotatedBitmap)
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