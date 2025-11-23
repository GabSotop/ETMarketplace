package com.evalenzuela.navigation.data.repository


import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.remote.RetrofitInstance



interface PostRepositoryInterface{
    suspend fun getPosts(): List<Post>
}

class PostRepository : PostRepositoryInterface {
    override suspend fun getPosts(): List<Post>{
        return RetrofitInstance.api.getPosts()
    }
}



