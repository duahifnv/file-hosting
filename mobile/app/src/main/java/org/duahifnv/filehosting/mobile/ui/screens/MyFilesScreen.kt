package org.duahifnv.filehosting.mobile.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.duahifnv.filehosting.mobile.data.models.FileMetaDto
import org.duahifnv.filehosting.mobile.ui.utils.formatDate
import org.duahifnv.filehosting.mobile.ui.utils.formatFileSize
import org.duahifnv.filehosting.mobile.ui.viewmodel.FilesViewModel
import java.io.File

@Composable
fun MyFilesScreen(
    viewModel: FilesViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val myFiles by viewModel.myFiles.collectAsState()
    val sharedFiles by viewModel.sharedFiles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> viewModel.loadMyFiles()
            1 -> viewModel.loadSharedFiles()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Мои файлы") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Общие файлы") }
            )
        }

        when (selectedTab) {
            0 -> FilesTab(
                files = myFiles,
                loading = loading,
                error = error,
                viewModel = viewModel,
                isShared = false
            )
            1 -> FilesTab(
                files = sharedFiles,
                loading = loading,
                error = error,
                viewModel = viewModel,
                isShared = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesTab(
    files: List<FileMetaDto>,
    loading: Boolean,
    error: String?,
    viewModel: FilesViewModel,
    isShared: Boolean
) {
    var contentTypeFilter by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf<String?>(null) }
    var sortDirection by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val tempFile = File(context.cacheDir, "temp_upload")
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
                val mediaType = mimeType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()
                val requestFile = tempFile.asRequestBody(mediaType)

                val fileName = getFileName(context, it) ?: "uploaded_file"
                val body = MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    requestFile
                )

                viewModel.uploadFile(body)
                tempFile.delete()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isShared) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Загрузить файл",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            filePickerLauncher.launch("*/*")
                        }
                    ) {
                        Text("Выбрать файл")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Фильтры",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    value = contentTypeFilter,
                    onValueChange = { contentTypeFilter = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Тип контента (например, image/png)") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var expandedSort by remember { mutableStateOf(false) }
                    var expandedDirection by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedSort,
                            onExpandedChange = { expandedSort = !expandedSort }
                        ) {
                            TextField(
                                value = sortBy ?: "Без сортировки",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                placeholder = { Text("Сортировка") },
                                enabled = false
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSort,
                                onDismissRequest = { expandedSort = false }
                            ) {
                                listOf(
                                    null to "Без сортировки",
                                    "originalName" to "Имя",
                                    "contentType" to "Тип",
                                    "originalSize" to "Размер",
                                    "createdAt" to "Дата создания",
                                    "expiresAt" to "Дата истечения"
                                ).forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            sortBy = value
                                            expandedSort = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedDirection,
                            onExpandedChange = { expandedDirection = !expandedDirection }
                        ) {
                            TextField(
                                value = sortDirection ?: "ASC",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                placeholder = { Text("Направление") },
                                enabled = false
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDirection,
                                onDismissRequest = { expandedDirection = false }
                            ) {
                                listOf("ASC" to "По возрастанию", "DESC" to "По убыванию")
                                    .forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                sortDirection = value
                                                expandedDirection = false
                                            }
                                        )
                                    }
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (isShared) {
                            viewModel.loadSharedFiles(
                                contentType = contentTypeFilter.ifBlank { null },
                                sort = sortBy,
                                sortDirection = sortDirection
                            )
                        } else {
                            viewModel.loadMyFiles(
                                contentType = contentTypeFilter.ifBlank { null },
                                sort = sortBy,
                                sortDirection = sortDirection
                            )
                        }
                    }
                ) {
                    Text("Применить фильтры")
                }
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(files) { file ->
                    FileItem(
                        file = file,
                        onDelete = {
                            viewModel.deleteFile(file.id, isShared)
                        },
                        isShared = isShared
                    )
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: FileMetaDto,
    onDelete: () -> Unit,
    isShared: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = file.originalName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Тип: ${file.contentType}",
                fontSize = 14.sp
            )
            Text(
                text = "Размер: ${formatFileSize(file.originalSize)}",
                fontSize = 14.sp
            )
            Text(
                text = "Владелец: ${file.username}",
                fontSize = 14.sp
            )
            Text(
                text = "Загружен: ${formatDate(file.createdAt)}",
                fontSize = 14.sp
            )
            file.expiresAt?.let {
                Text(
                    text = "Истекает: ${formatDate(it)}",
                    fontSize = 14.sp
                )
            }

            if (!isShared) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDelete
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}

fun getFileName(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}
