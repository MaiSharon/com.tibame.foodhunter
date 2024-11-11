package com.tibame.foodhunter.andysearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng

@Composable
fun SearchResult(
    navController: NavHostController,
    searchTextVM: SearchScreenVM
) {

    val context = (LocalContext.current)
    val cities = parseCityJson(context, "taiwan_districts.json")
    val selectRest by searchTextVM.selectRestList.collectAsState()
    val choiceRest by searchTextVM.choiceOneRest.collectAsState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            ShowSearchBar(
                cities = cities,
                searchTextVM = searchTextVM,
                navController = navController
            )

            ShowGoogleMap(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                restaurants = selectRest,
                restaurantVM = searchTextVM,
                onLocationUpdate = { location -> currentLocation = location })
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {

            if (choiceRest != null) {
                ShowRestaurantLists(
                    restaurants = listOf(choiceRest!!),
                    state = false,
                    navController = navController,
                    currentLocation = currentLocation,
                    searchTextVM = searchTextVM
                )
            } else {
                ShowRestaurantLists(
                    restaurants = selectRest,
                    state = false,
                    navController = navController,
                    currentLocation = currentLocation,
                    searchTextVM = searchTextVM
                )
            }
        }
    }

}


