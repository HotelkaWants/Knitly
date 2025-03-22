package com.hotelka.knitlyWants.Cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.projectCurrent
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedProjectCard(project: Project?, author: UserData) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable { expanded = !expanded }
            .padding(7.dp)
            .wrapContentSize()
            .background(Transparent),
    ) {

        Column {
            Box(

            ) {
                Image(
                    modifier = Modifier
                        .height(250.dp)
                        .width(250.dp),
                    painter = rememberAsyncImagePainter(project?.projectData!!.cover),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Scheme Cover"
                )
                Column(
                    Modifier
                        .width(250.dp)
                        .padding(top = 220.dp)
                        .align(Alignment.BottomStart)
                        .background(white,RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 5.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = project?.projectData?.title!!,
                        modifier = Modifier
                            .width(150.dp),
                        fontSize = 24.sp,
                        color = textColor

                    )
                    Row(
                        modifier = Modifier
                            .wrapContentSize()

                    ) {
                        Image(
                            painter = painterResource(R.drawable.reviews),
                            contentDescription = "Reviews",
                            Modifier
                                .size(25.dp),
                            colorFilter = ColorFilter.tint(textColor)
                        )
                        Text(
                            text = project.projectData.reviews.toString(),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 5.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Image(
                            imageVector = if (project.projectData.likes.users?.contains(
                                    userData.value.userId
                                ) == true
                            ) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            Modifier
                                .size(25.dp)
                                .clickable {
                                    FirebaseDB.sendLike(
                                        project.projectData.projectId.toString(),
                                        project.projectData.likes
                                    )
                                },
                            colorFilter = if (project.projectData.likes.users?.contains(
                                    userData.value.userId
                                ) == true
                            ) ColorFilter.tint(headers_activeElement)
                            else ColorFilter.tint(Color.Gray)
                        )
                        Text(
                            text = project.projectData.likes.total.toString(),
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

            AnimatedVisibility(
                modifier = Modifier.background(Color(217, 217, 214)),
                visible = expanded,
                enter = slideInVertically(
                    initialOffsetY = {
                        it / 2
                    },
                ),
                exit = slideOutVertically(
                    targetOffsetY = {
                        it / 2
                    },
                ),

                ) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentHeight()
                        .background(
                            white,
                        )
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 5.dp)

                ) {
                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            AsyncImage(
                                model = if (author.profilePictureUrl!!.isEmpty()) {
                                    R.drawable.outline_account_circle_24
                                } else author.profilePictureUrl,
                                contentDescription = "Author",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        expanded = !expanded
                                    },
                                contentScale = ContentScale.Crop

                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = author.username.toString(),
                                color = textColor,
                            )

                            }
                        if (project?.category != Category.Blog) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(
                                        if (project?.category == Category.Knitting) R.drawable.kneedles
                                        else R.drawable.hook
                                    ),
                                    contentDescription = null,
                                    tint = textColor
                                )
                                Text(
                                    modifier = Modifier.padding(start = 10.dp),
                                    text = project?.tool.toString(),
                                    color = textColor,
                                )
                            }

                        }

                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = project?.projectData!!.description,
                            color = textColor,
                            fontSize = 16.sp,
                            style = LocalTextStyle.current.merge(
                                TextStyle(
                                    lineHeight = 1.5.em
                                )
                            )
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                            onClick = {
                                FirebaseDB.sendReview(project.projectData.projectId!!, project.projectData.reviews)
                                projectCurrent = project
                                navController.navigate("projectOverview")

                            }
                        ) { Text(text = stringResource(R.string.full_project)) }

                    }

                }
            }
        }
    }

}