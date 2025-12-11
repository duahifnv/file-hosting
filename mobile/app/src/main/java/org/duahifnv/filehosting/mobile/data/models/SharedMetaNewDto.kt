package org.duahifnv.filehosting.mobile.data.models

data class SharedMetaNewDto(
    val sharedUsersEmails: List<String>,
    val sharingLifetime: String? = null
)
