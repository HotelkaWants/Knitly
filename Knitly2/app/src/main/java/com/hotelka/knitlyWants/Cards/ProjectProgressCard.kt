package com.hotelka.knitlyWants.Cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.nav.formatTime
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.WaveProgress
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white

@Composable
@Preview
fun ProjectProgressCard(project: ProjectsArchive = ProjectsArchive()) {
    val currentDetail = project.project!!.details?.get(project.progressDetails!!.detail!!)
    val category = project.project.category

    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .wrapContentWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable{
                currentProjectInProgress = project
                navController.navigate("workingOnProject")
            }
        ) {
        Row {
            AsyncImage(
                model = project.project.projectData!!.cover,
                contentScale = ContentScale.Crop,
                contentDescription = "Scheme Cover",
                modifier = Modifier
                    .wrapContentHeight()
                    .width(200.dp)
                    .background(white)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        val colors = listOf(
                            Black,
                            Black,
                            Transparent,
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    }

            )
            Column(Modifier.background(white).fillMaxHeight().padding(15.dp), verticalArrangement = Arrangement.SpaceAround) {
                Text(
                    modifier = Modifier,
                    text = project.project.projectData.title!!,
                    fontSize = 20.sp,
                    color = textColor,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    modifier = Modifier.width(70.dp).padding(top = 5.dp),
                    text = if (project.progress != 1f) currentDetail!!.title!!
                    else stringResource(R.string.projectIsDone),
                    fontSize = 18.sp,
                    color = textColor,
                )
                AnimatedVisibility(visible = project.progress != 1f) {
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = "Row${project.progressDetails!!.row!! + 1}",
                        fontSize = 16.sp,
                        color = textColor,
                    )
                }
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (category == Category.Knitting)R.drawable.kneedles
                    else R.drawable.hook ),
                    contentDescription = null,
                    modifier = Modifier
                        .height(70.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = textColor
                )
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = formatTime(project.timeInProgress?.toInt()!!),
                    fontSize = 16.sp,
                    color = textColor,
                )
            }

            Box(modifier = Modifier.background(white).width(30.dp)) {
                WaveProgress(
                    progress = if (project.progress == 1f) 2f
                    else project.progress.toFloat(), modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clip(
                            RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}