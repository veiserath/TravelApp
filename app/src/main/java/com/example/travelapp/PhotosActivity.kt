package com.example.travelapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.data.PhotoViewModel

class PhotosActivity : AppCompatActivity() {
    lateinit var newPhotoUri:String
    private lateinit var mPhotoViewModel: PhotoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        if (intent.getSerializableExtra("photoUri") != null) {
            val photoUri =
                intent.getSerializableExtra("photoUri") as String
            newPhotoUri = photoUri.split("///")[1]
//            image_view.setImageBitmap(BitmapFactory.decodeFile(newPhotoUri))
        }
        val photo_collection_recycler = findViewById<View>(R.id.photo_collection_recycler) as RecyclerView
        val adapter = PhotoAdapter()
        photo_collection_recycler.adapter = adapter
        photo_collection_recycler.layoutManager = LinearLayoutManager(this)
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        mPhotoViewModel.readAllData.observe(this, Observer { user -> adapter.setData(user) })

    }
}