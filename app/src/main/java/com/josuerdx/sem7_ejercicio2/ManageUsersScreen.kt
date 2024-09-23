package com.josuerdx.sem7_ejercicio2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josuerdx.mydatabase.User
import com.josuerdx.mydatabase.UserDatabase
import kotlinx.coroutines.launch

@Composable
fun ManageUsersScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var userList by remember { mutableStateOf(listOf<User>()) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        val db = UserDatabase.getDatabase(context)
        userList = db.userDao().getAllUsers()
    }

    // Layout principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Usuarios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de usuarios
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            items(userList) { user ->
                UserRow(
                    user = user,
                    onDelete = {
                        coroutineScope.launch {
                            val db = UserDatabase.getDatabase(context)
                            db.userDao().deleteUser(user)
                            userList = db.userDao().getAllUsers()
                        }
                    },
                    onUpdateClick = {
                        selectedUser = user
                        firstName = user.firstName
                        lastName = user.lastName
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Formulario de actualización
        selectedUser?.let {
            Text(
                text = "Actualizar Usuario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Nombre
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombres") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )

            // Apellido
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón actualizar
            Button(
                onClick = {
                    coroutineScope.launch {
                        val db = UserDatabase.getDatabase(context)
                        selectedUser?.let { user ->
                            db.userDao().updateUser(user.copy(firstName = firstName, lastName = lastName))
                            userList = db.userDao().getAllUsers()
                        }
                        selectedUser = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D3E39))
            ) {
                Text("Actualizar Usuario", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun UserRow(user: User, onDelete: () -> Unit, onUpdateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nombres del usuario
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )

            Row {
                // Botón editar
                Button(
                    onClick = onUpdateClick,
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Editar", color = Color.White, fontSize = 14.sp)
                }

                // Botón eliminar
                Button(
                    onClick = onDelete,
                    modifier = Modifier.height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
