package com.evalenzuela.navigation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evalenzuela.navigation.data.model.Item
import com.evalenzuela.navigation.data.repository.SampleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class CartItem(
    val item: Item,
    val quantity: Int = 1
)
enum class CurrentProfileType {
    BUYER,
    SELLER
}
class MainViewModel(
    private val repo: SampleRepository = SampleRepository()
) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartTotal = MutableStateFlow(formatCurrency(0.0))
    val cartTotal: StateFlow<String> = _cartTotal.asStateFlow()

    private val _userProfileType = MutableStateFlow(CurrentProfileType.BUYER)
    val userProfileType: StateFlow<CurrentProfileType> = _userProfileType.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private fun loadItems() {
        viewModelScope.launch {
            _items.value = repo.getAll()
        }
    }


    init {
        loadItems()
    }


    fun getItem(id: Int): Item? = repo.getById(id)

    fun addItemToCart(item: Item) {
        val currentCart = _cartItems.value.toMutableList()

        val existingItem = currentCart.find { it.item.id == item.id }

        if (existingItem != null) {

            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            currentCart[currentCart.indexOf(existingItem)] = updatedItem
        } else {

            currentCart.add(CartItem(item))
        }

        _cartItems.value = currentCart
        calculateCartTotal()
    }

    fun checkout() {
        _cartItems.value = emptyList()
        _cartTotal.value = formatCurrency(0.0)
    }

    fun switchProfileType(newType: CurrentProfileType) {
        _userProfileType.value = newType
    }

    fun onEmailChange(newEmail: String) {
        _userEmail.value = newEmail
        _emailError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = null
    }

    fun validateAndSaveProfile(): Boolean {
        var isValid = true
        val email = _userEmail.value
        val password = _password.value

        if (email.isBlank()) {
            _emailError.value = "El correo no puede estar vacío."
            isValid = false
        } else if (!email.contains('@') || !email.contains('.')) {
            _emailError.value = "Formato de correo inválido (ej: usuario@dominio.cl)."
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.length < 6) {
            _passwordError.value = "Debe tener al menos 6 caracteres."
            isValid = false
        } else if (!password.any { it.isDigit() }) {
            _passwordError.value = "La contraseña debe contener al menos un número."
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    fun addNewItem(title: String, price: String, imageUrl: String) {
        val newItem = Item(
            id = -1,
            title = title,
            description = "Producto nuevo agregado por el vendedor.",
            price = price,
            imageUrl = imageUrl
        )
        viewModelScope.launch {
            repo.addItem(newItem)
            loadItems()
        }
    }






    private fun calculateCartTotal() {
        var total = 0.0

        for (cartItem in _cartItems.value) {

            val priceString = cartItem.item.price
                .replace(".", "")
                .replace("$", "")
                .replace(",", ".")
                .trim()

            val price = priceString.toDoubleOrNull() ?: 0.0
            total += price * cartItem.quantity
        }


        _cartTotal.value = formatCurrency(total)
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 0

        return format.format(amount).replace("CLP", "$").trim()
    }
}