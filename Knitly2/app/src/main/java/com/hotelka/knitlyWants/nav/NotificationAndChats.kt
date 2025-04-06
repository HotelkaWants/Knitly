package com.hotelka.knitlyWants.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.Cards.ChatCard
import com.hotelka.knitlyWants.Cards.TutorialCard
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.ui.theme.Tabs
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import kotlinx.coroutines.launch

@Preview
@Composable
fun NotificationsAndChats() {
    val notificationsTab = Tabs(
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        text = stringResource(R.string.notifications)
    )
    val chatsTab = Tabs(
        selectedIcon = ImageVector.vectorResource(R.drawable.round_message_24),
        unselectedIcon = ImageVector.vectorResource(R.drawable.outline_message_24),
        text = stringResource(R.string.chats)
    )
    val scope = rememberCoroutineScope()
    val tabs =
        remember { mutableStateListOf<Tabs>(notificationsTab, chatsTab) }
    var pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 2 }
    )
    var selectedTab = remember { derivedStateOf { pagerState.currentPage } }
    var chats = remember { mutableStateListOf<Chat>() }
    FirebaseDB.getChats{ chat ->
        chat?.let {
            if (!chats.contains(it)){
                chats.add(it)
            }
        }

    }
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
                    LazyColumn {

                    }
                }

                1 -> {
                    LazyColumn {
                        items(chats){ chat ->
                            ChatCard(chat)
                        }
                    }
                }


            }
        }
    }

}