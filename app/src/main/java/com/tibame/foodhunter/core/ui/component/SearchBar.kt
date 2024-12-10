package com.tibame.foodhunter.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor

@Preview(showBackground = true)
@Composable
fun PreviewSearch() {
    SearchBarExample()
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: @Composable () -> Unit = { Text("搜尋") },
    onSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .background(
                color = if (query.isNotEmpty()) Color.White else FColor.Gary_20,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (query.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                shape = RoundedCornerShape(12.dp)
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.str_search),
                    modifier = Modifier.size(20.dp),
                    tint = FColor.Dark_80
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    if (query.isEmpty()) { placeholder() }
                    innerTextField()
                }

                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                color = FColor.Orange_6th,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { onQueryChange("") },
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.str_clear),
                                tint = FColor.Dark_80,
                            )
                        }
                    }
                }
            }
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            color = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search // 將 IME 動作設置為搜尋
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch() // 當按下「搜索」鍵時觸發 onSearch
                // 收起鍵盤
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    )
}

// 使用示例
@Composable
fun SearchBarExample() {
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
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}