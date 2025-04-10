package com.hotelka.knitlyWants.nav

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.Cards.BlogCard
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Tutorials
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
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
    var query by remember { mutableStateOf("") }
    var searched by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    var tutorials by remember { mutableStateOf(Tutorials()) }
    var base = remember { mutableStateListOf<Blog>() }
    var crochet = remember { mutableStateListOf<Blog>() }
    var knitting = remember { mutableStateListOf<Blog>() }

    var author by remember { mutableStateOf(UserData()) }

    tutorials.apply {
        FirebaseDB.getAllTutorials {
            tutorials = it
            if (base.isEmpty()) {
                base.addAll(tutorials.All!!.values)
                crochet.addAll(tutorials.Crocheting!!.values)
                knitting.addAll(tutorials.Knitting!!.values)
            }
        }
    }
    fun search() {
        base.clear(); crochet.clear();knitting.clear()
        tutorials.apply {
            All!!.values.forEach { blog ->
                FirebaseDB.getUser(blog.projectData!!.authorID.toString()) { author = it }
                if (blog.projectData!!.description.toLowerCase(Locale.ROOT).contains(query) ||
                    author.username!!.toLowerCase(Locale.ROOT).contains(query) ||
                    blog.projectData!!.title!!.toLowerCase(Locale.ROOT).contains(query)
                ) {
                    base.add(blog)
                }
            }
            Crocheting!!.values.forEach { blog ->
                FirebaseDB.getUser(blog.projectData!!.authorID.toString()) { author = it }
                if (blog.projectData!!.description.toLowerCase(Locale.ROOT).contains(query) ||
                    author.username!!.toLowerCase(Locale.ROOT).contains(query) ||
                    blog.projectData!!.title!!.toLowerCase(Locale.ROOT).contains(query)
                ) {
                    crochet.add(blog)
                }
            }
            Knitting!!.values.forEach { blog ->
                FirebaseDB.getUser(blog.projectData!!.authorID.toString()) { author = it }
                if (blog.projectData!!.description.toLowerCase(Locale.ROOT).contains(query) ||
                    author.username!!.toLowerCase(Locale.ROOT).contains(query) ||
                    blog.projectData!!.title!!.toLowerCase(Locale.ROOT).contains(query)
                ) {
                    knitting.add(blog)
                }
            }
        }
        if (base.isEmpty()) {
            base.addAll(tutorials.All!!.values)
            crochet.addAll(tutorials.Crocheting!!.values)
            knitting.addAll(tutorials.Knitting!!.values)
        }
    }
    Column {
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
                            base.clear();crochet.clear();knitting.clear()
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
                onSearch = { search(); keyboard?.hide(); searched = true }
            )
        )
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
                    .weight(1f)
                    .background(basic),
            ) {
                when (pagerState.currentPage) {
                    0 -> {
                        LazyColumn(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (base.isEmpty() && query.isNotEmpty() && searched) {
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

                                }
                            } else {
                                itemsIndexed(base) { index, blog ->
                                    BlogCard(blog)
                                }
                            }

                        }
                    }

                    1 -> {
                        LazyColumn(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (knitting.isEmpty() && query.isNotEmpty() && searched) {
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

                                }
                            } else {
                                itemsIndexed(knitting) { index, blog ->
                                    BlogCard(blog)
                                }
                            }
                        }
                    }

                    2 -> {
                        LazyColumn(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (crochet.isEmpty() && query.isNotEmpty() && searched) {
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

                                }
                            } else {
                                itemsIndexed(crochet) { index, blog ->
                                    BlogCard(blog)
                                }
                            }
                        }
                    }
                }
            }
        }


    }


}
