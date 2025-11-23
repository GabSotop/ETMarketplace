package com.evalenzuela.navigation.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val cartItems = viewModel.cartItems.collectAsState().value
    val total = viewModel.cartTotal.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Carrito de Compras (${cartItems.size})")},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),


            actions = {
                if (cartItems.isNotEmpty()) {
                    IconButton(onClick = { viewModel.checkout() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Vaciar Carrito",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (cartItems.isEmpty()) {

                EmptyCartMessage(Modifier.weight(1f))
            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemRow(
                            title = cartItem.item.title,
                            price = cartItem.item.price,
                            quantity = cartItem.quantity
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                    }
                }


                CheckoutReceipt(total = total, onCheckoutClick = viewModel::checkout)
            }
        }
    }
}


@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text("Tu carrito está vacío ", style = MaterialTheme.typography.titleMedium)
    }
}


@Composable
fun CartItemRow(title: String, price: String, quantity: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text("Cantidad: $quantity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(price, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}


@Composable
fun CheckoutReceipt(total: String, onCheckoutClick: () -> Unit) {


    var isProcessing by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = if (isProcessing) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
        label = "ButtonColorAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TOTAL A PAGAR:", style = MaterialTheme.typography.headlineSmall)
                Text(
                    total,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isProcessing) {
                        isProcessing = true
                        onCheckoutClick()
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = animatedColor)
            ) {

                Crossfade(targetState = isProcessing, label = "ButtonTextCrossfade") { state ->
                    val buttonText = if (state) "¡Pago Procesado!" else "Finalizar Compra y Pagar"
                    Text(buttonText, style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isProcessing) "¡Gracias por tu compra!" else "¡Recibo generado! Al pagar, el carrito se vaciará.",
                style = MaterialTheme.typography.labelSmall,
                color = if (isProcessing) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}