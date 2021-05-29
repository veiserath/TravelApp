package com.example.travelapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
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

    lateinit var newPhotoUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)
        if (intent.getSerializableExtra("photoUri") != null) {
            val photoUri =
                intent.getSerializableExtra("photoUri") as String
            newPhotoUri = photoUri.split("///")[1]
            imageViewDetails.setImageBitmap(BitmapFactory.decodeFile(newPhotoUri))
            imageViewDetails.rotation = 270F
//            Log.println(Log.DEBUG,"dupa","WYMIARY: height: " + BitmapFactory.decodeFile(newPhotoUri).height + " width: " + BitmapFactory.decodeFile(newPhotoUri).width)
        }

        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)

        addCommentButton.setOnClickListener{
            val comment = addCommentEditText.text
            insertItemToTheDatabase(comment.toString())
            var intent = Intent(applicationContext,PhotosActivity::class.java)
            intent.putExtra("comment", comment)
            startActivity(intent)
        }


    }
    fun insertItemToTheDatabase(comment: String) {

        val geoCoder = Geocoder(baseContext, Locale.getDefault())
        val address = geoCoder.getFromLocation(Shared.location!!.latitude, Shared.location!!.longitude, 1)

//        "" + (Shared.location?.latitude ?: "dupa") + " " + (Shared.location?.longitude
//            ?: "dupcia")
        val user = PhotoModel(0,newPhotoUri, LocalDate.now().toString(), "" + address[0].locality + ", " + address[0].countryName,comment)
        mPhotoViewModel.addUser(user)
        Toast.makeText(this,"Added photo!",Toast.LENGTH_LONG).show()

        var intent = Intent(applicationContext,MainActivity::class.java)
//            intent.putExtra("index", 0)
        startActivity(intent)
    }
}