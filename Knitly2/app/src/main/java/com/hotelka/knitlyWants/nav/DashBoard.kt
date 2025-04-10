package com.hotelka.knitlyWants.nav

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hotelka.knitlyWants.Cards.DraftCard
import com.hotelka.knitlyWants.Cards.ProjectProgressCard
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.PROJECTS
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.ui.theme.Search
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Preview
@Composable
fun DashBoard() {

    var usersDrafts = remember { mutableStateListOf<Any>() }
    usersDrafts.apply {
        clear()
        addAll(SupportingDatabase(LocalContext.current).getAllProjectsDraft())
        addAll(SupportingDatabase(LocalContext.current).getAllBlogDrafts())
    }

    val projectsInProgress = remember { mutableStateListOf<ProjectsArchive>() }

    var projectsInProgressCount by remember { mutableIntStateOf(0) }
    var projectsCompletedCount by remember { mutableIntStateOf(0) }
    var refreshing by remember { mutableStateOf(true) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(2000)
            refreshing = false
        }
    }
    LaunchedEffect(Unit) {
        projectsInProgress.clear()
        FirebaseDB.collectCurrentUserProjectsWorks { project ->
            projectsInProgress.add(project)
            if (project.progress == 1f) projectsCompletedCount++
            else projectsInProgressCount++
        }
    }

    var projectsCreatedCount by remember { mutableIntStateOf(0) }
    FirebaseDB.refUsers.child(userData.value.userId).child(PROJECTS).get().addOnSuccessListener {
        projectsCreatedCount = it.childrenCount.toInt()
    }
    val scrollState = rememberLazyListState()
    var paddingFromSearch by remember { mutableStateOf(70.dp) }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            refreshing = true

        },
    ) {
        Surface(color = basic) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .background(Transparent)
            ) {
                Search(scrollState, { it -> paddingFromSearch = it })

                LazyColumn(
                    modifier = Modifier
                        .background(Transparent)
                        .padding(top = paddingFromSearch)
                        .padding(),
                    scrollState
                ) {
                    item {
                        AnimatedVisibility(
                            visible = usersDrafts.isNotEmpty()
                        ) {
                            Text(
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .background(basic)
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = 20.dp,
                                            topStart = 20.dp,
                                            bottomEnd = 20.dp
                                        )
                                    )
                                    .background(accent_secondary) // Replace with your drawable oval
                                    .padding(10.dp),
                                text = stringResource(R.string.myDrafts),
                                color = white
                            )
                        }
                        LazyRow(
                            modifier = Modifier
                                .background(basic)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.Center),
                            contentPadding = PaddingValues(5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            itemsIndexed(usersDrafts) { index, draft ->
                                when (draft) {
                                    is Blog -> DraftCard(null, draft)
                                    is Project -> DraftCard(draft, null)
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = projectsInProgress.isNotEmpty()
                        ) {
                            Text(
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .background(basic)
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topEnd = 20.dp,
                                            topStart = 20.dp,
                                            bottomEnd = 20.dp
                                        )
                                    )
                                    .background(accent_secondary) // Replace with your drawable oval
                                    .padding(10.dp),
                                text = stringResource(R.string.myProjects),
                                color = white
                            )
                        }
                        LazyRow(
                            modifier = Modifier
                                .background(basic)
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            items(projectsInProgress) {
                                ProjectProgressCard(it)
                            }
                        }

                        Text(
                            fontSize = 25.sp,
                            modifier = Modifier
                                .background(basic)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topEnd = 20.dp,
                                        topStart = 20.dp,
                                        bottomEnd = 20.dp
                                    )
                                )
                                .background(accent_secondary) // Replace with your drawable oval
                                .padding(10.dp),
                            text = stringResource(R.string.statics),
                            color = white
                        )
                        Row(
                            modifier = Modifier
                                .background(basic)
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(end = 20.dp)
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(headers_activeElement)
                            ) {
                                Text(
                                    text = stringResource(R.string.projectsComplete),
                                    modifier = Modifier
                                        .padding(end = 20.dp, start = 10.dp)
                                        .padding(top = 10.dp),
                                    fontSize = 14.sp,
                                    color = white,
                                    style = TextStyle.Default.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 20.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .padding(30.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .drawBehind {
                                                drawCircle(
                                                    style = Stroke(
                                                        width = 5f
                                                    ),
                                                    color = white,
                                                    radius = this.size.maxDimension
                                                )
                                            },
                                        text = projectsCompletedCount.toString(),
                                        fontSize = 30.sp,
                                        color = white
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(accent_secondary),
                            ) {
                                Text(
                                    text = stringResource(R.string.projectsInProgress),
                                    modifier = Modifier
                                        .padding(end = 20.dp, start = 10.dp)
                                        .padding(top = 10.dp),
                                    fontSize = 13.sp,
                                    color = white,
                                    style = TextStyle.Default.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 20.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .padding(30.dp)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .drawBehind {
                                                drawCircle(
                                                    style = Stroke(
                                                        width = 5f
                                                    ),
                                                    color = white,
                                                    radius = this.size.maxDimension
                                                )
                                            },
                                        text = projectsInProgressCount.toString(),
                                        fontSize = 30.sp,
                                        color = white
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .background(basic)
                                .padding(10.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(textColor)
                        ) {
                            Text(
                                text = stringResource(R.string.projectsCreated),
                                modifier = Modifier
                                    .padding(end = 20.dp, start = 10.dp)
                                    .padding(top = 10.dp),
                                fontSize = 14.sp,
                                color = white,
                                style = TextStyle.Default.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(30.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .drawBehind {
                                            drawCircle(
                                                style = Stroke(
                                                    width = 5f
                                                ),
                                                color = white,
                                                radius = this.size.maxDimension
                                            )
                                        },
                                    text = projectsCreatedCount.toString(),
                                    fontSize = 30.sp,
                                    color = white
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

