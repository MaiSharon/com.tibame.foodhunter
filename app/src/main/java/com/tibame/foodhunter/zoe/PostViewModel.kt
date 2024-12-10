package com.tibame.foodhunter.zoe

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class PostViewModel : ViewModel() {
    private val repository = PostRepository.getInstance()

    // 基本狀態
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _selectedFilters = MutableStateFlow<List<String>>(emptyList())
    val selectedFilters = _selectedFilters.asStateFlow()
    private val _selectedPostId = MutableStateFlow<Int?>(null)
    val selectedPostId = _selectedPostId.asStateFlow()

    // 評論相關狀態
    private val _commentLoadingStates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val commentLoadingStates = _commentLoadingStates.asStateFlow()
    private val _commentLoadingError = MutableStateFlow<Map<Int, String>>(emptyMap())
    val commentLoadingError = _commentLoadingError.asStateFlow()

    private val personalPostsCache = mutableMapOf<Int, StateFlow<List<Post>>>()
    private val _carouselItems = MutableStateFlow<List<CarouselItem>>(emptyList())
    val carouselItems: StateFlow<List<CarouselItem>> = _carouselItems.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.loadPosts()
        }
    }

    // 更新的評論載入方法
    fun loadCommentsForPost(postId: Int) {
        viewModelScope.launch {
            try {
                // 設置載入狀態
                _commentLoadingStates.update { it + (postId to true) }
                _commentLoadingError.update { it - postId }

                // 載入評論
                repository.loadCommentsForPost(postId)

                // 清除載入狀態
                _commentLoadingStates.update { it - postId }
            } catch (e: Exception) {
                Log.e("PostViewModel", "載入評論失敗: postId=$postId", e)
                _commentLoadingStates.update { it - postId }

            }
        }
    }

    // 更新後的評論創建方法
    fun createComment(postId: Int, userId: Int, content: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.createComment(postId, userId, content)
                if (!success) {
                    Log.e("PostViewModel", "評論創建失敗")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "評論創建時發生錯誤", e)
            } finally {
                _isLoading.value = false
            }
        }
    }



    // 更新的 getPostById 方法
    fun getPostById(postId: Int): StateFlow<Post?> {
        // 自動觸發評論載入
        loadCommentsForPost(postId)

        return repository.postList
            .map { posts -> posts.find { it.postId == postId } }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }



    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deletePost(context: Context, postId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.deletePost(postId)
                if (success) {
                    repository.loadPosts()
                }
                Toast.makeText(context, "貼文已刪除", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "刪除失敗", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getPersonalPosts(publisherId: Int): StateFlow<List<Post>> {
        return personalPostsCache.getOrPut(publisherId) {
            repository.postList
                .map { posts ->
                    posts.filter { post ->
                        post.publisher.id == publisherId
                    }.sortedByDescending {
                        it.timestamp
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = emptyList()
                )
        }
    }

    fun setPostId(postId: Int) {
        _selectedPostId.value = postId
    }

    fun getFilteredPosts(): StateFlow<List<Post>> {
        return combine(
            repository.postList,
            selectedFilters,
            searchQuery
        ) { posts, filters, query ->
            var filteredList = posts

            if (filters.isNotEmpty()) {
                filteredList = filteredList.filter { post ->
                    filters.contains(post.postTag)
                }
            }

            if (query.isNotEmpty()) {
                filteredList = filteredList.filter { post ->
                    post.content.contains(query, ignoreCase = true) ||
                            post.location.contains(query, ignoreCase = true) ||
                            post.publisher.name.contains(query, ignoreCase = true)
                }
            }

            filteredList.sortedByDescending { it.timestamp }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun updateFilters(filters: List<String>) {
        _selectedFilters.value = filters
    }


    val restRelatedPosts: StateFlow<List<Post>> = repository.restRelatedPost
    fun fetchRestRelatedPosts(restId: Int) {
        viewModelScope.launch {
            repository.loadRestRelatedPosts(restId)
        }
    }



}