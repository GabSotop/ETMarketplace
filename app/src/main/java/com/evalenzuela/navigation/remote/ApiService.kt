package com.evalenzuela.navigation.remote


import com.evalenzuela.navigation.data.model.Post

import retrofit2.http.GET

interface ApiService{
    @GET("/posts")
    suspend fun getPosts(): List<Post>
}



