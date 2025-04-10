package com.hotelka.knitlyWants.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.Cards.UserInfoCard
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.ui.theme.Tabs
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.textColor
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FansAndFriends() {
    var searched by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    var query by remember { mutableStateOf("") }
    var fans = remember { mutableStateListOf<UserData>() }
    if (fans.isEmpty()) {
        FirebaseDB.getSubscribers {
            fans.add(it)
        }
    }
    var subscriptions = remember { mutableStateListOf<UserData>() }
    if (subscriptions.isEmpty()) {
        FirebaseDB.getSubscriptions {
            if (!fans.contains(it)) subscriptions.add(it)
        }
    }

    var fansAndFriends = remember { mutableStateListOf<UserData>() }
    var fansAndFriendsFiltered = remember { mutableStateListOf<UserData>() }

    if (fansAndFriends.isEmpty()) {
        fansAndFriends.apply {
            addAll(fans)
            addAll(subscriptions)
            distinct()
        }
    }
    fun search() {
        fansAndFriendsFiltered.clear()
        fansAndFriends.forEach { user ->
            if (user.username!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.name!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.lastName!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.bio!!.toLowerCase(Locale.ROOT).contains(query)
            ) {
                fansAndFriendsFiltered.add(user)
            }
        }
    }

    val fansAndFriendsTab = Tabs(
        selectedIcon = ImageVector.vectorResource(R.drawable.friends),
        unselectedIcon = ImageVector.vectorResource(R.drawable.friends),
        text = stringResource(R.string.friends_and_fans)
    )
    val scope = rememberCoroutineScope()
    var state = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            singleLine = true,
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = textColor
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        modifier = Modifier.clickable {
                            query = ""
                            fansAndFriendsFiltered.clear()
                            searched = false
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textColor
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = basic,
                unfocusedContainerColor = basic,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            label = { Text(stringResource(R.string.search)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { search(); keyboard?.hide(); searched = true}
            )
        )
        TabRow(
            selectedTabIndex = 0,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            containerColor = basic,
            contentColor = basic,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    height = 1.dp,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[0]),
                    color = darkBasic
                )

            }
        ) {
            Tab(
                selected = true,
                selectedContentColor = textColor,
                unselectedContentColor = textColor,
                onClick = {
                    scope.launch {
                        state.scrollToItem(0)
                    }
                },
                text = {
                    Row {
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = fansAndFriendsTab.text
                        )
                        Icon(
                            modifier = Modifier.padding(start = 2.dp),
                            imageVector = fansAndFriendsTab.selectedIcon,
                            contentDescription = null
                        )
                    }
                },
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (fansAndFriendsFiltered.isEmpty() && query.isNotEmpty() && searched) {
                item {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            text = stringResource(R.string.noResult),
                            textAlign = TextAlign.Center,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(
                        Modifier.height(2.dp),
                        color = darkBasic
                    )
                }
            } else if (fansAndFriends.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        text = stringResource(R.string.noFriends),
                        textAlign = TextAlign.Center,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(if (fansAndFriendsFiltered.isNotEmpty())
                fansAndFriendsFiltered.sortedByDescending { user -> user.subscribers?.size }
            else fansAndFriends.sortedByDescending { user -> user.subscribers?.size }) { user ->
                UserInfoCard(user)
                HorizontalDivider(
                    Modifier.height(2.dp),
                    color = darkBasic
                )

            }

        }


    }

}

