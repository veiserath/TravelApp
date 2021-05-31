package com.example.travelapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.databinding.ActivityPhotosBinding

class PhotosActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotosBinding.inflate(layoutInflater) }
    private lateinit var adapter: PhotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        adapter = setupRecyclerView()
        adapter.refresh(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh(applicationContext)
    }

    private fun setupRecyclerView(): PhotoAdapter {
        val adapter = PhotoAdapter()
        binding.photoCollectionRecycler.adapter = adapter
        binding.photoCollectionRecycler.layoutManager = LinearLayoutManager(this)
        return adapter
    }

}