package com.evalenzuela.navigation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.evalenzuela.navigation.ui.viewmodel.CurrentProfileType
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val profileType = viewModel.userProfileType.collectAsState().value
    val context = LocalContext.current

    // Estados para el bloqueo del Admin
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPasswordInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Perfil: $profileType") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
        )}
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Text("Seleccionar Rol:")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CurrentProfileType.values().forEach { role ->
                    FilterChip(
                        selected = profileType == role,
                        onClick = {
                            if (role == CurrentProfileType.ADMIN) {

                                showAdminLoginDialog = true
                            } else {

                                viewModel.switchProfileType(role)
                            }
                        },
                        label = { Text(role.toString()) }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            when (profileType) {
                CurrentProfileType.SELLER -> SellerCameraContent(viewModel)
                CurrentProfileType.BUYER -> BuyerProfileContent(viewModel)
                CurrentProfileType.ADMIN -> Text("MODO ADMINISTRADOR ACTIVO\n\nTienes permisos totales sobre la aplicación. Puedes eliminar productos desde el Home.", color = MaterialTheme.colorScheme.primary)
                CurrentProfileType.GUEST -> Text("Modo Invitado. Puedes ver productos y agregar al carrito, pero inicia sesión para finalizar compra.")
            }
        }
    }

   if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminLoginDialog = false
                adminPasswordInput = ""
            },
            icon = { Icon(Icons.Default.Lock, contentDescription = null) },
            title = { Text("Acceso Restringido") },
            text = {
                Column {
                    Text("Este perfil es solo para personal autorizado.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = { adminPasswordInput = it },
                        label = { Text("Clave de Admin") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (viewModel.validateAdminAccess(adminPasswordInput)) {
                        viewModel.switchProfileType(CurrentProfileType.ADMIN)
                        Toast.makeText(context, "Acceso concedido", Toast.LENGTH_SHORT).show()
                        showAdminLoginDialog = false
                    } else {
                        Toast.makeText(context, "Clave incorrecta", Toast.LENGTH_SHORT).show()
                    }
                    adminPasswordInput = ""
                }) {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdminLoginDialog = false
                    adminPasswordInput = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BuyerProfileContent(viewModel: MainViewModel) {
    val email by viewModel.userEmail.collectAsState()
    val password by viewModel.password.collectAsState()
    var showRecoverDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("Acceso Clientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        TextButton(onClick = { showRecoverDialog = true }) {
            Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = {
                if(viewModel.validateAndSaveProfile()){
                    Toast.makeText(context, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error en credenciales", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }

    if (showRecoverDialog) {
        AlertDialog(
            onDismissRequest = { showRecoverDialog = false },
            title = { Text("Recuperar Contraseña") },
            text = { Text("Ingresa tu correo para enviarte un enlace de recuperación temporal.") },
            confirmButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                    showRecoverDialog = false
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoverDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun SellerCameraContent(viewModel: MainViewModel) {
    var productName by remember { mutableStateOf("") }
    var productDesc by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productUrl by remember { mutableStateOf("") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        capturedBitmap = it
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
        else Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Publicar Producto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text("Imagen del producto:", style = MaterialTheme.typography.labelMedium)

        if (capturedBitmap != null) {
            Image(
                bitmap = capturedBitmap!!.asImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier.size(100.dp)
            )
            TextButton(onClick = { capturedBitmap = null }) { Text("Quitar foto") }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) cameraLauncher.launch(null)
                    else permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cámara")
                }
            }
            Text("O ingresa una URL web:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top=8.dp))
            OutlinedTextField(
                value = productUrl,
                onValueChange = { productUrl = it },
                label = { Text("https://ejemplo.com/imagen.jpg") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Nombre Producto") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = productDesc,
            onValueChange = { productDesc = it },
            label = { Text("Descripción (Detalles técnicos, estado, etc.)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = productPrice, onValueChange = { productPrice = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                if (productName.isNotEmpty() && productPrice.isNotEmpty() && productDesc.isNotEmpty()) {
                    val finalImageUrl = if (productUrl.isNotBlank()) productUrl else "https://via.placeholder.com/150"

                    viewModel.addNewItem(productName, productDesc, productPrice, finalImageUrl)

                    Toast.makeText(context, "Producto Publicado", Toast.LENGTH_SHORT).show()
                    productName = ""
                    productDesc = ""
                    productPrice = ""
                    productUrl = ""
                    capturedBitmap = null
                } else {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Guardar en Catálogo")
        }


        Spacer(Modifier.height(50.dp))
    }
}