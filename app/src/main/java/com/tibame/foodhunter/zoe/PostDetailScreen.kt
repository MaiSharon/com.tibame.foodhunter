package com.tibame.foodhunter.zoe

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.core.ui.theme.FColor
import kotlinx.coroutines.launch

enum class SheetContent {
    NONE,
    MESSAGE,
    EDIT
}
@Composable
fun PostDetailScreen(
    postId: Int?,
    navController: NavHostController,
    postViewModel: PostViewModel,
    userVM: UserViewModel
) {
    val post = postId?.let { postViewModel.getPostById(it).collectAsState().value }
    val memberId by userVM.memberId.collectAsState()


    post?.let { nonNullPost ->
        PostDetail(
            post = nonNullPost,
            viewModel = postViewModel,
            navController = navController,
            memberId = memberId,  // 直接傳遞 memberId

        )
    } ?: Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "找不到相關貼文",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                colorResource(id = R.color.orange_2nd)
            )
        ) {
            Text("返回")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetail(
    post: Post,
    viewModel: PostViewModel,
    navController: NavHostController,
    memberId: Int,  // 改為使用 memberId
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSheet by remember { mutableStateOf(SheetContent.NONE) }

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PostDetailItem(
            post = post,
            memberId = memberId,
            onEditClick = {
                currentSheet = SheetContent.EDIT
                showBottomSheet = true
            },
            onMessageClick = {
                currentSheet = SheetContent.MESSAGE
                showBottomSheet = true
            },
            onUserClick = { publisherId ->
                navController.navigate("person_homepage/$publisherId") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                currentSheet = SheetContent.NONE
            },
            containerColor = Color.White
        ) {
            when (currentSheet) {
                SheetContent.MESSAGE -> MessageSheet(
                    post = post,
                    viewModel = viewModel,
                    memberId = memberId,  // 傳入 memberId
                    onConfirm = { showBottomSheet = false }
                )
                SheetContent.EDIT -> EditSheet(
                    post = post,
                    viewModel = viewModel,
                    navController = navController,
                    onConfirm = { showBottomSheet = false }
                )
                SheetContent.NONE -> { /* No content to display */ }
            }
        }
    }
}

@Composable
fun PostDetailItem(
    post: Post,
    memberId: Int,
    onEditClick: () -> Unit,
    onMessageClick: () -> Unit,
    onUserClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                imageData = post.publisher.avatarBitmap,
                defaultImage = post.publisher.avatarImage,
                onClick = { onUserClick(post.publisher.id) },
                contentDescription = "Avatar for ${post.publisher.name}"
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = post.publisher.name)
                Text(text = post.location)
            }

            if (memberId == post.publisher.id) {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ImageDisplay(imageSource = ImageSource.CarouselSource(post.carouselItems))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FavoriteIcon()

                IconButton(onClick = onMessageClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat_bubble_outline_24),
                        contentDescription = "Chat Bubble",
                        modifier = Modifier.size(22.dp),
                        tint = FColor.Dark_66
                    )
                }
                Text(
                    text = "${post.comments.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FColor.Dark_66
                )
                Spacer(modifier = Modifier.weight(1f)) // 將標籤推到右側

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End // 將標籤的排列方式設置為靠右
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_local_offer_24),
                        contentDescription = "Select Tag",
                        modifier = Modifier.size(22.dp),
                        tint = FColor.Dark_66
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = post.postTag,
                        color = FColor.Dark_66
                    )
                }
            }
        }

        Text(
            text = post.content,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis
        )
    }
}
@Composable
fun MessageSheet(
    post: Post,
    viewModel: PostViewModel,
    onConfirm: () -> Unit,
    memberId: Int  // 新增 memberId 參數
) {
    LaunchedEffect(Unit) {
        Log.d("MessageSheet", """
            MessageSheet received parameters:
            - postId: ${post.postId}
            - memberId: $memberId
            - publisherId: ${post.publisher.id}
        """.trimIndent())
    }
    var commentText by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPost by viewModel.getPostById(post.postId).collectAsState()
    val comments = currentPost?.comments ?: post.comments

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "留言",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(comments) { comment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        imageData = comment.commenter.avatarBitmap,
                        defaultImage = comment.commenter.avatarImage,
                        contentDescription = "Avatar for ${comment.commenter.name}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = comment.commenter.name, fontWeight = FontWeight.SemiBold)
                        Text(text = comment.content)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            enabled = !isLoading,
            placeholder = { Text(text = stringResource(id = R.string.Add_message)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.LightGray,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            Log.d("MessageSheet", "Creating comment: postId=${post.postId}, memberId=$memberId, content=$commentText")
                            viewModel.createComment(
                                postId = post.postId,
                                userId = memberId,  // 直接使用傳入的 memberId
                                content = commentText
                            )
                            commentText = ""
                        }
                    },
                    enabled = !isLoading && commentText.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_send_24),
                            contentDescription = "Send Comment",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
@Composable
fun EditSheet(
    post: Post,
    viewModel: PostViewModel,
    navController: NavHostController,
    onConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.4f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate("${context.getString(R.string.str_post)}/edit/${post.postId}")
                onConfirm()
            },
            colors = ButtonDefaults.buttonColors(
                colorResource(id = R.color.orange_2nd)
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = stringResource(id = R.string.edit)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                colorResource(id = R.color.orange_2nd)
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = stringResource(id = R.string.delete)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "確定要刪除？",
                    color = colorResource(id = R.color.black)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showDialog = false
                            viewModel.deletePost(context, post.postId)
                        }
                        onConfirm()  // 關閉 BottomSheet
                        navController.navigate(context.getString(R.string.str_Recommended_posts))
                    }
                ) {
                    Text(
                        text = "刪除",
                        color = colorResource(id = R.color.orange_1st)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = "取消",
                        color = colorResource(id = R.color.black)
                    )
                }
            },
            containerColor = Color.White
        )
    }
}