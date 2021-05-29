package com.example.travelapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application):AndroidViewModel(application){


    val readAllData: LiveData<List<PhotoModel>>
    private val repository: PhotoRepository

    init {
        val userDao = PhotoDatabase.getDatabase(application).userDao()
        repository = PhotoRepository(userDao)
        readAllData = repository.readAllData
    }


    fun addUser(photoModel: PhotoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(photoModel)
        }
    }
}