package com.evalenzuela.navigation.data.repository

import com.evalenzuela.navigation.data.model.Item
import com.evalenzuela.navigation.remote.RetrofitInstance
import java.util.concurrent.atomic.AtomicInteger

class SampleRepository {

    private val sampleItems: MutableList<Item> = mutableListOf(
        Item(1, "Laptop Gamer", "Alto rendimiento...", "$1.299.990", "https://storage-asset.msi.com/global/picture/news/2024/nb/nb-20240103-1.jpg"),
        Item(2, "Monitor Curvo 27", "Ideal para diseño...", "$349.500", "https://www.acerstore.cl/cdn/shop/files/1_XZ342CU.png?v=1757536474"),
        Item(3, "Teclado Mecánico", "Switches rojos...", "$89.990", "https://dojiw2m9tvv09.cloudfront.net/74837/product/captura-de-pantalla-2023-01-17-a-la-s-00-50-494358.png"),
        Item(4, "Mouse Inalámbrico", "Ergonómico, 16000 DPI.", "$35.750", "https://cintegral.cl/wp-content/uploads/2022/05/1575-0cf-MOUSE-Logitech-G502-inalambrico-lightspeed-600x1042.jpg"),
        Item(5, "Disco SSD 1TB", "Velocidad ultrarrápida.", "$79.990", "https://ae-pic-a1.aliexpress-media.com/kf/Sa984fe25adf144f39be7a4c0045a0d4aK.jpg_640x640q75.jpg_.avif")
    )

    private val nextId = AtomicInteger(sampleItems.size + 1)

    suspend fun getAll(): List<Item> {
        return try {

            val remoteItems = RetrofitInstance.productApi.getProducts()


            if (remoteItems.isNotEmpty()) {
                sampleItems.clear()
                sampleItems.addAll(remoteItems)
            }
            sampleItems
        } catch (e: Exception) {

            println("Error conectando al backend: ${e.localizedMessage}")
            sampleItems
        }
    }

    fun getById(id: Int): Item? = sampleItems.find { it.id == id }


    suspend fun addItem(item: Item) {
        try {

            val createdItem = RetrofitInstance.productApi.createProduct(item)


            sampleItems.add(createdItem)
        } catch (e: Exception) {
            println("Error creando producto en backend: ${e.localizedMessage}")

            val fallbackItem = item.copy(id = nextId.getAndIncrement())
            sampleItems.add(fallbackItem)
        }
    }
}