package com.example.travelapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.data.PhotoViewModel
import com.example.travelapp.databinding.ActivityPhotosBinding

class PhotosActivity : AppCompatActivity() {
    private lateinit var mPhotoViewModel: PhotoViewModel
    private val binding by lazy { ActivityPhotosBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = setupRecyclerView()
        setupDatabase(adapter)
    }

    private fun setupDatabase(adapter: PhotoAdapter) {
        mPhotoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        mPhotoViewModel.readAllData.observe(this, { user -> adapter.setData(user) })
    }

    private fun setupRecyclerView(): PhotoAdapter {
        val adapter = PhotoAdapter()
        binding.photoCollectionRecycler.adapter = adapter
        binding.photoCollectionRecycler.layoutManager = LinearLayoutManager(this)
        return adapter
    }
}