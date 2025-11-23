package com.evalenzuela.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.evalenzuela.navigation.data.model.Post
import com.evalenzuela.navigation.data.repository.PostRepositoryInterface
import com.evalenzuela.navigation.ui.screens.PostScreen
import com.evalenzuela.navigation.ui.viewmodel.PostViewModel
import org.junit.Rule
import org.junit.Test


class FakePostRepositoryForUI : PostRepositoryInterface {
    override suspend fun getPosts(): List<Post> {
        return listOf(
            Post(1, 1, "Titulo Test UI", "Cuerpo Test UI"),
            Post(2, 2, "Otro Post", "Otro Cuerpo")
        )
    }
}

class PostScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun muestraElTituloYLosPosts() {

        val fakeRepo = FakePostRepositoryForUI()


        val fakeViewModel = PostViewModel(fakeRepo)

        composeTestRule.setContent {
            PostScreen(viewModel = fakeViewModel)
        }


        composeTestRule.onNodeWithText("Listado de Posts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Titulo: Titulo Test UI").assertIsDisplayed()
    }
}