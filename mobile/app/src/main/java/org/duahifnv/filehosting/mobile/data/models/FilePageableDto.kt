package org.duahifnv.filehosting.mobile.data.models

data class FilePageableDto(
    val page: Int = 0,
    val size: Int = 10,
    val sort: String? = null,
    val sortDirection: String? = null
)
