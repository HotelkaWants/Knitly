package com.hotelka.knitlyWants.nav

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hotelka.knitlyWants.Cards.BlogCard
import com.hotelka.knitlyWants.Cards.ProjectContainer
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import com.hotelka.knitlyWants.userWatching
import com.hotelka.knitlyWants.users
import kotlinx.coroutines.delay


@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val db = SupportingDatabase(context)
    var projects by remember { mutableStateOf(db.getAllProjects()) }
    Log.d("projectsHome", db.getAllProjects().toString())
    var blogs = db.getAllBlog()
    var refreshing by remember { mutableStateOf(true) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            FirebaseDB.createSupportingDatabase(context)
            projects = db.getAllProjects()
            blogs = db.getAllBlog()
            users.value = db.getAllUsers()
            delay(2000)
            refreshing = false
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            refreshing = true
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    LazyRow(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        items(users.value) {
                            if (it.userId != userData.value.userId) UserItem(it)
                        }
                    }

                }

                item {
                    Text(
                        text = stringResource(R.string.news),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topEnd = 20.dp,
                                    topStart = 20.dp,
                                    bottomEnd = 20.dp
                                )
                            )
                            .background(accent_secondary)
                            .padding(10.dp),
                        fontSize = 28.sp,
                        color = white
                    )
                }
                item {
                    LazyRow(modifier = Modifier.padding(10.dp)) {
                        itemsIndexed(blogs) { index, blog ->
                            BlogCard(blog)
                        }
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.inspiration),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topEnd = 20.dp,
                                    topStart = 20.dp,
                                    bottomEnd = 20.dp
                                )
                            )
                            .background(accent_secondary)
                            .padding(10.dp),
                        fontSize = 28.sp,
                        color = white
                    )
                }
                itemsIndexed(
                    projects,
                    key = { index, item -> item.projectData!!.projectId!! }) { index, project ->
                    ProjectContainer(project, false)
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(20.dp)
                    .wrapContentSize()
                    .align(Alignment.BottomEnd),
                onClick = {
                    editableProject = null
                    navController.navigate("createProject")
                },
                containerColor = accent_secondary,
                shape = RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp,
                    bottomStart = 30.dp
                ),
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "add", tint = white)
            }
        }
    }
}

@Composable
@Preview
fun UserItem(user: UserData = UserData()) {
    FirebaseDB.isOnlineGet(user.userId){
        user.isOnline = it
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .background(Color.Transparent)
            .clickable {
                userWatching = user
                navController.navigate("userProfile")
            },
        content = {
            Box {
                if (user.profilePictureUrl != null) {
                    AsyncImage(
                        model = user.profilePictureUrl,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        contentDescription = "SchemePreview"
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.baseline_account_circle_24,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "SchemePreview"
                    )
                }
                if (user.isOnline) {
                    Badge(
                        containerColor = headers_activeElement,
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }


            Text(
                text = user.username!!,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 14.sp,
                color = textColor
            )

        }
    )
}

