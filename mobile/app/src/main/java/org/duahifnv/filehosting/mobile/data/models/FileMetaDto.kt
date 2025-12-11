package org.duahifnv.filehosting.mobile.data.models

data class FileMetaDto(
    val id: String,
    val username: String,
    val originalName: String,
    val contentType: String,
    val originalSize: Long,
    val createdAt: String,
    val expiresAt: String?
)
