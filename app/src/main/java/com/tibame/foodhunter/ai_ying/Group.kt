package com.tibame.foodhunter.ai_ying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor

@Composable
fun GroupMain(
    navController: NavHostController,
    groupVM: GroupViewModel
) {
    val context = LocalContext.current
    var selectTabIndex by remember { mutableIntStateOf(0) }
    var isShowSearchResult by remember { mutableStateOf(false) }
    //val groupChats by groupVM.groupChat.collectAsState()
    LaunchedEffect(Unit) {
        groupVM.getGroupChatList(groupVM.getUserName())
        //groupVM.getTokenSendServer()
    }
    val isLoading by groupVM.isLoading.collectAsState()
    if (isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = FColor.Orange_1st
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "載入中...",
                style = MaterialTheme.typography.bodyMedium,
                color = FColor.Orange_1st
            )
        }
    }
    else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GroupTopTabs(selectTabIndex,
                onTabClick1 = {
                    selectTabIndex = 0
                    isShowSearchResult = false
                },
                onTabClick2 = {
                    selectTabIndex = 1
                })
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectTabIndex) {
                    0 -> {
                        GroupChatList(navController, groupVM)//groupChats, navController, groupVM)
                    }

                    1 -> {
                        if (!isShowSearchResult) {
                            GroupSearch(navController, groupVM) {
                                isShowSearchResult = true
                            }
                        } else {
                            GroupSearchResult(
                                navController,
                                groupVM,
                                onBackClick = {
                                    isShowSearchResult = false
                                },
                                onJoinClick = {
                                    selectTabIndex = 0
                                }
                            )
                        }
                    }
                }
            }
        }
        if (selectTabIndex == 0) {
            GroupCreateButton {
                navController.navigate(context.getString(R.string.str_create_group))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GroupMainPreview() {
    MaterialTheme {
        GroupMain(rememberNavController(), viewModel())
    }
}