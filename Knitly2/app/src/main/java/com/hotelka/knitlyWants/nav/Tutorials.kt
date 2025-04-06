package com.hotelka.knitlyWants.nav

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.Cards.TutorialCard
import com.hotelka.knitlyWants.Data.HistoryTutorialsData
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.ui.theme.Tabs
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import kotlinx.coroutines.launch
import java.util.Locale

@Preview
@Composable
fun prev() {
    Tutorials()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tutorials() {
    val supportingDatabase = SupportingDatabase(LocalContext.current)
    var searchHistory = remember { mutableStateListOf<HistoryTutorialsData>() }
//    searchHistory.addAll(roomDatabase.getHistoryTutorialsList())
    var query by remember { mutableStateOf("") }
    var tabsActive by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        windowInsets = WindowInsets(top = 0.dp),
        query = query,
        onQueryChange = {
            query = it.toLowerCase(Locale.ROOT)
        },
        onSearch = {
            searchHistory.add(
                HistoryTutorialsData(
                    id = "",
                    historyTitle = it,
                    resultType = ""
                )
            )
            tabsActive = true
        },
        active = true,
        onActiveChange = {

        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = textColor
            )
        },
        trailingIcon = {

            Icon(
                modifier = Modifier.clickable {
                    query = ""
                },
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = textColor
            )

        },
        placeholder = {
            Text(stringResource(R.string.search))
        },
        colors = SearchBarDefaults.colors(
            containerColor = basic,
            dividerColor = darkBasic,
        ),
    ) {
        val knittingBaseTab = Tabs(
            selectedIcon = ImageVector.vectorResource(R.drawable.baseline_library_books_24),
            unselectedIcon = ImageVector.vectorResource(R.drawable.baseline_library_books_24),
            text = stringResource(R.string.knittingBase)
        )
        val knittingTab = Tabs(
            selectedIcon = ImageVector.vectorResource(R.drawable.kneedles),
            unselectedIcon = ImageVector.vectorResource(R.drawable.kneedles),
            text = stringResource(R.string.knitting)
        )
        val crochetTab = Tabs(
            selectedIcon = ImageVector.vectorResource(R.drawable.hook),
            unselectedIcon = ImageVector.vectorResource(R.drawable.hook),
            text = stringResource(R.string.crocheting)
        )
        val scope = rememberCoroutineScope()
        val tabs =
            remember { mutableStateListOf<Tabs>(knittingBaseTab, knittingTab, crochetTab) }
        var pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { 3 }
        )
        var selectedTab = remember { derivedStateOf { pagerState.currentPage } }
         Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(white)
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
                            LazyColumn {
                                items(20){
                                    TutorialCard()
                                }
                            }
                        }

                        1 -> {

                        }

                        2 -> {

                        }
                    }
                }
            }



    }


}
