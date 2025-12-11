package org.duahifnv.filehosting.mobile.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.duahifnv.filehosting.mobile.ui.theme.NeomorphicButton
import org.duahifnv.filehosting.mobile.ui.theme.NeomorphicCard
import org.duahifnv.filehosting.mobile.ui.theme.NeomorphicTextField
import org.duahifnv.filehosting.mobile.ui.utils.formatDate
import org.duahifnv.filehosting.mobile.ui.utils.formatFileSize
import org.duahifnv.filehosting.mobile.ui.viewmodel.DownloadViewModel

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = viewModel()
) {
    var fileId by remember { mutableStateOf("") }
    var isShared by remember { mutableStateOf(false) }
    val fileMeta by viewModel.fileMeta.collectAsState()
    val fileData by viewModel.fileData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                fileData?.let { data ->
                    try {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(data)
                        }
                    } catch (e: Exception) {
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            if (it.contains("Ошибка подключения") || it.contains("Не авторизован")) {
                return@LaunchedEffect
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NeomorphicCard(
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
                    text = "Скачать файл",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                NeomorphicTextField(
                    value = fileId,
                    onValueChange = { fileId = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Введите ID файла (UUID)",
                    enabled = !loading
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isShared,
                        onCheckedChange = { isShared = it }
                    )
                    Text("Из общих файлов")
                }

                NeomorphicButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (fileId.isNotBlank()) {
                            viewModel.loadFileMeta(fileId, isShared)
                        }
                    },
                    enabled = !loading && fileId.isNotBlank()
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Загрузить информацию")
                    }
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

        fileMeta?.let { meta ->
            NeomorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Информация о файле",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    FileInfoRow("Имя", meta.originalName)
                    FileInfoRow("Тип", meta.contentType)
                    FileInfoRow("Размер", formatFileSize(meta.originalSize))
                    FileInfoRow("Владелец", meta.username)
                    FileInfoRow("Загружен", formatDate(meta.createdAt))
                    meta.expiresAt?.let {
                        FileInfoRow("Истекает", formatDate(it))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (fileData == null) {
                        NeomorphicButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.downloadFile(fileId, isShared)
                            },
                            enabled = !loading
                        ) {
                            if (loading) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    Text("Загрузка...")
                                }
                            } else {
                                Text("Загрузить файл")
                            }
                        }
                    } else {
                        NeomorphicButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = meta.contentType
                                    putExtra(Intent.EXTRA_TITLE, meta.originalName)
                                }
                                saveFileLauncher.launch(intent)
                            },
                            enabled = !loading
                        ) {
                            Text("Скачать файл")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontSize = 14.sp
        )
    }
}

