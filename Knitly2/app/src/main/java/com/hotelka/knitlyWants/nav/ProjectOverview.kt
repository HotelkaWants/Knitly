package com.hotelka.knitlyWants.nav

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.Category.Companion.Crocheting
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.blogCurrent
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.projectCurrent
import com.hotelka.knitlyWants.ui.theme.Shapes
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun preview() {
}

@Composable
fun ProjectOverview(projectData: Project? = null, blog: Blog? = null) {

    val cover by remember { mutableStateOf(
        if (projectData != null) projectData.projectData?.cover
        else blog?.projectData?.cover
    ) }
    val category by remember { mutableStateOf(
        if (projectData != null) projectData.category
        else blog?.category
    ) }
    val title by remember { mutableStateOf(
        if (projectData != null) projectData.projectData?.title
        else blog?.projectData?.title
    ) }
    val description by remember { mutableStateOf(
        if (projectData != null) projectData.projectData?.description
        else blog?.projectData?.description
    ) }
    val author by remember { mutableStateOf(
        if (projectData != null) projectData.projectData?.authorID
        else blog?.projectData?.authorID
    ) }

    //Knitting
    val tool by remember { mutableStateOf(projectData?.tool) }
    val details by remember { mutableStateOf(projectData?.details) }
    val yarns by remember { mutableStateOf(projectData?.yarns) }
    var expandedRows = remember { mutableStateListOf<Boolean>() }
    details?.forEach { expandedRows.add(false) }

    //Blog
    var additionalImages = (blog?.additionalImages)
    BackHandler {
        projectCurrent = null
        blogCurrent = null
        navController.popBackStack()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(textFieldColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val lazyListState = rememberLazyListState()
            var scrolledY = 0f
            var previousOffset = 0
            LazyColumn(
                Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                lazyListState,
            ) {

                item {
                    Column(
                        Modifier
                            .background(basic)
                            .graphicsLayer {
                                scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                                translationY = scrolledY * 0.5f
                                previousOffset =
                                    lazyListState.firstVisibleItemScrollOffset
                            }
                    ) {
                        Box(
                            Modifier
                                .wrapContentHeight()
                        ) {
                            AsyncImage(
                                model = cover,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .heightIn(0.dp, 400.dp)
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                            )
                            if (author == userData.value.userId) {
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                        .background(
                                            white,
                                            RoundedCornerShape(20.dp)
                                        ),
                                    onClick = {
                                        editableProject = projectData
                                        navController.popBackStack()
                                        navController.navigate("createProject")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = textColor
                                    )
                                }
                            }
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    category!!,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor,
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .clip(Shapes.small)
                                        .background(Color(250, 241, 235, 210))
                                        .padding(vertical = 6.dp, horizontal = 16.dp)

                                )


                                TextField(
                                    enabled = false,
                                    value = title!!,
                                    onValueChange = {},
                                    maxLines = 1,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colorStops = arrayOf(
                                                    Pair(0f, Transparent),
                                                    Pair(1f, textFieldColor)
                                                )
                                            )
                                        )
                                        .padding(start = 15.dp, end = 15.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 20.sp),
                                    colors = TextFieldDefaults.colors(
                                        disabledTextColor = textColor,
                                        disabledContainerColor = Transparent,
                                        disabledLabelColor = textColor
                                    ),
                                    label = { Text(stringResource(R.string.title)) },
                                )
                            }
                        }
                        TextField(
                            enabled = false,
                            value = description!!,
                            onValueChange = { },
                            modifier = Modifier
                                .background(textFieldColor)
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = textColor,
                                disabledContainerColor = white,
                                disabledLabelColor = textColor
                            ),
                            textStyle = TextStyle(fontSize = 16.sp),
                            label = { Text(stringResource(R.string.description)) }
                        )

                    }

                }
                if (category != Category.Blog) {
                    item {
                        Row(
                            Modifier
                                .background(textFieldColor)
                                .padding(start = 10.dp, end = 10.dp)
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextField(
                                enabled = false,
                                value = tool.toString(),
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp)),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledContainerColor = textFieldColor,
                                    disabledLabelColor = textColor
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.hook),
                                        contentDescription = null,
                                        tint = textColor,
                                    )
                                }
                            )
                        }
                    }
                    item {
                        Row(
                            Modifier
                                .background(textFieldColor)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextField(
                                enabled = false,
                                value = yarns.toString(),
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp)),
                                label = { Text(stringResource(R.string.yarns)) },
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = textColor,
                                    disabledContainerColor = textFieldColor,
                                    disabledLabelColor = textColor
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.yarn),
                                        contentDescription = null,
                                        tint = textColor
                                    )
                                }

                            )
                        }
                    }
                }
                when (category) {
                    Crocheting -> {
                        details?.let {
                            itemsIndexed(it) { indexD, detail ->
                                Column {
                                    OutlinedTextField(
                                        enabled = false,
                                        value = detail.title!!,
                                        onValueChange = {},
                                        modifier = Modifier
                                            .background(textFieldColor)
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        label = { Text(stringResource(R.string.detail)) },
                                        textStyle = LocalTextStyle.current.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                        ),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = textColor,
                                            disabledContainerColor = white,
                                            disabledLabelColor = textColor
                                        ),
                                        trailingIcon = {
                                            IconButton(
                                                onClick = {
                                                    expandedRows[indexD] = !expandedRows[indexD]
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowDropDown,
                                                    contentDescription = null,

                                                    )
                                            }
                                        }
                                    )
                                    detail.rows.forEachIndexed { index, row ->
                                        AnimatedVisibility(
                                            visible = expandedRows[indexD],
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
                                            Box() {
                                                Column(modifier = Modifier.background(textFieldColor)) {
                                                    TextField(
                                                        enabled = false,

                                                        value = row.description!!,
                                                        onValueChange = {},
                                                        textStyle = LocalTextStyle.current.copy(
                                                            fontSize = 15.sp,
                                                        ),
                                                        label = { Text("Row${index + 1}") },
                                                        modifier = Modifier
                                                            .background(textFieldColor)
                                                            .padding(
                                                                horizontal = 5.dp
                                                            )
                                                            .padding(top = 5.dp)
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(20.dp)),
                                                        colors = TextFieldDefaults.colors(
                                                            disabledTextColor = textColor,
                                                            disabledContainerColor = textFieldColor,
                                                            disabledLabelColor = textColor
                                                        ),
                                                    )
                                                    AnimatedVisibility(
                                                        visible = row.noteAdded,
                                                        enter = fadeIn() + scaleIn(),
                                                        exit = fadeOut() + scaleOut()
                                                    ) {
                                                        var textFieldSize by remember {
                                                            mutableStateOf(
                                                                IntSize.Zero
                                                            )
                                                        }
                                                        var imagesRowSize by remember {
                                                            mutableStateOf(
                                                                IntSize.Zero
                                                            )
                                                        }
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .wrapContentHeight()
                                                                .background(textFieldColor)
                                                        ) {
                                                            TextField(
                                                                enabled = false,
                                                                modifier = Modifier
                                                                    .padding(horizontal = 20.dp)
                                                                    .background(textFieldColor)
                                                                    .fillMaxWidth(),
                                                                value = row.note!!.text!!,
                                                                onValueChange = {},
                                                                colors = TextFieldDefaults.colors(
                                                                    disabledTextColor = textColor,
                                                                    disabledContainerColor = textFieldColor,
                                                                    disabledLabelColor = textColor
                                                                )
                                                            )
                                                            if (row.note?.imageUrl?.isNotEmpty() == true) {
                                                                LazyRow(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .heightIn(0.dp, 200.dp)
                                                                ) {

                                                                    itemsIndexed(row.note?.imageUrl!!) { indexI, url ->
                                                                        Image(
                                                                            painter = rememberAsyncImagePainter(url),
                                                                            modifier = Modifier
                                                                                .fillMaxSize()
                                                                                .padding(10.dp)
                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        20.dp
                                                                                    )
                                                                                ),
                                                                            contentScale = ContentScale.Crop,
                                                                            contentDescription = "Note Image"
                                                                        )
                                                                    }

                                                                }

                                                            }
                                                        }

                                                    }

                                                }

                                            }
                                        }
                                    }


                                }
                            }
                        }
                    }

                    Category.Knitting -> {

                    }
                    Category.Blog -> {
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .background(textFieldColor)
                                    .fillMaxWidth()
                                    .heightIn(0.dp, 200.dp),
                            ) {

                                itemsIndexed(additionalImages!!) { indexI, url ->
                                    Image(
                                        painter = rememberAsyncImagePainter(url),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    20.dp
                                                )
                                            ),
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = "Blog Image"
                                    )
                                }
                            }
                        }
                    }
                }
                if (projectData != null) {
                    item {
                        var projectIsStarted by remember { mutableStateOf(false) }
                        var project by remember { mutableStateOf<ProjectsArchive>(ProjectsArchive()) }
                        FirebaseDB.refProjectsInProgress.child(userData.value.userId).get()
                            .addOnSuccessListener { it ->
                                for (child in it.children) {
                                    if (projectData.projectData!!.projectId == child.key) {
                                        projectIsStarted = true
                                        project = child.getValue(ProjectsArchive::class.java)!!
                                        break
                                    }
                                }
                            }
                        Button(
                            onClick = {
                                if (projectIsStarted) {
                                    currentProjectInProgress = project
                                    navController.navigate("workingOnProject")

                                } else {
                                    FirebaseDB.startProject(
                                        projectData,
                                        { navController.navigate("workingOnProject") })
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent_secondary
                            )
                        ) {
                            Text(
                                text = stringResource(
                                    if (projectIsStarted)
                                        if (project.progress != 1f) R.string.continueWorking else R.string.projectIsDone
                                    else R.string.startProject
                                )
                            )
                        }
                    }
                }
            }
        }

    }

}