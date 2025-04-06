package com.hotelka.knitlyWants.Cards

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjectsInProgress
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.formatNumber
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.projectCurrent
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData

@Composable
fun ProjectContainer(project: Project, exists: Boolean) {
    var context = LocalContext.current
    var started by remember {mutableStateOf(context.getString(R.string.started))}
    refProjectsInProgress.child(userData.value.userId).child(project.projectData!!.projectId!!).get().addOnSuccessListener{
        started = if (it.exists()) context.getString(R.string.started)
        else context.getString(R.string.startProject)
    }
    var likes by remember { mutableStateOf(project.projectData.likes) }
    var likeIcon by remember { mutableStateOf(
        if (project.projectData.likes.users?.contains(
                userData.value.userId
            ) == true
        ) Icons.Filled.Favorite
        else Icons.Outlined.FavoriteBorder
    ) }
    var likeCount by remember { mutableStateOf(project.projectData.likes.total) }
    var iconColorFilter by remember { mutableStateOf(
        if (project.projectData.likes.users?.contains(
                userData.value.userId
            ) == true
        ) ColorFilter.tint(headers_activeElement)
        else ColorFilter.tint(Color.Gray)
    ) }
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                FirebaseDB.sendReview(project.projectData.projectId, project.projectData.reviews)
                projectCurrent = project
                navController.navigate("projectOverview")
            }
            .padding(vertical = 15.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(basic)
    ) {

        Row(
            modifier = Modifier
                .background(basic)
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.TopStart)

                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 5.dp),
                        color = textColor,
                        fontSize = 15.sp,
                        text = "${project.projectData.author}, ${project.projectData.date}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        color = textColor,
                        fontSize = 15.sp,
                        text = project.projectData.title!!,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        modifier = Modifier
                            .padding(5.dp)
                            .width(200.dp)
                            .padding(bottom = 30.dp)
                            .height(122.dp)
                            .padding(2.dp),
                        overflow = TextOverflow.Ellipsis,
                        color = textColor,
                        fontSize = 16.sp,
                        text = project.projectData.description
                    )

                }
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    Pair(0.2f, Transparent),
                                    Pair(1f, basic)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 5.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = started,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .background(
                                    color = headers_activeElement,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    if (started != context.getString(R.string.started)) {
                                        FirebaseDB.startProject(
                                            project
                                        ) { started = context.getString(R.string.started) }
                                        SupportingDatabase(context).addProjectInProgress(
                                            ProjectsArchive(project),
                                            project.projectData.projectId
                                        )
                                    }
                                }
                                .padding(10.dp),
                            fontSize = 10.sp,
                            color = white,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            painter = painterResource(R.drawable.reviews),
                            contentDescription = "Reviews",
                            Modifier
                                .padding(start = 5.dp)
                                .size(25.dp)
                                .align(Alignment.CenterVertically),
                            colorFilter = ColorFilter.tint(textColor)
                        )
                        Text(
                            text = formatNumber(project.projectData.reviews),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 5.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Image(
                            imageVector = likeIcon ,
                            contentDescription = "Like",
                            Modifier
                                .size(25.dp)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    FirebaseDB.sendLike(
                                        project.projectData.projectId.toString(),
                                        likes
                                    ) { sent, like ->
                                        likes = like
                                        if (sent){
                                            likeCount = likeCount!! + 1
                                            likeIcon = Icons.Filled.Favorite
                                            iconColorFilter = ColorFilter.tint(headers_activeElement)
                                        } else {
                                            likeCount = likeCount!! - 1
                                            likeIcon = Icons.Outlined.FavoriteBorder
                                            iconColorFilter = ColorFilter.tint(Color.Gray)
                                        }
                                    }
                                },
                            colorFilter = iconColorFilter
                        )
                        Text(
                            text = formatNumber(likeCount!!),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                AsyncImage(
                    model = project.projectData.cover.toString(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Scheme Cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.9f }
                        .drawWithContent {
                            val colors = listOf(
                                Transparent,
                                Color(0, 0, 0, 250),
                                Color.Black,
                            )
                            drawContent()
                            drawRect(
                                brush = Brush.horizontalGradient(colors),
                                blendMode = BlendMode.DstIn
                            )
                        }
                        .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)),

                    )
            }
        }

    }
}