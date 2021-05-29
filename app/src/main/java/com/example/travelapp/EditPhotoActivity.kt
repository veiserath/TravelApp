package com.example.travelapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_photo.*

class EditPhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)
        if (intent.getSerializableExtra("photoUri") != null) {
            val photoUri =
                intent.getSerializableExtra("photoUri") as String
            imageView.setImageBitmap(BitmapFactory.decodeFile(photoUri))
            val comment = intent.getSerializableExtra("comment") as String
            textView.text = comment
        }
    }
}