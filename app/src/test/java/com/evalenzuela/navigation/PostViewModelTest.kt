package com.evalenzuela.navigation

import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.data.repository.PostRepositoryInterface
import com.evalenzuela.navigation.ui.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class FakePostRepository : PostRepositoryInterface {
    override suspend fun getPosts(): List<Post> {
        return listOf(
            Post(1, 1, "Titulo Mock 1", "Body 1"),
            Post(1, 2, "Titulo Mock 2", "Body 2")
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando ViewModel inicia, carga la lista de posts correctamente`() = runTest {

        val fakeRepo = FakePostRepository()
        val viewModel = PostViewModel(fakeRepo)


        testDispatcher.scheduler.advanceUntilIdle()
        val posts = viewModel.postList.first()

        assertEquals(2, posts.size)
        assertEquals("Titulo Mock 1", posts[0].title)
    }
}