package com.hotelka.knitlyWants.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelka.knitlyWants.Cards.ChatCard
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.ui.theme.Tabs
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import kotlinx.coroutines.launch

@Preview
@Composable
fun NotificationsAndChats() {
//    val notificationsTab = Tabs(
//        selectedIcon = Icons.Filled.Notifications,
//        unselectedIcon = Icons.Outlined.Notifications,
//        text = stringResource(R.string.notifications)
//    )
    val chatsTab = Tabs(
        selectedIcon = ImageVector.vectorResource(R.drawable.round_message_24),
        unselectedIcon = ImageVector.vectorResource(R.drawable.outline_message_24),
        text = stringResource(R.string.chats)
    )
    val scope = rememberCoroutineScope()
    val tabs =
        remember { mutableStateListOf<Tabs>(chatsTab) }
    var pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 1 }
    )
    var selectedTab = remember { derivedStateOf { pagerState.currentPage } }
    var chats = remember { mutableStateListOf<Chat>() }
    FirebaseDB.getChats { chat ->
        chat?.let {
            if (!chats.contains(it)) {
                chats.add(it)
            }
        }

    }
    var deleteChat by remember { mutableStateOf(Chat()) }
    var deleteUserId by remember { mutableStateOf("") }
    var deleteUserName by remember { mutableStateOf("") }
    var deleteChatIndex by remember { mutableIntStateOf(0) }
    var deleteAlert by remember { mutableStateOf(false) }
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(basic)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                containerColor = basic,
                contentColor = basic,
                indicator = { tabPositions ->
                    if (selectedTab.value < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.value]),
                            color = darkBasic
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTab.value == index,
                        selectedContentColor = textColor,
                        unselectedContentColor = textColor,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Row {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = currentTab.text
                                )
                                Icon(
                                    modifier = Modifier.padding(start = 2.dp),
                                    imageVector = if (selectedTab.value == index) currentTab.selectedIcon
                                    else currentTab.unselectedIcon,
                                    contentDescription = null
                                )
                            }
                        },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                when (pagerState.currentPage) {

                    0 -> {
                        if (chats.isNotEmpty()) {

                            LazyColumn {
                                itemsIndexed(chats) { index, chat ->
                                    ChatCard(chat) { userId, userName, chat ->
                                        deleteUserId = userId; deleteChat = chat; deleteUserName =
                                        userName; deleteChatIndex = index
                                        deleteAlert = true
                                    }
                                    HorizontalDivider(color = darkBasic, thickness = 2.dp)
                                }
                            }
                        } else {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 50.dp),
                                text = stringResource(R.string.notChats),
                                textAlign = TextAlign.Center,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )

                        }
                    }


                }
            }
        }
        if (deleteAlert) {
            DeleteChatAlert(deleteUserName, onDismiss = { deleteAlert = false }) {
                FirebaseDB.deleteChat(deleteUserId, deleteChat.id.toString()) {
                    chats.removeAt(deleteChatIndex)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteChatAlert(user: String, onDismiss: () -> Unit, onDelete: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.deleteChat) + user + "?",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = textColor

                )


                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))

                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onDelete()
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.delete))

                    }
                }
            }
        }
    }
}