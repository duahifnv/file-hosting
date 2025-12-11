package org.duahifnv.filehosting.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.duahifnv.filehosting.mobile.data.TokenStore
import org.duahifnv.filehosting.mobile.ui.screens.DownloadScreen
import org.duahifnv.filehosting.mobile.ui.screens.MyFilesScreen
import org.duahifnv.filehosting.mobile.ui.screens.ProfileScreen
import org.duahifnv.filehosting.mobile.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenStore = TokenStore(this)

        setContent {
            MaterialTheme {
                MainScreen(tokenStore)
            }
        }
    }
}

@Composable
fun MainScreen(tokenStore: TokenStore) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(tokenStore) }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Скачать") },
                    label = { Text("Скачать") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { if (isAuthenticated) selectedTab = 1 },
                    enabled = isAuthenticated,
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Мои файлы") },
                    label = { Text("Мои файлы") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Профиль") },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> DownloadScreen()
                1 -> if (isAuthenticated) MyFilesScreen() else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Войдите в систему для доступа к файлам")
                    }
                }
                2 -> ProfileScreen(tokenStore, authViewModel)
            }
        }
    }
}
