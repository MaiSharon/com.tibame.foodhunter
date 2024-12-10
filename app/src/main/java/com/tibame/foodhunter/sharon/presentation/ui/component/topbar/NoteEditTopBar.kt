package com.tibame.foodhunter.sharon.presentation.ui.component.topbar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.presentation.ui.component.DeleteConfirmationDialog
import com.tibame.foodhunter.sharon.presentation.viewmodel.NoteEditVM
import com.tibame.foodhunter.core.ui.theme.FColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditTopBar(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    ),
    noteEditVM: NoteEditVM,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val uiState by noteEditVM.uiState.collectAsState()

    Log.d("NoteEditTopBar", "TopBar UI狀態: $uiState")


    TopAppBar(
        title = {},
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (uiState.hasTitle) {
                IconButton(onClick = {
                    noteEditVM.saveAndNavigateBack(navController)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.str_back)
                    )
                }
            }
        },
        actions = {
            when {
                uiState.isFirstEntry && !uiState.hasTitle ->
                    Box(
                        modifier = Modifier
                            .padding(end = 18.dp)
                            .size(30.dp)
                            .background(
                                color = FColor.Orange_6th,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(FColor.Orange_6th),
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.str_clear),
                                tint = FColor.Dark_80,
                            )
                        }
                    }
                // 不是首次進入、有既有筆記
                uiState.hasTitle || uiState.isExistingNote ->
                    IconButton(
                        modifier = Modifier
                            .padding(end = 18.dp),
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
            }
        }
    )
    // 如果顯示刪除對話框
    if (showDeleteDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // 半透明黑色背景
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center // 確保對話框在中間
            ) {
                DeleteConfirmationDialog(
                    onDeleteConfirmed = {
                        showDeleteDialog = false
                        noteEditVM.deleteNote(navController)
                        navController.popBackStack()
                    },
                    onCancel = {
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}
