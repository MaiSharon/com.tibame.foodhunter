@file:OptIn(ExperimentalMaterial3Api::class)

package com.tibame.foodhunter.core.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor
import com.tibame.foodhunter.core.ui.theme.titleFamily

@Composable
fun TopFunctionBar(
    canback: Boolean = false,
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val context = LocalContext.current
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                stringResource(R.string.app_name),
                fontFamily = titleFamily
            )
        },
        navigationIcon = {
            if (canback) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.str_back),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            /*IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.str_notice),
                    tint = Color.White
                )
            }*/
            IconButton(onClick = {navController.navigate(context.getString(R.string.str_member)+"/8")}) {
                Icon(
                    Icons.Filled.MailOutline,
                    contentDescription = stringResource(R.string.str_chat),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FColor.Orange_1st,//MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = Color.White//MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
fun BottomFunctionBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPostClick: () -> Unit,
    onGroupClick: () -> Unit,
    onMemberClick: () -> Unit,
    selectScene: String
) {
    NavigationBar(
//        contentColor = FColor.Orange_3rd,
        containerColor = FColor.Orange_6th
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.str_Recommended_posts)
                )
            },
            label = {
                Text(stringResource(id = R.string.str_home))
            },
            selected = selectScene == stringResource(id = R.string.str_Recommended_posts),
            onClick = {
                onHomeClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor= Color.White,
                indicatorColor= FColor.Orange_3rd
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.str_search)
                )
            },
            label = {
                Text(stringResource(id = R.string.str_search))
            },
            selected = selectScene == stringResource(id = R.string.str_search),
            onClick = {
                onSearchClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor= Color.White,
                indicatorColor= FColor.Orange_3rd
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Outlined.AddCircle,
                    contentDescription = stringResource(id = R.string.str_post)
                )
            },
            label = {
                Text(stringResource(id = R.string.str_post))
            },
            selected = selectScene == stringResource(id = R.string.str_post),
            onClick = {
                onPostClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor= Color.White,
                indicatorColor= FColor.Orange_3rd
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = stringResource(id = R.string.str_group)
                )
            },
            label = {
                Text(stringResource(id = R.string.str_group))
            },
            selected = selectScene == stringResource(id = R.string.str_group),
            onClick = {
                onGroupClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor= Color.White,
                indicatorColor= FColor.Orange_3rd
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = stringResource(id = R.string.str_member)
                )
            },
            label = {
                Text(stringResource(id = R.string.str_member))
            },
            selected = selectScene == stringResource(id = R.string.str_member),
            onClick = {
                onMemberClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor= Color.White,
                indicatorColor= FColor.Orange_3rd
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FoodHunterPreview() {
    MaterialTheme {
//        Main()
        BottomFunctionBar({},{},{},{},{},stringResource(id = R.string.str_post))
    }
}