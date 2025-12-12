package com.evalenzuela.navigation

import com.evalenzuela.navigation.data.model.Item
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun agregarItemAlCarrito() = runTest {
        val viewModel = MainViewModel()
        val item = Item(1, "Laptop", "Desc", "$1000", "")

        viewModel.addItemToCart(item)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals("Laptop", viewModel.cartItems.value[0].item.title)
    }

    @Test
    fun eliminarItemDelCarrito() = runTest {
        val viewModel = MainViewModel()
        val item = Item(1, "Laptop", "Desc", "$1000", "")

        viewModel.addItemToCart(item)
        viewModel.removeItemFromCart(item)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun checkoutVaciaCarrito() = runTest {
        val viewModel = MainViewModel()
        viewModel.addItemToCart(Item(1, "A", "D", "$100", ""))

        viewModel.checkout()

        assertTrue(viewModel.cartItems.value.isEmpty())
    }

    @Test
    fun seguridadAdmin() {
        val viewModel = MainViewModel()
        assertFalse(viewModel.validateAdminAccess("1234"))
        assertTrue(viewModel.validateAdminAccess("admin123"))
    }

    @Test
    fun validacionEmail() {
        val viewModel = MainViewModel()
        viewModel.onEmailChange("correomalo")
        assertFalse(viewModel.validateAndSaveProfile())
    }
}