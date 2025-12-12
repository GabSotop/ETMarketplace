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

data class CartItem(val item: Item, val quantity: Int = 1)

enum class CurrentProfileType {
    GUEST,
    BUYER,
    SELLER,
    ADMIN;

    override fun toString(): String {
        return when(this) {
            GUEST -> "Invitado"
            BUYER -> "Comprador"
            SELLER -> "Vendedor"
            ADMIN -> "Administrador"
        }
    }
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

    private val _userProfileType = MutableStateFlow(CurrentProfileType.GUEST)
    val userProfileType: StateFlow<CurrentProfileType> = _userProfileType.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _locationData = MutableStateFlow("Ubicación no obtenida")
    val locationData: StateFlow<String> = _locationData.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()


    private val ADMIN_SECRET_CODE = "admin123"

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch { _items.value = repo.getAll() }
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

    fun removeItemFromCart(item: Item) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.item.id == item.id }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
                currentCart[currentCart.indexOf(existingItem)] = updatedItem
            } else {
                currentCart.remove(existingItem)
            }
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


    fun validateAdminAccess(inputPass: String): Boolean {
        return inputPass == ADMIN_SECRET_CODE
    }

    fun updateLocation(loc: String) {
        _locationData.value = loc
    }
    fun addNewItem(title: String, description: String, price: String, imageUrl: String) {
        if (_userProfileType.value != CurrentProfileType.SELLER && _userProfileType.value != CurrentProfileType.ADMIN) return

        val newItem = Item(
            id = -1,
            title = title,
            description = description,
            price = price,
            imageUrl = imageUrl
        )
        viewModelScope.launch {
            repo.addItem(newItem)

            _items.value = repo.getAll()
        }
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
            _emailError.value = "Formato inválido."
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.length < 6) {
            _passwordError.value = "Mínimo 6 caracteres."
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }
    private var recentlyDeletedItem: Item? = null
    private var recentlyDeletedIndex: Int = -1


    fun deleteItem(item: Item) {
        val currentList = _items.value.toMutableList()
        val index = currentList.indexOf(item)

        if (index != -1) {
            recentlyDeletedIndex = index
            recentlyDeletedItem = item

            currentList.removeAt(index)
            _items.value = currentList


        }
    }

    fun restoreDeletedItem() {
        if (recentlyDeletedItem != null && recentlyDeletedIndex != -1) {
            val currentList = _items.value.toMutableList()

            currentList.add(recentlyDeletedIndex, recentlyDeletedItem!!)
            _items.value = currentList


            recentlyDeletedItem = null
            recentlyDeletedIndex = -1
        }
    }


    private fun calculateCartTotal() {
        var total = 0.0
        for (cartItem in _cartItems.value) {
            val priceString = cartItem.item.price.replace(".", "").replace("$", "").replace(",", ".").trim()
            total += (priceString.toDoubleOrNull() ?: 0.0) * cartItem.quantity
        }
        _cartTotal.value = formatCurrency(total)
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("CLP", "$").trim()
    }
}
