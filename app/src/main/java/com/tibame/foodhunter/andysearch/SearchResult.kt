package com.tibame.foodhunter.andysearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng

@Composable
fun SearchResult(
    navController: NavHostController,
    restaurantID: String = ""
){
    val context = (LocalContext.current)
    val cities = parseCityJson(context, "taiwan_districts.json")
    val restaurants = parseRestaurantJson(context, "restaurants.json")
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    Column(modifier = Modifier.fillMaxSize()){
        ShowSearchBar(cities)
        ShowGoogleMap(
            restaurants = restaurants,
            restaurantID = restaurantID,
            onLocationUpdate = {location -> currentLocation = location})
        ShowRestaurantLists(restaurants, false, navController, currentLocation)
    }
}


