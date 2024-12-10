package com.tibame.foodhunter.ai_ying

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun GroupCreate(
    navController: NavHostController,
    gChatVM: GroupViewModel
) {
    LaunchedEffect(Unit) {
        gChatVM.getRestaurantList()
    }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showRestaurantPickerDialog by remember { mutableStateOf(false) }
    val inputData by remember { mutableStateOf(GroupCreateData()) }
    var selectDate by remember {
        mutableStateOf(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        )
    }
    var searchInput by remember { mutableStateOf("") }
    val restaurantList by gChatVM.restaurantList.collectAsState()
    var restaurantName by remember { mutableStateOf("") }
    var errMsg by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(1) {
            //*
            GroupTitleText(text = stringResource(R.string.str_create_group))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            {
                GroupText(text = stringResource(R.string.str_create_name))
                GroupSingleInput {
                    inputData.name = it
                }
                GroupText(text = stringResource(R.string.str_create_location))
                GroupSingleInputWithIcon(
                    readOnly = true,
                    placeholder = {
                        Text(restaurantName)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                showRestaurantPickerDialog = true
                            }
                        )
                    }
                ) {}
                /*GroupSingleInput {
                    inputData.location = it
                }*/
                GroupText(text = stringResource(R.string.str_create_time))
                GroupSingleInputWithIcon(
                    readOnly = true,
                    placeholder = {
                        Text(inputData.time)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                showDatePickerDialog = true
                            }
                        )
                    }
                ) {
                    inputData.time = it
                }
                GroupText(text = stringResource(R.string.str_create_price))
                GroupPriceSlider { min, max ->
                    inputData.priceMin = min
                    inputData.priceMax = max
                }
                /*GroupText(text = stringResource(R.string.str_create_member))
                GroupSelectMember {
                    inputData.joinMember = it.toString()
                }
                GroupText(text = stringResource(R.string.str_create_public))
                GroupDropDownMenu(listOf("公開", "邀請", "私人")) {
                    inputData.isPublic = it
                }*/
                GroupText(text = stringResource(R.string.str_create_describe))
                GroupBigInput(5) {
                    inputData.describe = it
                }
                //*/
                Spacer(modifier = Modifier.size(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errMsg.isNotBlank()){
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(errMsg)
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    Button(
                        onClick = {
                            if (inputData.name.isBlank()){
                                errMsg = "揪團名稱不可為空白!"
                                return@Button
                            }
                            if (restaurantName.isBlank()){
                                errMsg = "請選擇餐廳!"
                                return@Button
                            }
                            if (inputData.time.isBlank()){
                                errMsg = "請選擇日期!"
                                return@Button
                            }
                            if (inputData.priceMin==inputData.priceMax){
                                errMsg = "請正確選擇金額範圍!"
                                return@Button
                            }
                            gChatVM.createGroup(inputData)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FColor.Orange_1st
                        )
                    ) {
                        Text("確定")
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
            }

        }
    }
    var saveSelectTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    Box {
        if (showDatePickerDialog) {
            MyDatePickerDialog(
                showData =  saveSelectTime ,
                onDismissRequest = {
                    showDatePickerDialog = false
                },
                // 確定時會接收到選取日期
                onConfirm = { utcTimeMillis ->
                    saveSelectTime = utcTimeMillis?:System.currentTimeMillis()
                    selectDate = utcTimeMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC+8"))
                            .toLocalDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                            ?: selectDate
                        //.toLocalDate().format(ofLocalizedDate(FormatStyle.MEDIUM))
                    }
                    inputData.time = selectDate?:LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                    showDatePickerDialog = false
                },
                // 設定取消時欲執行內容
                onDismiss = {
                    showDatePickerDialog = false
                }
            )
        }
        if (showRestaurantPickerDialog) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(FColor.Orange_3rd)
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            showRestaurantPickerDialog = false
                        }
                    )
                    GroupTitleText(text = "選擇餐廳")
                }
                GroupSearchBar(
                    onValueChange = {
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
                    items(restaurantList.filter { it.restaurantName.contains(searchInput, true) }) {
                        Column(
                            modifier = Modifier
                                .background(Color.White)
                                .clickable {
                                    inputData.location = it.restaurantId
                                    restaurantName = it.restaurantName
                                    searchInput = ""
                                    showRestaurantPickerDialog = false
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
                                    ) {
                                        Text(
                                            text = it.restaurantName
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
}

@Preview(showBackground = true)
@Composable
fun GroupCreatePreview() {
    MaterialTheme {
        GroupCreate(rememberNavController(), viewModel())
//        MyDatePickerDialog(
//            onDismissRequest = {
//            },
//            // 確定時會接收到選取日期
//            onConfirm = {
//            },
//            // 設定取消時欲執行內容
//            onDismiss = {
//            }
//        )
    }
}