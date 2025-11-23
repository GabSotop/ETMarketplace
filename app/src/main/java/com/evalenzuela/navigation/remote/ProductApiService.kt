package com.evalenzuela.navigation.remote

import com.evalenzuela.navigation.data.model.Item
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductApiService {

    @GET("/api/products")
    suspend fun getProducts(): List<Item>

    @POST("/api/products")
    suspend fun createProduct(@Body item: Item): Item
}