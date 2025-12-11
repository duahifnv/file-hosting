package org.duahifnv.filehosting.mobile.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.duahifnv.filehosting.mobile.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth")
    suspend fun authenticate(@Body authDto: AuthDto): Response<JwtDto>

    @POST("/api/register")
    suspend fun register(@Body registerDto: RegisterDto): Response<JwtDto>

    @GET("/api/user/me")
    suspend fun getUserForm(): Response<UserFormDto>

    @PUT("/api/user/me")
    suspend fun updateUser(@Body userFormDto: UserFormDto): Response<Unit>

    @GET("/api/user")
    suspend fun getUser(@Query("email") email: String): Response<UserBasicDto>

    @GET("/api/users")
    suspend fun getAllUsers(): Response<UsersBasicDto>

    @POST("/api/files")
    @Multipart
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<Map<String, Any>>

    @GET("/api/files/{fileId}")
    suspend fun getFileById(
        @Path("fileId") fileId: String,
        @Query("shared") shared: Boolean? = null
    ): Response<ResponseBody>

    @DELETE("/api/files/{fileId}")
    suspend fun removeFile(@Path("fileId") fileId: String): Response<Unit>

    @GET("/api/file-metas")
    suspend fun getAllFileMetas(
        @Query("contentType") contentType: String? = null,
        @Query("shared") shared: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String? = null,
        @Query("sortDirection") sortDirection: String? = null
    ): Response<FileMetasDto>

    @GET("/api/file-metas/{fileId}")
    suspend fun getFileMeta(
        @Path("fileId") fileId: String,
        @Query("shared") shared: Boolean? = null
    ): Response<FileMetaDto>

    @PUT("/api/files/sharing/{fileId}")
    suspend fun addSharedFile(
        @Path("fileId") fileId: String,
        @Body sharedMetaNewDto: SharedMetaNewDto
    ): Response<SharedMetaDto>

    @DELETE("/api/files/sharing/{fileId}")
    suspend fun removeAllSharesFromFile(@Path("fileId") fileId: String): Response<Unit>
}
