package com.example.frontend

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MyViewModel :  ViewModel()  {
    private val service = PostsService.create()


    private val _posts = MutableLiveData<List<ItemResponse>>()
    val posts: LiveData<List<ItemResponse>>
        get() = _posts



    fun fetchBooks() {
        viewModelScope.launch {
            println("get posts")
            _posts.value = service.getPosts()
        }
    }

    fun add(item: ItemRequest) {
        viewModelScope.launch {
            println("add posts")
            _posts.value = service.createPost(item)
        }
    }
    init {
        Log.d(ContentValues.TAG,"init")
        fetchBooks()
    }
}