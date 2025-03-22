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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.editableBlog
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DraftCard(project: Project? = Project(), blog: Blog? = Blog()) {
    var expanded by remember { mutableStateOf(false) }
    var projectData by remember { mutableStateOf(
        if (project != null) project.projectData
        else blog?.projectData
    ) }
    Card(
        modifier = Modifier
            .padding(7.dp)
            .clickable { expanded = !expanded }
            .wrapContentSize()
            .background(Transparent),
    ) {

        Column {
            Box(
                Modifier
            ) {
                AsyncImage(
                    modifier = Modifier
                        .background(white)
                        .height(250.dp)
                        .width(250.dp),
                    model = if (projectData!!.cover!!.isNotEmpty() == true) projectData!!.cover
                    else R.drawable.baseline_photo_camera_24,
                    colorFilter = if (projectData!!.cover!!.isNotEmpty() == true) null
                    else ColorFilter.tint(accent_secondary),                    contentScale = ContentScale.Crop,
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
                        text = projectData!!.title!!,
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
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            Modifier
                                .padding(start = 5.dp)
                                .size(25.dp),
                            colorFilter = ColorFilter.tint(textColor)
                        )
                        Text(
                            text = stringResource(R.string.lastUpdate) + projectData!!.date,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 5.dp),
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
                        if (blog == null) {
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
                            text = projectData!!.description,
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
                                if (project != null) {editableProject = project; editableBlog = null}
                                else {editableBlog = blog; editableProject = null}
                                navController.navigate("createProject")
                            }
                        ) { Text(text = stringResource(R.string.edit)) }

                    }

                }
            }
        }
    }

}