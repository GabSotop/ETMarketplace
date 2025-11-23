package com.evalenzuela.navigation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.data.repository.PostRepository
import com.evalenzuela.navigation.data.repository.PostRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepositoryInterface = PostRepository()
) : ViewModel() {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList

    private val titulosResenas = listOf(
        "Laptop Gamer: ¡Corre todo en Ultra!",
        "Monitor Curvo 27: Inmersión total",
        "Teclado Mecánico: El sonido es adictivo",
        "Mouse Inalámbrico: Cero latencia",
        "Disco SSD 1TB: Mi PC vuela ahora",
        "Laptop Gamer: Buena compra, pero se calienta un poco",
        "Monitor Curvo: Llegó impecable a Temuco",
        "Teclado Mecánico: Ideal para programar y jugar"
    )


    private val cuerposResenas = listOf(
        "Probé el Cyberpunk y el Valorant y los FPS son estables. La pantalla se ve increíble. Vale cada peso invertido.",
        "Perfecto para diseño gráfico y gaming. La curvatura ayuda mucho a la vista después de horas de trabajo.",
        "Los switches rojos son muy suaves. La retroiluminación RGB le da el toque perfecto a mi setup.",
        "Tenía miedo del delay por ser inalámbrico, pero responde igual que uno con cable. La batería dura semanas.",
        "Windows inicia en 5 segundos. La instalación fue fácil y el envío de la tienda fue muy rápido.",
        "El rendimiento es bestial, aunque los ventiladores suenan fuerte cuando juego cosas pesadas. Recomiendo usar base.",
        "Tenía dudas por el envío a región, pero venía con doble protección. Ningún pixel quemado.",
        "Lo uso para trabajar todo el día y mis muñecas lo agradecen. Excelente relación precio-calidad."
    )

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {

                val rawPosts = repository.getPosts()


                val productReviews = rawPosts.mapIndexed { index, post ->
                    post.copy(

                        title = titulosResenas[index % titulosResenas.size],
                        body = cuerposResenas[index % cuerposResenas.size]
                    )
                }

                _postList.value = productReviews

            } catch (e: Exception) {
                println("Error al obtener reseñas: ${e.localizedMessage}")
            }
        }
    }
}