package com.tibame.foodhunter.ai_ying

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.core.ui.theme.FColor

@Composable
fun GroupChatList(
    //groupChats: List<GroupChat>,
    navController: NavHostController,
    gChatVM: GroupViewModel
) {
    var searchInput by remember { mutableStateOf("") }
    val groupChats by gChatVM.groupChat.collectAsState()
    GroupSearchBar(
        onValueChange= {
            searchInput = it
            searchInput
        },
        onClearClick = {
            searchInput = ""
        }
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(groupChats.filter { it.name.contains(searchInput,true) }) { groupChat ->
            if (groupChat.state == 99 && searchInput.isEmpty()) {
                Column(
                    modifier = Modifier
                        .background(FColor.Orange_3rd)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        text = groupChat.name,
                        color = Color.White
                    )
                }
            }
            else if (groupChat.state == 98 && searchInput.isEmpty()) {
                Column(
                    modifier = Modifier
                        .background(FColor.Gary_20)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        text = groupChat.name,
                        color = FColor.Gary
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier.background(Color.White).clickable {
                        gChatVM.setDetailGroupChat(groupChat)
                        navController.navigate("GroupChatRoom/${groupChat.id}")
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ){
                                /*Image(
                                    painter = painterResource(id = R.drawable.user1),
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )*/
                                Text(
                                    text = groupChat.name
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                }
            }
            //Spacer(modifier = Modifier.fillMaxWidth().size(1.dp).background(colorResource(R.color.orange_1st)))
        }

    }
}


@Preview(showBackground = true)
@Composable
fun GroupChatListPreview(groupVM:GroupViewModel= viewModel()) {
    MaterialTheme {
        GroupMain(rememberNavController(), viewModel())
    //val groupChats by groupVM.groupChatFlow.collectAsState()
        //GroupChatList(groupChats, rememberNavController(), viewModel())
    }
}