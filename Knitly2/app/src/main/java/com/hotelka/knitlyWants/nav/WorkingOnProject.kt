package com.hotelka.knitlyWants.nav

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.collection.SparseArrayCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.Shapes
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Preview
@Composable
fun WorkingOnProject(project: ProjectsArchive = ProjectsArchive()) {
    var context = LocalContext.current

    var secondsStopWatch by remember { mutableStateOf(project.timeInProgress!!.toInt()) }
    var running by remember { mutableStateOf(true) }


    if (running) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                secondsStopWatch++
            }
        }
    }
    var currentDetailRow by remember { mutableStateOf(project.progressDetails!!) }
    val details by remember { mutableStateOf(project.project!!.details!!) }
    var counterTitle by remember { mutableStateOf("Row${currentDetailRow.row?.plus(1)}") }
    val listLazyStates = remember { SparseArrayCompat<LazyListState>() }
    var currentDetailMaxRows by remember { mutableIntStateOf(details[currentDetailRow.detail!!].rows.size) }
    val coroutineScope = rememberCoroutineScope()
    val tabsScope = rememberCoroutineScope()
    val tabs = remember { mutableStateListOf<String>() }
    var infoExpanded by remember { mutableStateOf(false) }
    var restartProjectDialog by remember { mutableStateOf(false) }

    fun getProgress(): Double {
        var detailsRowsSum: Double = 0.0
        details.forEach { detail -> detailsRowsSum += detail.rows.size }
        var completedDetailsRowsSum: Double = 0.0
        details.forEachIndexed { index, detail -> if (index < currentDetailRow.detail!!) completedDetailsRowsSum += detail.rows.size }
        Log.d(
            "progress",
            ((completedDetailsRowsSum + currentDetailRow.row!!) / detailsRowsSum).toString()
        )

        return (completedDetailsRowsSum + currentDetailRow.row!!) / detailsRowsSum
    }
    BackHandler {
        val project = project.copy(
            progress = getProgress().toFloat(),
            timeInProgress = secondsStopWatch.toString(),
            progressDetails = currentDetailRow
        )
        FirebaseDB.saveProjectProgress(project, { navController.popBackStack() })
    }
    details.apply {
        tabs.clear()
        forEach {
            tabs.add(it.title!!)
        }
    }

    var pagerState = rememberPagerState(
        initialPage = currentDetailRow.detail!!,
        pageCount = { details.size }
    )
    var selectedTab = remember { derivedStateOf { pagerState.currentPage } }

    Surface(color = basic) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Transparent)
                    .padding(horizontal = 20.dp),
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp),
                    onClick = {
                        infoExpanded = !infoExpanded
                    }
                ) {
                    Icon(
                        imageVector = if (infoExpanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = textColor
                    )
                }
                Row(
                    Modifier
                        .wrapContentSize()
                        .align(Alignment.TopStart)
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(5.dp),
                        onClick = {
                            restartProjectDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = textColor
                        )
                    }
                    Text(
                        text = formatTime(secondsStopWatch),
                        modifier = Modifier
                            .padding(vertical = 20.dp, horizontal = 10.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor

                    )
                }
            }
            LazyColumn() {
                item {
                    AnimatedVisibility(
                        visible = infoExpanded,
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
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        Column(
                            Modifier
                                .background(basic)

                        ) {

                            Box(
                                Modifier
                                    .wrapContentHeight()
                            ) {
                                AsyncImage(
                                    model = project.project!!.projectData!!.cover,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .heightIn(0.dp, 400.dp)
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(
                                                bottomEnd = 20.dp,
                                                bottomStart = 20.dp
                                            )
                                        )
                                )

                                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                    Text(
                                        project.project.category!!,
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
                                        value = project.project.projectData.title!!,
                                        onValueChange = {},
                                        maxLines = 1,
                                        modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topEnd = 20.dp,
                                                    topStart = 20.dp
                                                )
                                            )
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
                                value = project.project!!.projectData!!.description,
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
                            Row(
                                Modifier
                                    .background(textFieldColor)
                                    .padding(start = 10.dp, end = 10.dp)
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                TextField(
                                    enabled = false,
                                    value = project.project.tool!!,
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


                            Row(
                                Modifier
                                    .background(textFieldColor)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                TextField(
                                    enabled = false,
                                    value = project.project.yarns!!,
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
                }
            }
            ScrollableTabRow(
                edgePadding = 0.dp,
                selectedTabIndex = selectedTab.value,
                modifier = Modifier,
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
                            tabsScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                modifier = Modifier.width(90.dp),
                                text = currentTab,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle.Default.copy(
                                    fontWeight = if (index == currentDetailRow.detail) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = if (index == currentDetailRow.detail) 16.sp else 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(top = if (infoExpanded) 40.dp else 0.dp)
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                val detail = details[pagerState.currentPage]
                val indexD = pagerState.currentPage
                Column {
                    val lazyState = rememberLazyListState().also {
                        listLazyStates.put(page, it)
                    }
                    OutlinedTextField(
                        enabled = false,
                        value = detail.title!!,
                        onValueChange = {},
                        modifier = Modifier
                            .background(textFieldColor)
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        ),
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = textColor,
                            disabledContainerColor = white,
                        )
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 80.dp),
                        state = lazyState
                    ) {
                        itemsIndexed(items = detail.rows) { indexR, row ->
                            Box(
                                Modifier
                                    .padding(horizontal = 15.dp)
                                    .padding(top = 5.dp)
                                    .background(
                                        if (indexR != currentDetailRow.row
                                            || indexD != currentDetailRow.detail
                                        ) textFieldColor else white,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(bottom = 15.dp)
                            ) {
                                Column(
                                    modifier = Modifier

                                ) {
                                    TextField(
                                        enabled = false,

                                        value = row.description!!,
                                        onValueChange = {},
                                        textStyle = LocalTextStyle.current.copy(
                                            fontSize = 15.sp,
                                        ),
                                        label = { Text("Row${indexR + 1}") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp)),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = textColor,
                                            disabledContainerColor = Transparent,
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
                                                .heightIn(
                                                    0.dp,
                                                    textFieldSize.height.dp + imagesRowSize.height.dp + 10.dp
                                                )
                                        ) {
                                            TextField(
                                                enabled = false,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .onGloballyPositioned {
                                                        textFieldSize = it.size
                                                    },
                                                value = row.note!!.text!!,
                                                onValueChange = {},
                                                colors = TextFieldDefaults.colors(
                                                    disabledTextColor = textColor,
                                                    disabledContainerColor = Transparent,
                                                    disabledLabelColor = textColor
                                                )
                                            )
                                            if (row.note?.imageUrl?.isNotEmpty() == true) {
                                                LazyRow(
                                                    modifier = Modifier
                                                        .padding(10.dp)
                                                        .fillMaxWidth()
                                                        .wrapContentHeight()
                                                        .onGloballyPositioned {
                                                            imagesRowSize = it.size
                                                        }
                                                ) {

                                                    itemsIndexed(row.note?.imageUrl!!) { indexI, url ->
                                                        AsyncImage(
                                                            model = url,
                                                            modifier = Modifier
                                                                .wrapContentHeight()
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .background(headers_activeElement, RoundedCornerShape(20.dp))
                .padding(5.dp)
        ) {
            IconButton(onClick = {
                coroutineScope.launch {
                    if (currentDetailRow.row != 0) {
                        if (currentDetailRow.row != currentDetailMaxRows) {
                            currentDetailRow =
                                currentDetailRow.copy(row = currentDetailRow.row!! - 1)
                            listLazyStates[currentDetailRow.detail!!]?.let { state ->
                                val itemInfo =
                                    state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == currentDetailRow.row }
                                itemInfo?.let {
                                    val offset = state.layoutInfo.viewportSize.height / 4
                                    state.animateScrollToItem(currentDetailRow.row!!, -offset)
                                }

                            }
                            counterTitle = "Row${currentDetailRow.row!! + 1}"

                        } else {
                            currentDetailRow = currentDetailRow.copy(row = 0)
                            listLazyStates[currentDetailRow.detail!!]?.let { state ->
                                state.animateScrollToItem(currentDetailRow.row!!)
                            }
                            counterTitle = "Row${currentDetailRow.row!! + 1}"

                        }
                    }
                }
            }) {
                Icon(
                    imageVector = if (currentDetailRow.row != currentDetailMaxRows) ImageVector.vectorResource(
                        R.drawable.baseline_remove_24
                    )
                    else Icons.Default.Refresh,
                    contentDescription = null,
                    tint = white
                )
            }
            Text(counterTitle, color = white)

            IconButton(onClick = {
                coroutineScope.launch {
                    if (currentDetailRow.row != currentDetailMaxRows) {
                        currentDetailRow = currentDetailRow.copy(row = currentDetailRow.row!! + 1)
                        listLazyStates[currentDetailRow.detail!!]?.let { state ->
                            val itemInfo =
                                state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == currentDetailRow.row }
                            itemInfo?.let {
                                val offset = state.layoutInfo.viewportSize.height / 4
                                state.animateScrollToItem(currentDetailRow.row!!, -offset)
                            }

                        }
                        counterTitle =
                            if (currentDetailRow.row != currentDetailMaxRows) "Row${currentDetailRow.row!! + 1}"
                            else context.getString((R.string.detailIsDone))

                    } else {
                        if (currentDetailRow.detail != details.size - 1) {
                            currentDetailRow = currentDetailRow.copy(
                                row = 0,
                                detail = currentDetailRow.detail!! + 1
                            )
                            currentDetailMaxRows = details[currentDetailRow.detail!!].rows.size
                            counterTitle = "Row${currentDetailRow.row!! + 1}"
                            pagerState.animateScrollToPage(currentDetailRow.detail!!)
                        } else {
                            counterTitle = context.getString(R.string.projectIsDone)
                        }
                    }
                }
            }) {
                Icon(
                    imageVector = if (currentDetailRow.row != currentDetailMaxRows) Icons.Default.Add
                    else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = white
                )
            }


        }

    }

    if (restartProjectDialog) {
        RestartProjectAlertDialog({
            restartProjectDialog = false
        }) {
            FirebaseDB.startProject(
                project.project!!,
                {
                    navController.apply {
                        popBackStack()
                        navigate("workingOnProject")
                    }
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestartProjectAlertDialog(onDismiss: () -> Unit, onRestart: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.restartProject),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = textColor

                )


                Row(Modifier.wrapContentSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))

                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onRestart()
                        }
                    ) {
                        Text(stringResource(R.string.restart))

                    }
                }
            }
        }
    }
}

fun formatTime(secondsStopWatch: Int): String {
    var hours = secondsStopWatch / 3600
    var minutes = (secondsStopWatch % 3600) / 60
    var secs = secondsStopWatch % 60
    return String
        .format(
            Locale.getDefault(),
            "%02d:%02d:%02d", hours,
            minutes, secs
        )
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }