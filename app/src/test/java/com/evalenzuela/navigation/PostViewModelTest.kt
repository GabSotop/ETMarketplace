package com.evalenzuela.navigation

import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.data.repository.PostRepositoryInterface
import com.evalenzuela.navigation.ui.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class RepoFalsoSeparado : PostRepositoryInterface {
    override suspend fun getPosts(): List<Post> {
        return listOf(
            Post(1, 1, "API Titulo", "API Cuerpo"),
            Post(1, 2, "API Titulo", "API Cuerpo"),
            Post(1, 3, "API Titulo", "API Cuerpo")
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TEST 6
    @Test
    fun cargaInicial() = runTest {
        val viewModel = PostViewModel(RepoFalsoSeparado())
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(3, viewModel.postList.value.size)
    }

    // TEST 7
    @Test
    fun productoNuevoSinResenas() = runTest {
        val viewModel = PostViewModel(RepoFalsoSeparado())
        testDispatcher.scheduler.advanceUntilIdle()
        val reviews = viewModel.getReviewsForProduct(99)
        assertTrue(reviews.isEmpty())
    }

    // TEST 8
    @Test
    fun resenaLaptop() = runTest {
        val viewModel = PostViewModel(RepoFalsoSeparado())
        testDispatcher.scheduler.advanceUntilIdle()
        val reviews = viewModel.getReviewsForProduct(1)
        assertEquals("Rendimiento excepcional", reviews[0].title)
    }

    // TEST 9
    @Test
    fun resenaMonitor() = runTest {
        val viewModel = PostViewModel(RepoFalsoSeparado())
        testDispatcher.scheduler.advanceUntilIdle()
        val reviews = viewModel.getReviewsForProduct(2)
        assertEquals("Inmersión total", reviews[0].title)
    }

    // TEST 10
    @Test
    fun reemplazoIdioma() = runTest {
        val viewModel = PostViewModel(RepoFalsoSeparado())
        testDispatcher.scheduler.advanceUntilIdle()
        val reviews = viewModel.getReviewsForProduct(3)
        // Verifica que NO usa el texto "API Titulo" del repo falso
        assertEquals("Sensación táctil", reviews[0].title)
    }
}