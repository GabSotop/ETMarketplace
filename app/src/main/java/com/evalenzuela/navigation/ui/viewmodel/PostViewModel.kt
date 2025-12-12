package com.evalenzuela.navigation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.data.repository.PostRepository
import com.evalenzuela.navigation.data.repository.PostRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepositoryInterface = PostRepository()
) : ViewModel() {

    private val _rawApiPosts = MutableStateFlow<List<Post>>(emptyList())
    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList


    private val _userLocalReviews = MutableStateFlow<Map<Int, List<Post>>>(emptyMap())
    val userLocalReviews: StateFlow<Map<Int, List<Post>>> = _userLocalReviews.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                val response = repository.getPosts()
                _rawApiPosts.value = response
                _postList.value = response
            } catch (e: Exception) {
                println("Error al obtener reseñas: ${e.localizedMessage}")
            }
        }
    }

    fun addUserReview(productId: Int, userName: String, content: String) {
        val currentReviews = _userLocalReviews.value.toMutableMap()
        val productReviews = currentReviews[productId]?.toMutableList() ?: mutableListOf()

        val newReview = Post(
            userId = 999,
            id = (System.currentTimeMillis() % 10000).toInt(),
            title = userName,
            body = content
        )

        productReviews.add(0, newReview)
        currentReviews[productId] = productReviews

        _userLocalReviews.value = currentReviews
    }

    fun getReviewsForProduct(productId: Int): List<Post> {
        val rawPosts = _rawApiPosts.value
        if (rawPosts.isEmpty()) return emptyList()


        val systemReviews = if (productId > 5) {
            emptyList()
        } else {
            getSystemReviews(productId, rawPosts)
        }


        val localReviews = _userLocalReviews.value[productId] ?: emptyList()


        return localReviews + systemReviews
    }


    private fun getSystemReviews(productId: Int, rawPosts: List<Post>): List<Post> {
        val comentariosDelProducto = when (productId) {
            1 -> listOf(
                Pair("Rendimiento excepcional", "He probado software de renderizado y videojuegos AAA, y la respuesta del equipo es sobresaliente."),
                Pair("Sistema de refrigeración", "Mantiene temperaturas estables incluso tras horas de uso continuo."),
                Pair("Calidad de pantalla", "El panel ofrece colores vibrantes y un brillo adecuado.")
            )
            2 -> listOf(
                Pair("Inmersión total", "La curvatura realmente marca la diferencia en juegos."),
                Pair("Fidelidad de color", "Tras calibrarlo, cubre perfectamente el espectro sRGB."),
                Pair("Sin píxeles muertos", "El panel llegó en perfecto estado.")
            )
            3 -> listOf(
                Pair("Sensación táctil", "Los switches rojos son lineales y suaves."),
                Pair("Silencioso", "Me sorprendió que no fuera tan ruidoso."),
                Pair("Materiales duraderos", "Las teclas de doble inyección aseguran durabilidad.")
            )
            4 -> listOf(
                Pair("Precisión del sensor", "El seguimiento es pixel a pixel."),
                Pair("Libertad total", "Olvidarse del cable es lo mejor."),
                Pair("Autonomía bestial", "Llevo tres semanas usándolo a diario.")
            )
            5 -> listOf(
                Pair("Velocidad extrema", "Las pantallas de carga desaparecieron."),
                Pair("Revivió mi PC", "Instalé el sistema aquí y vuela."),
                Pair("Temperaturas bajas", "Se mantiene fresco sin disipador extra.")
            )
            else -> emptyList()
        }

        if (comentariosDelProducto.isEmpty()) return emptyList()

        return rawPosts.mapIndexed { index, apiPost ->
            val (titulo, cuerpo) = comentariosDelProducto[index % comentariosDelProducto.size]
            apiPost.copy(title = titulo, body = cuerpo)
        }
    }
}