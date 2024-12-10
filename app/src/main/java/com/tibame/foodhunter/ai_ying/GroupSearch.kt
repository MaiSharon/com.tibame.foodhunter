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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
fun GroupSearch(
    navController: NavHostController,
    gChatVM: GroupViewModel,
    onSearchClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        gChatVM.getRestaurantList()
    }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectDate by remember {
        mutableStateOf(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        )
    }
    val inputData by gChatVM.groupSearchCache.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var showRestaurantPickerDialog by remember { mutableStateOf(false) }
    val restaurantList by gChatVM.restaurantList.collectAsState()
    var errMsg by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(1) {
            //*
            GroupTitleText(text = stringResource(R.string.str_search_group))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                GroupText(text = stringResource(R.string.str_create_name))
                GroupSingleInput(defaultInput = inputData.name) {
                    inputData.name = it
                }
                GroupText(text = stringResource(R.string.str_create_location))
                GroupSingleInput(
                    defaultInput = inputData.location,
                    /*placeholder = {
                        Text(inputData.location)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                showRestaurantPickerDialog = true
                            }
                        )
                    }*/
                ) {
                    inputData.location = it
                }
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
                GroupPriceSlider(
                    defaultLow = inputData.priceMin,
                    defaultHigh = inputData.priceMax
                ) { min, max ->
                    inputData.priceMin = min
                    inputData.priceMax = max
                }
                Spacer(modifier = Modifier.size(8.dp))
//                GroupText(text = stringResource(R.string.str_search_tags))
//                FlowRow(
//                    modifier = Modifier
//                        .border(1.dp, Color.LightGray)
//                        .background(Color.White)
//                        .fillMaxSize()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    tags.forEach {//.shuffled().forEach {
//                        FoodLabel(it) {
//                            inputData.tags.toMutableList().add(it)
//                        }
//                    }
//                }
                GroupText(text = stringResource(R.string.str_create_describe))
                GroupBigInput(5) {
                    inputData.describe = it
                }
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (inputData.priceMin==inputData.priceMax){
                                    errMsg = "請正確選擇金額範圍!"
                                    return@Button
                                }
                                gChatVM.searchGroupByCondition(inputData)
                                onSearchClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FColor.Orange_1st
                            )
                        ) {
                            Text("搜尋")
                        }
//                        Button(
//                            onClick = {
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = FColor.Orange_5th
//                            )
//                        ) {
//                            Text(
//                                text = "清空",
//                                color = Color.Black
//                            )
//                        }
                    }
                }

            }

        }
    }
    var saveSelectTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    Box {
        if (showDatePickerDialog) {
            MyDatePickerDialog(
                showData = saveSelectTime,
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
//                            .toLocalDate().format(ofLocalizedDate(FormatStyle.MEDIUM))
                    }
                    showDatePickerDialog = false
                    inputData.time = selectDate?:LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
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
                    .background(Color.Yellow)
            ) {
                GroupTitleText(text = "選擇餐廳")
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
                    items(restaurantList.filter { it.restaurantName.contains(searchInput) }) {
                        Column(
                            modifier = Modifier
                                .background(Color.White)
                                .clickable {
                                    inputData.location = it.restaurantName
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
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLabel(text: String, onClick: () -> Unit) {
    var selected by remember { mutableStateOf(false) }
    FilterChip(
        selected = selected,
        onClick = {
            selected = !selected
            onClick()
        },
        label = { Text(text) },
        leadingIcon = if (selected) {
            {
//                Icon(
//                    imageVector = Icons.Filled.Done,
//                    contentDescription = "Done icon",
//                    modifier = Modifier.size(FilterChipDefaults.IconSize)
//                )
            }
        } else {
            null
        },
        enabled = true,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = FColor.Orange_5th
        )
    )
//    Column(
//        modifier = Modifier.clickable {
//            selected = !selected
//            onClick()
//        }
//    ) {
//        Row(
//            modifier = Modifier
//                .background(
//                    when (selected) {
//                        true -> MaterialTheme.colorScheme.primary//colorResource(R.color.orange_1st)
//                        false -> MaterialTheme.colorScheme.primaryContainer//colorResource(R.color.orange_4th)
//                    },
//                    shape = RoundedCornerShape(8.dp)
//                )
//                .padding(top = 8.dp, end = 16.dp, start = 8.dp, bottom = 8.dp)
//        ) {
//            Icon(
//                when (selected) {
//                    true -> Icons.Outlined.Check
//                    false -> Icons.Outlined.Add
//                },
//                contentDescription = "",
//                tint = when (selected) {
//                    true -> Color.White
//                    false -> Color.Black
//                }
//            )
//            Text(
//                text = text,
//                color = when (selected) {
//                    true -> Color.White
//                    false -> Color.Black
//                }
//            )
//        }
//    }

//    TextField(
//        value = text,
//        onValueChange = {
//        },
//        modifier = Modifier.clipToBounds(),
//        shape = RoundedCornerShape(12.dp),
//        colors = TextFieldDefaults.colors(
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent
//        ),
//        readOnly = true
//    )
}

@Preview(showBackground = true)
@Composable
fun GroupSearchPreview() {
    MaterialTheme {
        GroupSearch(rememberNavController(), viewModel())
//        FoodLabel("aaa",{})
    }
}