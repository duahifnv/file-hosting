package org.duahifnv.filehosting.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.duahifnv.filehosting.mobile.data.TokenStore
import org.duahifnv.filehosting.mobile.data.models.RegisterDto
import org.duahifnv.filehosting.mobile.data.models.UserFormDto
import org.duahifnv.filehosting.mobile.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    tokenStore: TokenStore,
    viewModel: AuthViewModel = viewModel { AuthViewModel(tokenStore) }
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val userForm by viewModel.userForm.collectAsState()
    val error by viewModel.error.collectAsState()

    if (isAuthenticated && userForm != null) {
        EditProfileScreen(viewModel, userForm!!)
    } else {
        AuthScreen(viewModel)
    }

    error?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
}

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isLogin) "Вход" else "Регистрация",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Имя пользователя") }
                )

                if (!isLogin) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Email") }
                    )

                    TextField(
                        value = firstname,
                        onValueChange = { firstname = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Имя") }
                    )

                    TextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Фамилия") }
                    )
                }

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Пароль") }
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (isLogin) {
                            viewModel.login(username, password)
                        } else {
                            if (username.isNotBlank() && password.isNotBlank() &&
                                email.isNotBlank() && firstname.isNotBlank() && lastname.isNotBlank()
                            ) {
                                viewModel.register(
                                    RegisterDto(username, password, email, firstname, lastname)
                                )
                            }
                        }
                    }
                ) {
                    Text(if (isLogin) "Войти" else "Зарегистрироваться")
                }

                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(
                        text = if (isLogin) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти",
                        fontSize = 14.sp
                    )
                }

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileScreen(viewModel: AuthViewModel, initialUserForm: UserFormDto) {
    var email by remember { mutableStateOf(initialUserForm.email) }
    var firstname by remember { mutableStateOf(initialUserForm.firstname ?: "") }
    var lastname by remember { mutableStateOf(initialUserForm.lastname ?: "") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    LaunchedEffect(initialUserForm) {
        email = initialUserForm.email
        firstname = initialUserForm.firstname ?: ""
        lastname = initialUserForm.lastname ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Профиль",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                initialUserForm.username?.let {
                    Text(
                        text = "Имя пользователя: $it",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Email") }
                )

                TextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Имя") }
                )

                TextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Фамилия") }
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Новый пароль (оставьте пустым, чтобы не менять)") }
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.updateUser(
                            UserFormDto(
                                username = initialUserForm.username,
                                email = email,
                                firstname = firstname,
                                lastname = lastname,
                                password = if (password.isNotBlank()) password else null
                            )
                        )
                    }
                ) {
                    Text("Сохранить изменения")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.logout() }
                ) {
                    Text("Выйти")
                }

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
