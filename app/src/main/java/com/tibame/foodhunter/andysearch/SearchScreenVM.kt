package com.tibame.foodhunter.andysearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenVM: ViewModel() {
    private val _showSearchText = MutableStateFlow("")
    val showSearchText: StateFlow<String> = _showSearchText.asStateFlow()

    // 搜尋文字
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // 預先載入的餐廳
    private val _preRestaurantList = MutableStateFlow(emptyList<Restaurant>())
    val preRestaurantList: StateFlow<List<Restaurant>> = _preRestaurantList.asStateFlow()

    // 找尋餐廳的response
    private val _selectRestList = MutableStateFlow(emptyList<Restaurant>())
    val selectRestList: StateFlow<List<Restaurant>> = _selectRestList.asStateFlow()

    // 選擇單一餐廳
    private val _choiceOneRest = MutableStateFlow<Restaurant?>(null)
    val choiceOneRest: StateFlow<Restaurant?> = _choiceOneRest.asStateFlow()


    // 選擇城市
    private val _cityText = MutableStateFlow("")
    val cityText: StateFlow<String> = _cityText.asStateFlow()

    // 選擇價格
    private val _choicePrice = MutableStateFlow("")
    val choicePrice: StateFlow<String> = _choicePrice.asStateFlow()

    // 其他條件
    private val _otherConditions = MutableStateFlow("")
    val otherConditions: StateFlow<String> = _otherConditions.asStateFlow()

    // 丟給後端的搜尋條件
    private val _finalSearchText = MutableStateFlow("")
    val finalSearchText: StateFlow<String> = _finalSearchText.asStateFlow()

    init {
        // 初始化時設置預設值
        updateCityText("")
        updateChoicePrice("")
        _otherConditions.update{""}
        updateSearchText("")

    }


    fun updateSearchText(newText: String){
        _searchText.update {newText}
    }

    fun loadShowSearchText(){
        val cityPart = _cityText.value
        val pricePart = _choicePrice.value
        val otherPart = _otherConditions.value

        _showSearchText.update {
            listOf(pricePart, cityPart, otherPart)
                .filter { it.isNotBlank() } // 過濾掉空白的條件
                .joinToString(" ")
        }
    }

    fun clearSearchText(){
        updateCityText("")
        updateChoicePrice("")
        _otherConditions.update{""}
        updateSearchText("")
    }

    fun buildSearch(){
        _finalSearchText.update{
            val cityPart = _cityText.value
            val pricePart = _choicePrice.value
            val otherPart = _otherConditions.value
            val inputPart = _searchText.value

            if (pricePart.isNotBlank()){
                val prices = pricePart.split(" ~ ")
                val minPrice = prices[0].trim().toFloatOrNull() ?: 0f // 使用 trim() 去除空格
                val maxPrice = prices[1].trim().toFloatOrNull() ?: 0f
                Log.d("Price", "prices: $prices")
                Log.d("Price", "minPrice: $minPrice")
                Log.d("Price", "maxPrice: $maxPrice")
                // 計算平均價
                val calPrice = (minPrice + maxPrice) / 2
                Log.d("Price", "maxPrice: $calPrice")
                listOf(minPrice.toInt().toString(), maxPrice.toInt().toString(), cityPart, otherPart, inputPart)
                    .filter { it.isNotBlank() } // 過濾掉空白的條件
                    .joinToString(" ")
            } else {
                listOf(cityPart, otherPart, inputPart)
                    .filter { it.isNotBlank() } // 過濾掉空白的條件
                    .joinToString(" ")
            }
        }
    }

    fun updateCityText(newCity: String){
        _cityText.update { newCity }
    }

    fun updateChoicePrice(newPrice: String){
        _choicePrice.update { newPrice }
    }

    fun updateOtherConditions(conditions: List<String>){
        _otherConditions.update{
            conditions.joinToString(" ")
        }
    }

    // 選擇餐廳
    fun updateChoiceOneRest(choiceRest: Restaurant){
//        viewModelScope.launch {
//            _choiceOneRest.update {
//                val id = choiceRest.restaurant_id
//                fetchRestaurantId(id)
//            }
//        }
        _choiceOneRest.update {
            choiceRest
        }

    }

    // 清空單一餐廳
    fun clearChoiceRest(){
        _choiceOneRest.update { null }
    }

    // 預先載入10家餐廳
    fun preloadRestaurants(){
        CoroutineScope(Dispatchers.IO).launch {
            val initialRestaurants = fetchInitRestaurant()
            _preRestaurantList.update { initialRestaurants }
        }
    }

    // 從資料庫中獲取10家餐廳
    private suspend fun fetchInitRestaurant(): List<Restaurant>{
        try {
            val url = "$serverUrl/PreLoadRest"
            val gson = Gson()
            val result = CommonPost(url, "")
            return if (result.contains("NotFind")) {
                // 如果包含 "NotFind"，返回一個空的餐廳列表
                emptyList<Restaurant>()
            } else {
                // 正常解析 JSON 並返回餐廳列表
                val type = object : TypeToken<List<Restaurant>>() {}.type
                gson.fromJson(result, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()  // 打印出錯信息，方便調試
            return emptyList()    // 出錯時返回一個空的 List<Restaurant>
        }
    }

     fun updateSearchRest(searchText: String){
        viewModelScope.launch {
            _selectRestList.update { fetchRestaurant(searchText) }
        }
    }

    // 搜尋餐廳從database
    private suspend fun fetchRestaurant(searchText: String): List<Restaurant>{
        try {
            val url = "$serverUrl/SelectRestController"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("searchText", searchText)
            val result = CommonPost(url, jsonObject.toString())
            Log.d("NotFind", result)
            return if (result.contains("NotFind")) {
                // 如果包含 "NotFind"，返回一個空的餐廳列表
                emptyList<Restaurant>()
            } else {
                // 正常解析 JSON 並返回餐廳列表
                val type = object : TypeToken<List<Restaurant>>() {}.type
                gson.fromJson(result, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()  // 打印出錯信息，方便調試
            return emptyList()    // 出錯時返回一個空的 List<Restaurant>
        }
    }
    private suspend fun fetchRestaurantId(restaurantId: Int): Restaurant? {
        try {
            val url = "$serverUrl/SelectRestByIdController"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("restaurantId", restaurantId)
            val result = CommonPost(url, jsonObject.toString())
            Log.d("NotFind", result)
            return if (result.contains("NotFind")) {
                // 如果包含 "NotFind"，返回一個空的餐廳列表
                null
            } else {
                // 正常解析 JSON 並返回餐廳列表
                val type = object : TypeToken<List<Restaurant>>() {}.type
                gson.fromJson(result, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()  // 打印出錯信息，方便調試
            return null   // 出錯時返回一個空的 List<Restaurant>
        }
    }
}