package com.tibame.foodhunter.ai_ying

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor

@Composable
fun GroupSearchResult(
    navController: NavHostController,
    groupVM: GroupViewModel,
    onBackClick: () -> Unit = {},
    onJoinClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        groupVM.getGroupSearchResult()
    }
    val result by groupVM.groupSearchResult.collectAsState()
    var showGroupChatDetail by remember { mutableStateOf(false) }
    val selectSearchResult by groupVM.selectSearchResult.collectAsState()
    val groupChats by groupVM.groupChat.collectAsState()
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
        if (!showGroupChatDetail) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(1) {
                        GroupTitleText(text = "搜尋結果")
                    }
                    items(result.filterNot { result ->
                        groupChats.any { result.id == it.id }
                    }) {
                        Row(
                            modifier = Modifier
                                .height(56.dp)
                                .padding(start = 10.dp, end = 10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    showGroupChatDetail = true
                                    groupVM.updateSelectSearchResult(it)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    /*Image(
                                    painter = painterResource(id = R.drawable.user1),
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )*/
                                    Text(
                                        text = it.name
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = ""
                            )
                        }
                    }
                    items(1) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "共找到: ${
                                    result.filterNot { result ->
                                        groupChats.any { result.id == it.id }
                                    }.count()
                                } 筆結果"
                            )
                            Button(
                                onClick = onBackClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FColor.Orange_1st
                                )
                            ) {
                                Text("返回")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(1) {
                    GroupTitleText(text = selectSearchResult.name)
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        GroupText(text = stringResource(R.string.str_create_location))
                        GroupTextWithBackground(
                            text = groupVM.getRestaurantNameById(
                                selectSearchResult.location.toInt()
                            )
                        )
                        GroupText(text = stringResource(R.string.str_create_time))
                        GroupTextWithBackground(text = selectSearchResult.time)
                        GroupText(text = stringResource(R.string.str_create_price))
                        GroupTextWithBackground(text = "$${selectSearchResult.priceMin}-$${selectSearchResult.priceMax}")
                        /*GroupText(text = stringResource(R.string.str_create_member))
                GroupTextWithBackground(text = "參加人數:1")*/
                        GroupText(text = stringResource(R.string.str_create_describe))
                        GroupTextWithBackground(text = selectSearchResult.describe)
                        Spacer(modifier = Modifier.size(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        groupVM.joinGroup(
                                            "${selectSearchResult.id}",
                                            groupVM.getUserName()
                                        )
                                        onJoinClick()
                                        showGroupChatDetail = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FColor.Orange_1st
                                    )
                                ) {
                                    Text("加入")
                                }
                                Button(
                                    onClick = {
                                        showGroupChatDetail = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FColor.Orange_5th//MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Text(
                                        text = "返回",
                                        color = Color.Black//MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GroupSearchResultPreview() {
    MaterialTheme {
        GroupSearchResult(rememberNavController(), viewModel())
    }
}