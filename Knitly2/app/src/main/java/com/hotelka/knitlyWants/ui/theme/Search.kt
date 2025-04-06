package com.hotelka.knitlyWants.ui.theme

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.Cards.ProjectContainer
import com.hotelka.knitlyWants.Cards.UserInfoCard
import com.hotelka.knitlyWants.Data.BestResult
import com.hotelka.knitlyWants.Data.HistoryData
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(scrollState: LazyListState, setPadding: (Dp) -> Unit) {
    val context = LocalContext.current
    val supportingDatabase = SupportingDatabase(context)
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var tabsActive by remember { mutableStateOf(false) }
    var isSearchBarExpanded by remember { mutableStateOf(true) }

    var bestResult by remember { mutableStateOf<BestResult?>(null) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemScrollOffset }
            .collect { offset ->
                isSearchBarExpanded = offset < 100 // Adjust threshold as needed
            }
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val searchBarHeight by animateDpAsState(
        targetValue = if (active) {
            screenHeight

        } else if (isSearchBarExpanded) 54.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )
    setPadding(searchBarHeight)

    var projects by remember { mutableStateOf(supportingDatabase.getAllProjects()) }
    var existsList = remember { mutableStateListOf<Boolean>() }

    projects.forEach{ project ->
        existsList.add(supportingDatabase.getProjectInProgressExist(project.projectData!!.projectId!!))
    }
    var users by remember { mutableStateOf(supportingDatabase.getAllUsers()) }
    bestResult = BestResult(projects = projects, users = users)
    var filteredUsers = remember { mutableStateListOf<UserData>() }
    bestResult!!.users.apply {
        filteredUsers.clear()
        forEachIndexed { index, user ->
            if (user.username!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.name!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.lastName!!.toLowerCase(Locale.ROOT).contains(query) ||
                user.bio!!.toLowerCase(Locale.ROOT).contains(query)
            ) {
                filteredUsers.add(user)
            }
        }
    }
    var filteredProjects = remember { mutableStateListOf<Project>() }
    bestResult!!.projects.apply {
        filteredProjects.clear()
        forEachIndexed { index, project ->
            if (project.projectData!!.description.toLowerCase(Locale.ROOT).contains(query) ||
                project.projectData.author!!.toLowerCase(Locale.ROOT).contains(query) ||
                project.projectData.title!!.toLowerCase(Locale.ROOT).contains(query)
            ) {
                filteredProjects.add(project)
            }
        }
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(searchBarHeight),
        windowInsets = WindowInsets(top = 0.dp),
        query = query,
        onQueryChange = {
            query = it.toLowerCase(Locale.ROOT)
        },
        onSearch = {
            tabsActive = true
        },
        active = active,
        onActiveChange = {
            active = it
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = textColor
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = active,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        active = false
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = textColor
                )
            }
        },
        placeholder = {
            Text(stringResource(R.string.search))
        },
        colors = SearchBarDefaults.colors(
            containerColor = basic,
            dividerColor = darkBasic,
        ),
    ) {
        val projectsTab = Tabs(
            selectedIcon = ImageVector.vectorResource(R.drawable.baseline_space_dashboard_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.outline_space_dashboard_24),
            text = stringResource(R.string.projects)
        )
        val usersTab = Tabs(
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            text = stringResource(R.string.users)
        )
        val bestFound = Tabs(
            selectedIcon = ImageVector.vectorResource(R.drawable.outline_manage_search_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.baseline_manage_search_24),
            text = stringResource(R.string.bestFound)
        )
        val scope = rememberCoroutineScope()
        val tabs =
            remember { mutableStateListOf<Tabs>(bestFound, projectsTab, usersTab) }
        var pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { 3 }
        )
        var selectedTab = remember { derivedStateOf { pagerState.currentPage } }

        if (tabsActive) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    when (pagerState.currentPage) {
                        0 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    LazyRow(modifier = Modifier.wrapContentHeight()) {
                                        items(filteredUsers) { user ->
                                            UserInfoCard(user)
                                            SupportingDatabase(context).addHistory(
                                                selectedResult = user.userId,
                                                type = "user",
                                                UUID.randomUUID().toString()
                                            )
                                        }
                                    }
                                }
                                item {
                                    Column {
                                        filteredProjects.forEachIndexed { index, project ->
                                            ProjectContainer(project, existsList[index])
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(filteredProjects) { index, project ->
                                    ProjectContainer(project, existsList[index])
                                }
                            }
                        }

                        2 -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredUsers) { user ->
                                    UserInfoCard(user, "Vertical")
                                }

                            }
                        }
                    }
                }
            }

        }

    }


}


data class Tabs(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String,
)
