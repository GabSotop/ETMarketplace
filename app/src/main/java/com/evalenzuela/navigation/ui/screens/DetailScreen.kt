package com.evalenzuela.navigation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel
import com.evalenzuela.navigation.ui.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: Int,
    viewModel: MainViewModel,
    postViewModel: PostViewModel,
    onBack: () -> Unit
) {
    val item = viewModel.getItem(itemId)
    val context = LocalContext.current


    val apiState = postViewModel.postList.collectAsState().value

    val localReviewsState = postViewModel.userLocalReviews.collectAsState().value


    var newReviewText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.title ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (item != null) {
                // --- IMAGEN ---
                Card(elevation = CardDefaults.cardElevation(4.dp)) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(item.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(8.dp))
                Text(text = item.price, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("Descripción:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(item.description, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.addItemToCart(item)
                        Toast.makeText(context, "¡Producto agregado al carrito!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir al Carrito")
                }

                Spacer(Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))


                Text("Escribe tu opinión:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = newReviewText,
                    onValueChange = { newReviewText = it },
                    label = { Text("¿Qué te pareció el producto?") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (newReviewText.isNotBlank()) {
                                postViewModel.addUserReview(item.id, "Yo (Comprador)", newReviewText)
                                newReviewText = ""
                                Toast.makeText(context, "Reseña publicada", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))


                Text("Opiniones de compradores:", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(12.dp))


                val specificReviews = remember(item.id, apiState, localReviewsState) {
                    postViewModel.getReviewsForProduct(item.id)
                }

                if (apiState.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (specificReviews.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Aún no hay reseñas. ¡Sé el primero en opinar!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                            )
                        }
                    }
                } else {
                    specificReviews.forEach { post ->
                        ReviewItem(author = if(post.userId == 999) post.title else "Usuario Verificado", comment = post.body, isLocal = post.userId == 999)
                        Spacer(Modifier.height(12.dp))
                    }
                }

            } else {
                Text("Producto no encontrado", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ReviewItem(author: String, comment: String, isLocal: Boolean = false) {

    val cardColor = if (isLocal) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isLocal) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isLocal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(author, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(Modifier.width(8.dp))
                    Row {
                        repeat(5) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFC107))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(comment, style = MaterialTheme.typography.bodyMedium, color = textColor)
            }
        }
    }
}