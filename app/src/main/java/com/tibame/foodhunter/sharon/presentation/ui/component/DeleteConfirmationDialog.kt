package com.tibame.foodhunter.sharon.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tibame.foodhunter.core.ui.theme.FColor
import com.tibame.foodhunter.core.ui.theme.FTypography

@Preview(showBackground = true)@Composable
fun DeleteConfirmationDialogP() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        DeleteConfirmationDialog(
            onDeleteConfirmed = { showDialog = false },
            onCancel = { showDialog = false }
        )
    }
}


//TODO 排版
@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirmed: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(16.dp)
            .width(288.dp), // 固定寬度
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "確定要刪除？",
            style = FTypography.Title1,
            color = FColor.Dark_80,
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = FColor.Dark_80
                ),
            ) {
                Text(text = "取消", style = FTypography.Label1)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = onDeleteConfirmed,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = FColor.Orange_1st
                ),
            ) {
                Text(text = "刪除", style = FTypography.Label1)
            }
        }
    }
}
