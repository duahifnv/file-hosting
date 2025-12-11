package org.duahifnv.filehosting.mobile.data.models

data class UserFormDto(
    val username: String? = null,
    val email: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val password: String? = null
)
