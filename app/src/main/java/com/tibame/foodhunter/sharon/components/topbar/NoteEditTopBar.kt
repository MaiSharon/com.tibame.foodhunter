package com.tibame.foodhunter.sharon.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.components.DeleteConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditTopBar(
    canback:Boolean = true,
    navController: NavHostController? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    ),
    hasTitleInput: Boolean = false,
){
    var showDeleteDialog by remember { mutableStateOf(false) }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
//            Text(stringResource(R.string.str_member))
        },
        navigationIcon = {
            if (canback) {
                IconButton(onClick = {
                    navController?.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.str_back)
                    )
                }
            }
        },
        actions = {
            if (!hasTitleInput) {
                IconButton(onClick = {}) {
                    Icon(
                        modifier = Modifier.background(colorResource(R.color.orange_1st)),
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.str_note_cancel_add)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        showDeleteDialog = true
                    }  //TODO 刪除警告vm
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.str_notice)
                    )
                }
//                IconButton(onClick = {}) {
//                    Icon(
//                        Icons.Filled.Share,
//                        contentDescription = stringResource(R.string.str_chat)
//                    )
//                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
    // 如果顯示刪除對話框
    if (showDeleteDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // 半透明黑色背景
        ) {
            DeleteConfirmationDialog(
                onDeleteConfirmed = {
                    // 點擊確定刪除後的操作
                    showDeleteDialog = false
                    // TODO: 在這裡添加刪除邏輯
                },
                onCancel = {
                    // 點擊取消後隱藏對話框
                    showDeleteDialog = false
                }
            )
        }
    }

}
