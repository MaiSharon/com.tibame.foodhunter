
package com.tibame.foodhunter.zoe

// 原有的導入
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


class PostRepository {

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()
    private val gson = Gson()

    private val _restRelatedPost = MutableStateFlow<List<Post>>(emptyList())
    val restRelatedPost: StateFlow<List<Post>> = _restRelatedPost.asStateFlow()

    private suspend fun fetchComments(postId: Int): List<CommentResponse> {
        val url = "$serverUrl/comment/byPost?postId=$postId"
        val result = CommonPost(url, "")
        val type = object : TypeToken<List<CommentResponse>>() {}.type
        return try {
            gson.fromJson(result, type)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching comments", e)
            emptyList()
        }
    }
    private suspend fun CommentResponse.toComment(profileImage: ImageBitmap? = null): Comment {
        return Comment(
            id = messageId,
            commenter = Commenter(
                id = memberId,
                name = memberNickname,
                avatarBitmap = profileImage
            ),
            content = content,
            timestamp = messageTime
        )
    }
    // 獲取貼文列表
    private suspend fun fetchPosts(): List<PostResponse> {
        val url = "$serverUrl/post/preLoad"
        val result = CommonPost(url, "")
        val type = object : TypeToken<List<PostResponse>>() {}.type
        return gson.fromJson(result, type)
    }

    private fun processBase64Image(photoData: String?, photoId: Int): ImageBitmap? {
        try {
            // 檢查 photoData
            if (photoData == null) {
                Log.e("PostRepository", "photoData is null for photo $photoId")
                return null
            }
            Log.d("PostRepository", "photoData found for photo $photoId")

            // 清理 base64 數據
            val cleanBase64 = photoData.let { base64Data ->
                Log.d(
                    "PostRepository",
                    "Original base64 length for photo $photoId: ${base64Data.length}"
                )
                val cleaned = base64Data
                    .replace("data:image/\\w+;base64,".toRegex(), "")
                    .replace("\\s".toRegex(), "")
                Log.d(
                    "PostRepository",
                    "Cleaned base64 length for photo $photoId: ${cleaned.length}"
                )
                cleaned
            }

            // Base64 解碼
            val decodedBytes = try {
                Log.d("PostRepository", "Attempting Base64 decode for photo $photoId")
                Base64.decode(cleanBase64, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                Log.e("PostRepository", "Base64 decode failed for photo $photoId", e)
                return null
            }
            Log.d(
                "PostRepository",
                "Base64 decode successful for photo $photoId, bytes length: ${decodedBytes.size}"
            )

            // 位圖轉換
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            if (bitmap == null) {
                Log.e("PostRepository", "Bitmap decode failed for photo $photoId")
                return null
            }
            Log.d(
                "PostRepository",
                "Bitmap created successfully for photo $photoId, size: ${bitmap.width}x${bitmap.height}"
            )

            return bitmap.asImageBitmap()

        } catch (e: Exception) {
            Log.e("PostRepository", "Error processing photo $photoId", e)
            e.printStackTrace()
            return null
        }
    }
    private suspend fun PostResponse.toPost(): Post {
        // 圖片處理
        val carouselItems = photos?.mapNotNull { photo ->
            Log.d("PostRepository", "處理貼文照片 ID: ${photo.postPhotoId}")
            val imageBitmap = processBase64Image(photo.photoFile, photo.postPhotoId)
            imageBitmap?.let {
                Log.d("PostRepository", "成功處理貼文照片 ID: ${photo.postPhotoId}")
                CarouselItem(
                    id = photo.postPhotoId,
                    imageData = it,
                    order = 0
                )
            } ?: run {
                Log.e("PostRepository", "無法處理貼文照片 ID: ${photo.postPhotoId}")
                null
            }
        } ?: emptyList()

        Log.d("PostRepository", "成功處理 ${carouselItems.size} 張貼文照片")

        // 處理發布者頭像
        Log.d("PostRepository", "開始處理發布者 $publisher 的頭像")
        val publisherProfileBitmap = if (!publisherProfileImage.isNullOrEmpty()) {
            try {
                val bitmap = processBase64Image(publisherProfileImage, publisher)
                Log.d("PostRepository", "發布者頭像處理${if (bitmap != null) "成功" else "失敗"}")
                bitmap
            } catch (e: Exception) {
                Log.e("PostRepository", "處理發布者頭像時發生錯誤", e)
                null
            }
        } else {
            Log.d("PostRepository", "發布者沒有頭像數據")
            null
        }

        // Initialize post without comments
        return Post(
            postId = this.postId,
            publisher = Publisher(
                id = this.publisher,
                name = this.publisherNickname ?: "Unknown User",
                avatarBitmap = publisherProfileBitmap
            ),
            content = this.content,
            location = this.restaurantName ?: "Unknown Location",
            timestamp = this.postTime,
            postTag = this.postTag,
            carouselItems = carouselItems,
            comments = emptyList(),  // Initialize with empty comments
            isFavorited = false
        )
    }
    suspend fun loadCommentsForPost(postId: Int) {
        try {
            Log.d("PostRepository", "開始載入貼文 $postId 的評論")
            val commentResponses = fetchComments(postId)
            val comments = commentResponses.map { commentResponse ->
                Log.d("PostRepository", """
                處理評論:
                Comment ID: ${commentResponse.messageId}
                Commenter ID: ${commentResponse.memberId}
                Commenter Name: ${commentResponse.memberNickname}
                Profile Image 是否存在: ${!commentResponse.profileImage.isNullOrEmpty()}
                """.trimIndent())

                val commentProfileImage = if (!commentResponse.profileImage.isNullOrEmpty()) {
                    try {
                        val bitmap = processBase64Image(commentResponse.profileImage, commentResponse.memberId)
                        Log.d("PostRepository", "評論者 ${commentResponse.memberId} 的頭像處理${if (bitmap != null) "成功" else "失敗"}")
                        bitmap
                    } catch (e: Exception) {
                        Log.e("PostRepository", "處理評論者頭像時發生錯誤", e)
                        null
                    }
                } else {
                    Log.d("PostRepository", "評論者 ${commentResponse.memberId} 沒有頭像數據")
                    null
                }

                Comment(
                    id = commentResponse.messageId,
                    commenter = Commenter(
                        id = commentResponse.memberId,
                        name = commentResponse.memberNickname,
                        avatarBitmap = commentProfileImage
                    ),
                    content = commentResponse.content,
                    timestamp = commentResponse.messageTime
                )
            }

            // Update the post with loaded comments
            _postList.update { currentPosts ->
                currentPosts.map { post ->
                    if (post.postId == postId) {
                        post.copy(comments = comments)
                    } else {
                        post
                    }
                }
            }

            Log.d("PostRepository", "成功載入 ${comments.size} 條評論")
        } catch (e: Exception) {
            Log.e("PostRepository", "載入評論時發生錯誤", e)
        }
    }

    private inner class ImageProcessor {
        suspend fun processBase64Image(base64String: String?, id: Int): ImageBitmap? {
            if (base64String.isNullOrEmpty()) {
                Log.d("ImageProcessor", "ID: $id 的圖片數據為空")
                return null
            }

            return withContext(Dispatchers.IO) {
                try {
                    // 處理 Base64 圖片
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Log.d("ImageProcessor", "ID: $id 的圖片處理成功")
                    bitmap?.asImageBitmap()
                } catch (e: Exception) {
                    Log.e("ImageProcessor", "處理 ID: $id 的圖片時發生錯誤", e)
                    null
                }
            }
        }
    }

    private val imageProcessor = ImageProcessor()
    private suspend fun updatePostComments(postId: Int) {
        try {
            val commentResponses = fetchComments(postId)
            val comments = commentResponses.map { commentResponse ->
                Log.d("PostRepository", """
                處理評論:
                Comment ID: ${commentResponse.messageId}
                Commenter ID: ${commentResponse.memberId}
                Commenter Name: ${commentResponse.memberNickname}
                Profile Image 是否存在: ${!commentResponse.profileImage.isNullOrEmpty()}
                """.trimIndent())

                val commentProfileImage = if (!commentResponse.profileImage.isNullOrEmpty()) {
                    imageProcessor.processBase64Image(
                        commentResponse.profileImage,
                        commentResponse.memberId
                    )
                } else {
                    Log.d("PostRepository", "評論者 ${commentResponse.memberId} 沒有頭像數據")
                    null
                }

                Comment(
                    id = commentResponse.messageId,
                    commenter = Commenter(
                        id = commentResponse.memberId,
                        name = commentResponse.memberNickname,
                        avatarBitmap = commentProfileImage
                    ),
                    content = commentResponse.content,
                    timestamp = commentResponse.messageTime
                )
            }

            _postList.update { currentPosts ->
                currentPosts.map { post ->
                    if (post.postId == postId) {
                        post.copy(comments = comments)
                    } else {
                        post
                    }
                }
            }

            Log.d("PostRepository", "成功處理 ${comments.size} 條評論")
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating post comments", e)
        }
    }

    suspend fun createPost(postData: PostCreateData): Boolean {
        val url = "$serverUrl/post/create"

        return try {
            val json = gson.toJson(postData)
            Log.d("setPostCreateData", "Data: $json")
            val result = CommonPost(url, json)
            loadPosts()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error creating post", e)
            false
        }
    }
    suspend fun createComment(postId: Int, userId: Int, content: String): Boolean {
        val url = "$serverUrl/comment/create"
        val commentRequest = mapOf(
            "postId" to postId,
            "memberId" to userId,
            "content" to content
        )
        return try {
            val json = gson.toJson(commentRequest)
            val result = CommonPost(url, json)

            // 創建評論後只更新該貼文的評論
            updatePostComments(postId)
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error creating comment", e)
            false
        }
    }

    // 載入所有貼文
    suspend fun loadPosts() {
        try {
            val postResponses = fetchPosts()
            val posts = postResponses.mapNotNull { response ->
                try {
                    response.toPost()
                } catch (e: Exception) {
                    null
                }
            }
            _postList.update { posts }

        } catch (e: Exception) {
            Log.e("PostRepository", "Error loading posts", e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PostRepository? = null

        fun getInstance(): PostRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PostRepository().also { INSTANCE = it }
            }
        }
    }


    suspend fun deletePost(postId: Int): Boolean {
        val url = "$serverUrl/post/delete"

        return try {
            val requestMap = mapOf("postId" to postId)
            val jsonString = gson.toJson(requestMap)

            val result = CommonPost(url, jsonString)
            val response = gson.fromJson(result, DeleteResponse::class.java)
            if (response.success) {
                true
            } else {
                Log.e("DeletePost", "Delete failed: ${response.message}")
                false
            }
        } catch (e: Exception) {
            Log.e("DeletePost", "Error deleting post", e)
            false
        }
    }
    suspend fun getPost(postId: Int): Post? {
        val url = "$serverUrl/post/get/$postId"
        return try {
            val result = CommonPost(url, "")
            val response = gson.fromJson(result, PostResponse::class.java)
            response.toPost()
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching post $postId", e)
            null
        }
    }

    suspend fun updatePost(postId: Int, postData: PostCreateData): Boolean {
        val url = "$serverUrl/post/update"

        return try {
            Log.d("UpdatePost", "開始更新貼文流程 - ID: $postId")
            Log.d("UpdatePost", "更新內容 - " +
                    "Content: ${postData.content}, " +
                    "Tag: ${postData.postTag}, " +
                    "Photos: ${postData.photos.size}")

            val updateRequest = mapOf(
                "postId" to postId,
                "publisher" to postData.publisher,
                "content" to postData.content,
                "postTag" to postData.postTag,
                "restaurantId" to postData.restaurantId,
                "photos" to postData.photos.map { photo ->
                    mapOf(
                        "imgBase64Str" to photo.imgBase64Str
                    )
                }
            )

            val json = gson.toJson(updateRequest)
            Log.d("UpdatePost", "發送更新請求")

            val result = CommonPost(url, json)
            Log.d("UpdatePost", "收到伺服器回應: $result")

            if (result.contains("貼文更新成功")) {
                // 成功的情況
                Log.d("UpdatePost", "更新成功: $result")
                loadPosts() // 重新載入貼文列表
                true  // 返回 true 表示成功
            } else {
                // 失敗的情況
                Log.e("UpdatePost", "更新失敗，回應: $result")
                false
            }
        } catch (e: Exception) {
            Log.e("UpdatePost", "更新過程發生錯誤: ${e.message}")
            e.printStackTrace()
            false
        }
    }



    suspend fun loadRestRelatedPosts(restId: Int) {
        try {
            val restRelatedPostResponses = selectPostByRestId(restId)
            val posts = restRelatedPostResponses.mapNotNull{ response ->
                try {
                    response.toRestRelatedPost()
                } catch (e: Exception) {
                    null
                }
            }
            _restRelatedPost.update { posts }

        } catch (e: Exception) {
            Log.e("PostRepository", "Error loading posts", e)
        }
    }





    suspend fun selectPostByRestId(restId: Int): List<RestRelatedPostResponse> {
        val url = "$serverUrl/post/RestRelatedPost"
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.addProperty("restaurantId", restId)

        val result = CommonPost(url, jsonObject.toString())
        val type = object : TypeToken<List<RestRelatedPostResponse>>() {}.type
        return gson.fromJson(result, type)

    }

    private suspend fun RestRelatedPostResponse.toRestRelatedPost(): Post {
        // 圖片處理
        val carouselItems = photos?.mapNotNull { photo ->
            Log.d("RestRelatedPostResponse", "處理貼文照片 ID: ${photo.postPhotoId}")
            val imageBitmap = processBase64Image(photo.photoFile, photo.postPhotoId)
            imageBitmap?.let {
                Log.d("RestRelatedPostResponse", "成功處理貼文照片 ID: ${photo.postPhotoId}")
                CarouselItem(
                    id = photo.postPhotoId,
                    imageData = it,
                    order = 0
                )
            } ?: run {
                Log.e("RestRelatedPostResponse", "無法處理貼文照片 ID: ${photo.postPhotoId}")
                null
            }
        } ?: emptyList()

        Log.d("RestRelatedPostResponse", "成功處理 ${carouselItems.size} 張貼文照片")

        val publisherProfileBitmap = if (!publisherProfileImage.isNullOrEmpty()) {
            try {
                val bitmap = processBase64Image(publisherProfileImage, publisher)
                Log.d("PostRepository", "發布者頭像處理${if (bitmap != null) "成功" else "失敗"}")
                bitmap
            } catch (e: Exception) {
                Log.e("PostRepository", "處理發布者頭像時發生錯誤", e)
                null
            }
        } else {
            Log.d("PostRepository", "發布者沒有頭像數據")
            null
        }
        // 創建 RestRelatedPost 對象
        val restRelatedPost = Post(
            postId = this.postId,
            publisher = Publisher(
                id = this.postId, // 這裡假設發布者 ID 與 postId 相同，如有不同需調整
                name = "Unknown User", // 這裡沒有提供發布者名稱，使用預設值
                avatarBitmap = publisherProfileBitmap
            ),
            content = this.content,
            location = "Unknown Location", // 這裡沒有提供位置，使用預設值
            timestamp = System.currentTimeMillis().toString(), // 假設當前時間為時間戳，如有不同需調整
            postTag = "", // 這裡沒有提供標籤，使用空字符串
            carouselItems = carouselItems,
            comments = emptyList(), // 沒有評論信息，使用空列表
            isFavorited = false
        )

        Log.d("PostRepository", """
        貼文處理完成:
        Post ID: ${restRelatedPost.postId}
        Publisher: ${restRelatedPost.publisher.name} (ID: ${restRelatedPost.publisher.id})
        Has Publisher Avatar: ${restRelatedPost.publisher.avatarBitmap != null}
        Photos Count: ${restRelatedPost.carouselItems.size}
        Comments Count: ${restRelatedPost.comments.size}
    """.trimIndent())

        return restRelatedPost
    }

}


// 數據類別
data class PostResponse(
    val postId: Int,
    val postTag: String,
    val publisher: Int,
    val content: String,
    val postTime: String,
    val visibility: Int,
    val restaurantId: Int,
    val likeCount: Int,
    val publisherNickname: String?,
    val restaurantName: String?,
    val photos: List<PhotoResponse>? = null,
    val publisherProfileImage: String? = null
)


data class PhotoResponse(
    val postPhotoId: Int,
    val postId: Int,
    val photoFile: String? // 用於Base64圖片數據
)

// 圖片數據類別
data class CarouselItem(
    val id: Int,
    val imageData: ImageBitmap?, // 改為可為空的 ImageBitmap
    val order: Int,
    val contentDescription: String = "" // 可選的內容描述
)



data class DeleteResponse(
    val success: Boolean,
    val message: String
)

data class CommentResponse(
    val messageId: Int,
    val postId: Int,
    val memberId: Int,
    val content: String,
    val messageTime: String,
    val memberNickname: String,
    val profileImage: String? = null
)



data class UpdateResponse(
    val success: Boolean,
    val message: String
)


data class RestRelatedPostResponse(
    val postId: Int,
    val content: String,
    val publisher: Int,
    val profileImage: String? = null,
    val photos: List<PhotoResponse>? = null,
    val publisherProfileImage: String? = null
)