package com.evalenzuela.navigation

import com.evalenzuela.navigation.data.model.Item
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

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
    fun `agregar item al carrito actualiza la cantidad y el estado`() = runTest {
        val viewModel = MainViewModel()
        val itemPrueba = Item(
            id = 99,
            title = "Test Product",
            description = "Desc",
            price = "$1.000",
            imageUrl = ""
        )


        viewModel.addItemToCart(itemPrueba)


        testDispatcher.scheduler.advanceUntilIdle()

        val cart = viewModel.cartItems.value
        assertEquals(1, cart.size)

        assertEquals(itemPrueba.title, cart[0].item.title)
    }

    @Test
    fun `checkout vacia el carrito y reinicia el total`() = runTest {

        val viewModel = MainViewModel()
        val itemPrueba = Item(99, "Test", "Desc", "$1.000", "")
        viewModel.addItemToCart(itemPrueba)


        assertEquals(1, viewModel.cartItems.value.size)


        viewModel.checkout()


        assertEquals(0, viewModel.cartItems.value.size)


        assertTrue(viewModel.cartTotal.value.contains("0"))
    }
}