package com.example.travelapp.data

import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao) {


    val readAllData: LiveData<List<PhotoModel>> = photoDao.readAllData()


    suspend fun addUser(photoModel: PhotoModel){
        photoDao.addUser(photoModel)
    }
}