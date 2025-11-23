package com.evalenzuela.navigation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evalenzuela.navigation.ui.theme.TechSecondary
import com.evalenzuela.navigation.ui.theme.TechPrimary
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(itemId: Int, viewModel: MainViewModel, onBack: () -> Unit) {
    val item = viewModel.getItem(itemId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (item != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))

                Text(item.title, style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Precio: ${item.price}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TechPrimary
                )

                Spacer(Modifier.height(16.dp))

                Text(item.description, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.addItemToCart(item) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(12.dp),
                    colors = ButtonDefaults.buttonColors(

                        containerColor = TechSecondary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir al Carrito")
                }

            } else {
                Text("Item no encontrado", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}