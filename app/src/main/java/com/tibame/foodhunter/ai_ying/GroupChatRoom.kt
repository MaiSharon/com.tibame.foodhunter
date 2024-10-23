package com.tibame.foodhunter.ai_ying

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun GroupChatRoom(
    navController: NavHostController,
    groupRoomId: Int
) {
    Column() {
        Text(navController.currentBackStackEntryAsState().value?.destination?.route.toString())
        Text("groupRoomId:${groupRoomId}")
    }

}

@Preview(showBackground = true)
@Composable
fun GroupChatRoomPreview() {
    MaterialTheme {
        GroupChatRoom(rememberNavController(),0)
    }
}