import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.core.ui.component.SearchBar
import com.tibame.foodhunter.core.ui.theme.FColor
import com.tibame.foodhunter.zoe.CarouselItem
import com.tibame.foodhunter.zoe.ImageDisplay
import com.tibame.foodhunter.zoe.ImageSource
import com.tibame.foodhunter.zoe.PostEditorViewModel
import com.tibame.foodhunter.zoe.PostViewModel
import com.tibame.foodhunter.zoe.RestaurantList
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")



enum class NewPostSheetContent {
    NONE,
    TAGS,
    LOCATION
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPost(
    navController: NavHostController,
    postViewModel: PostViewModel = viewModel(),
    searchScreenVM: SearchScreenVM = viewModel(),
    postEditorViewModel: PostEditorViewModel = viewModel(),
    userVM: UserViewModel,
    postId: Int? = null
) {
    // State declarations
    val choiceRest by searchScreenVM.choiceOneRest.collectAsState()
    var selectedTag by remember { mutableStateOf("") }
    val selectedLocation by postEditorViewModel.selectedLocation.collectAsState()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var currentSheet by remember { mutableStateOf(NewPostSheetContent.NONE) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedCarouselItems by remember { mutableStateOf<List<CarouselItem>>(emptyList()) }
    var isEditing by remember { mutableStateOf(false) }
    // Available tags
    val availableTags = remember {
        listOf("早午餐", "健康餐", "韓式", "日式", "義式", "美式", "中式",  "下午茶", "甜點", "素食")
    }



    LaunchedEffect(choiceRest) {
        choiceRest?.let { restaurant ->
            Log.d("LocationSelection", "更新貼文位置: restaurantId = ${restaurant.restaurant_id}, name = ${restaurant.name}")
            postEditorViewModel.updateLocation(restaurant.restaurant_id, restaurant.name)
        }
    }

    // User and post data
    val membernameState = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val post = if (postId != null) {
        postViewModel.getPostById(postId).collectAsState().value
    } else {
        null
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris: List<Uri> ->
            selectedImageUris = uris
            // 清除旧的 CarouselItems
            selectedCarouselItems = emptyList()
            // 更新 ViewModel
            postEditorViewModel.updatePhotos(context, uris)
            // 如果在编辑模式，切换回普通模式显示新选择的图片
            isEditing = false
        }
    )


    // Effects
    LaunchedEffect(postId) {
        postEditorViewModel.initializeEditor(postId)
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = userVM.getUserInfo(userVM.username.value)
            if (user != null) {
                membernameState.value = user.id
            }
        }
    }

    LaunchedEffect(post) {
        post?.let {
            isEditing = true
            text = TextFieldValue(it.content)
            selectedTag = it.postTag
            selectedCarouselItems = it.carouselItems
            postEditorViewModel.apply {
                updateInputData(content = it.content)
                updateTags(setOf(it.postTag))
                updateLocation(0, it.location)
            }
        }
    }



    // Bottom sheet content
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                currentSheet = NewPostSheetContent.NONE
            },
            containerColor = Color.White,
            sheetMaxWidth = Dp.Unspecified,
            windowInsets = WindowInsets(0)
        ) {
            when (currentSheet) {
                NewPostSheetContent.TAGS -> TagSelectionSheet(
                    availableTags = availableTags,
                    selectedTags = selectedTag,
                    onFilterChange = { newTag ->
                        selectedTag = newTag
                        postEditorViewModel.updateTags(setOf(newTag))
                    },
                    onConfirm = { showBottomSheet = false }
                )
                NewPostSheetContent.LOCATION -> LocationSelectionSheet(
                    onLocationSelected = { restaurantId ->
                        showBottomSheet = false
                    },
                    testVM = searchScreenVM
                )
                NewPostSheetContent.NONE -> {}
            }
        }
    }

    // Main content
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // Image display area
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
                    .padding(16.dp)
            ) {
                when {
                    selectedImageUris.isNotEmpty() -> {
                        ImageDisplay(
                            imageSource = ImageSource.UriSource(selectedImageUris),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    selectedCarouselItems.isNotEmpty() -> {
                        ImageDisplay(
                            imageSource = ImageSource.CarouselSource(selectedCarouselItems),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Input area
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // Content TextField
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        postEditorViewModel.updateInputData(content = newText.text)
                    },
                    label = { Text("貼文內容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.LightGray
                    )
                )

                // Image selection button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (isEditing) Color.Gray else Color.Black
                    ),
                    onClick = {
                        if (!isEditing) {
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    },
                    enabled = !isEditing
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.photo),
                            contentDescription = "photo",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = if (isEditing)
                                "編輯模式下無法更改圖片"
                            else
                                stringResource(id = R.string.select_picture)
                        )
                    }
                }

                // Location selection button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (isEditing) Color.Gray else Color.Black
                    ),
                    onClick = {
                        if (!isEditing) {
                            currentSheet = NewPostSheetContent.LOCATION
                            showBottomSheet = true
                        }
                    },
                    enabled = !isEditing
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = "location",
                            tint = if (isEditing) Color.Gray else Color.Black
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = if (selectedLocation.isEmpty()) {
                                if (isEditing) {
                                    post?.location ?: stringResource(id = R.string.restaurant_location)
                                } else {
                                    stringResource(id = R.string.restaurant_location)
                                }
                            } else selectedLocation
                        )
                    }
                }

                // Tag selection button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    onClick = {
                        currentSheet = NewPostSheetContent.TAGS
                        showBottomSheet = true
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_local_offer_24),
                            contentDescription = "Select Tag",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = if (selectedTag.isEmpty())
                            stringResource(id = R.string.Select_tag)
                        else selectedTag
                        )
                    }
                }
                CapsuleButton(
                    text = stringResource(id = R.string.str_post),
                    onClick = {
                        postEditorViewModel.updatePublisher(membernameState.value)
                        postEditorViewModel.submitPost(context)
                        navController.navigate(context.getString(R.string.str_Recommended_posts))
                    },
                     modifier = Modifier.fillMaxWidth(0.8f)
                )


            }
        }
    }
}
@Composable
fun LocationSelectionSheet(
    onLocationSelected: (Int) -> Unit,
    testVM: SearchScreenVM
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "選擇餐廳位置",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val testRestaurant by testVM.selectRestList.collectAsState()
        Log.d("LocationSelection", "載入餐廳列表: ${testRestaurant.size} 筆資料")

        val scope = rememberCoroutineScope()
        var searchQuery by remember { mutableStateOf("") }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = {
                Text(
                    "搜尋",
                    color = FColor.Gary,
                    fontSize = 16.sp
                )
            },
            onSearch = {
                scope.launch {
                    Log.d("LocationSelection", "執行搜尋: query = $searchQuery")
                    testVM.updateSearchRest(searchQuery)
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        RestaurantList(
            restaurants = testRestaurant,
            onRestaurantSelected = { restaurantId ->
                testRestaurant.find { it.restaurant_id == restaurantId }?.let { restaurant ->
                    Log.d("LocationSelection", "選擇餐廳: id = $restaurantId, name = ${restaurant.name}")
                    scope.launch {
                        testVM.updateChoiceOneRest(restaurant)
                        Log.d("LocationSelection", "更新已選擇餐廳完成")
                    }
                }
                onLocationSelected(restaurantId)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelectionSheet(
    availableTags: List<String>,
    selectedTags: String,    // 改為單個字串
    onFilterChange: (String) -> Unit,  // 改為回傳單個字串
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.Select_tag))
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableTags.forEach { tag ->
                val isSelected = tag == selectedTags  // 改為直接比較字串

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onFilterChange(tag)  // 直接傳遞選中的標籤
                    },
                    label = { Text(tag) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = colorResource(R.color.orange_3rd)
                    ),
                    border = if (isSelected) {
                        BorderStroke(2.dp, colorResource(R.color.orange_5th))
                    } else {
                        BorderStroke(2.dp, Color.LightGray)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                colorResource(id = R.color.orange_1st)
            )
        ) {
            Text(text = "確定", color = Color.White)
        }
    }

}
@Composable
fun CapsuleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = FColor.Orange_1st,
    enabled: Boolean = true,
    shadowElevation: Dp = 4.dp,
    shadowColor: Color = FColor.Orange_3rd
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(50), // 使用百分比而不是dp
                spotColor = shadowColor,
                ambientColor = shadowColor,
                clip = true
            )
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                disabledContainerColor = containerColor.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(50) // 使用百分比而不是dp
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}