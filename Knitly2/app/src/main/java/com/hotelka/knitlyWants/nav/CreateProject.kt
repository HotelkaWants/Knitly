package com.hotelka.knitlyWants.nav

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.attafitamim.krop.core.crop.*
import com.attafitamim.krop.ui.ImageCropperDialog
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.Category.Companion.Crocheting
import com.hotelka.knitlyWants.Data.Detail
import com.hotelka.knitlyWants.Data.DetailRows
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.Note
import com.hotelka.knitlyWants.Data.PatternData
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectData
import com.hotelka.knitlyWants.Data.RowCrochet
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.getFileFromUri
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.Tools.distributeDecreases
import com.hotelka.knitlyWants.Tools.distributeIncreases
import com.hotelka.knitlyWants.editableBlog
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.imageBitmapToByteArray
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.CustomFabTools
import com.hotelka.knitlyWants.ui.theme.CustomFloatingActionButton
import com.hotelka.knitlyWants.ui.theme.LoadingAnimation
import com.hotelka.knitlyWants.ui.theme.Shapes
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.error
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun CreateProjectScreen(currentProject: Project? = null, blog_: Blog? = null) {
    val context = LocalContext.current
    var deleteProjectDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(if (currentProject != null) currentProject.projectData!!.title else if (blog_ != null) blog_.projectData!!.title else "") }

    var description by remember { mutableStateOf(if (currentProject != null) currentProject.projectData!!.description else if (blog_ != null) blog_.projectData!!.description else "") }
    var category by remember { mutableStateOf(if (currentProject != null) currentProject.category else if (blog_ != null) blog_.category else Category.Blog) }
    var credits by remember { mutableStateOf(if (currentProject != null) currentProject.credits else if (blog_ != null) blog_.credits else "") }
    var initialImageUri =
        if (currentProject != null) currentProject.projectData?.cover else if (blog_ != null)  blog_.projectData?.cover else ""

    var tool by remember {
        mutableDoubleStateOf(
            if (currentProject != null) currentProject.tool!!.replace(
                "mm",
                ""
            ).toDouble() else 0.0
        )
    }
    var yarns by remember { mutableStateOf(if (currentProject != null) currentProject.yarns!! else "") }
    var expandedCategories by remember { mutableStateOf(false) }
    var expandedSave by remember { mutableStateOf(false) }
    var saveEnabled by remember { mutableStateOf(false) }
    var expandedRows = remember { mutableStateListOf<Boolean>() }
    var expandedSteps by remember { mutableStateOf(true) }
    var creditsInfoExpanded by remember { mutableStateOf(false) }

    var details = remember { mutableStateListOf<Detail>() }
    if (currentProject != null) {
        if (details.isEmpty()) {
            details.addAll(currentProject.details!!)
        }
        if (expandedRows.isEmpty()) {
            currentProject.details!!.forEach { expandedRows.add(true) }
        }
    } else {
        if (details.isEmpty()) {
            details.add(Detail(rows = mutableListOf(RowCrochet())))
        }
        if (expandedRows.isEmpty()) {
            expandedRows.add(true)
        }
    }
    var steps = remember { mutableStateListOf<Detail>() }
    if (currentProject != null) {
        if (steps.isEmpty()) {
            steps.addAll(currentProject.details!!)
        }
    } else {
        if (steps.isEmpty()) {
            steps.add(Detail(rows = mutableListOf(RowCrochet())))
        }
    }

    var lastDetail by remember { mutableStateOf(details.last()) }
    var lastRow by remember { mutableIntStateOf(lastDetail.rows.size) }

    var lastStep by remember { mutableIntStateOf(steps.last().rows.size) }
    var pattern: PatternData? by remember { mutableStateOf(null) }

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    //Blog
    var blogImages = remember { mutableStateListOf<String?>() }
    var blog by remember { mutableStateOf(Blog()) }

    if (blog_ != null) {
        blog = blog_
        if (blogImages.isEmpty()) {
            blogImages.addAll(blog.additionalImages)
        }
    }
    var loading by remember { mutableStateOf(false) }

    val imageCropper = rememberImageCropper()
    val composableScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val cropState = imageCropper.cropState
    var knittingPatternConstructorEnabled by remember { mutableStateOf(false) }
    var patternId by remember { mutableStateOf<String?>(currentProject?.patternId) }

    if (patternId?.isNotEmpty() == true){
        FirebaseDB.getPattern(patternId.toString(), userData.value.userId) {
            pattern = it
            loading = false
        }
    }

    if (cropState != null) ImageCropperDialog(
        state = cropState,
        dialogPadding = PaddingValues(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 80.dp),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { cropState.done(accept = false) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = white)
                    }
                },
                actions = {
                    IconButton(onClick = { cropState.reset() }) {
                        Icon(painterResource(R.drawable.restore), null, tint = white)
                    }
                    IconButton(
                        onClick = { cropState.done(accept = true) },
                        enabled = !cropState.accepted
                    ) {
                        Icon(Icons.Default.Done, null, tint = white)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = textColor)
            )
        }
    )
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                composableScope.launch {
                    val result = imageCropper.crop(
                        uri,
                        context
                    ) // Suspends until user accepts or cancels cropping
                    when (result) {
                        CropResult.Cancelled -> {}
                        is CropError -> {}
                        is CropResult.Success -> {
                            imageBitmap = result.bitmap
                        }
                    }
                }
            }
        }

    var loadingEnabled by remember { mutableStateOf(false) }
    var calculator by remember { mutableStateOf(false) }
    var calculateValue by remember { mutableStateOf(context.getString(R.string.increases)) }

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
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)),
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

                            if (imageBitmap != null) {
                                Image(
                                    bitmap = imageBitmap!!,
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
                                        .clickable(onClick = { launcher.launch("image/*") })
                                )
                            } else if (initialImageUri != "" && initialImageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(initialImageUri),
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
                                        .clickable(onClick = { launcher.launch("image/*") }
                                        )
                                )
                            } else {
                                Image(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(textColor),
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
                                        .clickable(onClick = { launcher.launch("image/*") }
                                        )
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .background(
                                        white,
                                        RoundedCornerShape(20.dp)
                                    ),
                                onClick = {
                                    deleteProjectDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = textColor
                                )
                            }
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(bottom = 5.dp)

                                ) {
                                    item {
                                        Text(
                                            category!!,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier
                                                .clickable {
                                                    expandedCategories = !expandedCategories
                                                }
                                                .padding(start = 16.dp)
                                                .clip(Shapes.small)
                                                .background(Color(250, 241, 235, 210))
                                                .padding(vertical = 6.dp, horizontal = 16.dp)

                                        )
                                    }
                                    items(Category.allCategories - category) { it ->
                                        AnimatedVisibility(
                                            visible = expandedCategories,
                                            enter = fadeIn() + scaleIn(),
                                            exit = fadeOut() + scaleOut()
                                        ) {
                                            Text(
                                                it!!,
                                                fontWeight = FontWeight.Medium,
                                                color = textColor,
                                                modifier = Modifier
                                                    .clickable {
                                                        category = it
                                                        expandedCategories = false
                                                    }
                                                    .padding(start = 6.dp)
                                                    .clip(Shapes.small)
                                                    .background(Color(250, 241, 235, 210))
                                                    .padding(vertical = 6.dp, horizontal = 16.dp)

                                            )
                                        }
                                    }
                                }
                                Box {

                                    TextField(
                                        value = title!!,
                                        onValueChange = { title = it },
                                        maxLines = 1,
                                        modifier = Modifier
                                            .fillMaxWidth()
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
                                            .padding(start = 15.dp, end = 15.dp),
                                        textStyle = TextStyle(fontSize = 18.sp),
                                        colors = TextFieldDefaults.colors(
                                            unfocusedLabelColor = textColor,
                                            unfocusedPlaceholderColor = textColor,
                                            unfocusedContainerColor = Transparent,
                                            focusedContainerColor = Transparent,
                                            focusedTextColor = textColor,
                                            unfocusedTextColor = textColor
                                        ),
                                        label = { Text(stringResource(R.string.title)) },
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Done,
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                keyboard?.hide()
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }

                }
                item {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
                    ) {
                        TextField(
                            value = credits!!,
                            onValueChange = { credits = it },
                            modifier = Modifier
                                .background(textFieldColor)
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = TextFieldDefaults.colors(
                                unfocusedTextColor = textColor,
                                focusedTextColor = textColor,
                                focusedContainerColor = textFieldColor,
                                unfocusedContainerColor = textFieldColor,
                                unfocusedLabelColor = DarkGray,
                                focusedLabelColor = DarkGray
                            ),
                            textStyle = TextStyle(fontSize = 14.sp),
                            label = { Text(stringResource(R.string.credits)) },
                            trailingIcon = {
                                Row {

                                    IconButton(
                                        onClick = {
                                            creditsInfoExpanded = !creditsInfoExpanded
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Info,
                                            contentDescription = "Error",
                                            tint = Gray
                                        )
                                    }

                                }
                            }
                        )
                        Row(Modifier.align(Alignment.TopEnd)) {
                            AnimatedVisibility(
                                visible = creditsInfoExpanded,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut(),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .padding(end = 50.dp)
                                        .background(
                                            white,
                                            RoundedCornerShape(
                                                20.dp
                                            )
                                        )
                                        .wrapContentWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.leaveCredits),
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .background(textFieldColor)
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(20.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor,
                            focusedContainerColor = white,
                            unfocusedContainerColor = white,
                            unfocusedLabelColor = DarkGray,
                            focusedLabelColor = DarkGray
                        ),
                        textStyle = TextStyle(fontSize = 16.sp),
                        label = { Text(stringResource(R.string.description)) }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = category != Category.Blog,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Row(
                            Modifier
                                .background(textFieldColor)
                                .padding(start = 20.dp, end = 10.dp)
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (category == Crocheting) ImageVector.vectorResource(
                                    R.drawable.hook
                                )
                                else ImageVector.vectorResource(R.drawable.kneedles),
                                contentDescription = null,
                                tint = textColor
                            )
                            TextField(
                                value = tool.toString(),
                                onValueChange = {
                                    tool = if (it.isNotEmpty() && it != "0.0") {
                                        it.replace("0.0", "").toDouble()
                                    } else 0.0
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp)),
                                label = { Text(stringResource(R.string.mm)) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(
                                            FocusDirection.Down
                                        )
                                    },
                                ),
                                colors = TextFieldDefaults.colors(
                                    unfocusedLabelColor = textColor,
                                    unfocusedPlaceholderColor = textColor,
                                    unfocusedContainerColor = Transparent,
                                    focusedContainerColor = Transparent,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor
                                ),
                            )
                        }
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = category != Category.Blog,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Row(
                            Modifier
                                .background(textFieldColor)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.yarn),
                                contentDescription = null,
                                tint = textColor
                            )
                            TextField(
                                value = yarns.toString(),
                                onValueChange = { yarns = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp)),
                                label = { Text(stringResource(R.string.yarns)) },
                                colors = TextFieldDefaults.colors(
                                    unfocusedLabelColor = textColor,
                                    unfocusedPlaceholderColor = textColor,
                                    unfocusedContainerColor = Transparent,
                                    focusedContainerColor = Transparent,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(
                                            FocusDirection.Down
                                        )
                                    },
                                )
                            )
                        }
                    }
                }
                when (category) {
                    Crocheting -> {
                        itemsIndexed(details) { indexD, detail ->
                            var isEmpty by remember { mutableStateOf(false) }

                            Column {
                                OutlinedTextField(
                                    value = detail.title!!,
                                    onValueChange = {
                                        details[indexD] = detail.copy(title = it)
                                        isEmpty = it.isEmpty()
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .background(textFieldColor)
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    isError = isEmpty,
                                    label = { Text(stringResource(R.string.detail)) },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = secondary,
                                        unfocusedLabelColor = textColor,
                                        unfocusedPlaceholderColor = textColor,
                                        unfocusedContainerColor = Transparent,
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor
                                    ),
                                    trailingIcon = {
                                        Row {
                                            AnimatedVisibility(
                                                visible = isEmpty,
                                                enter = fadeIn() + scaleIn(),
                                                exit = fadeOut() + scaleOut()
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                    },
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Info,
                                                        contentDescription = "Error",
                                                        tint = error
                                                    )
                                                }
                                            }

                                            if (indexD != 0) {
                                                IconButton(
                                                    onClick = {
                                                        details.removeAt(indexD)
                                                        expandedRows.removeAt(indexD)
                                                    },
                                                ) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(
                                                            R.drawable.baseline_remove_24
                                                        ),
                                                        contentDescription = "Note",
                                                        tint = textColor
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    expandedRows[indexD] =
                                                        !expandedRows[indexD]
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowDropDown,
                                                    contentDescription = null,

                                                    )
                                            }
                                        }
                                    }

                                )

                                detail.rows.forEachIndexed { index, row ->
                                    if (detail == lastDetail) lastRow = index
                                    var repeat by remember { mutableStateOf(index + 2) }
                                    var expanded by remember { mutableStateOf(false) }
                                    var openRepeat by remember {
                                        mutableStateOf(
                                            false
                                        )
                                    }
                                    var images =
                                        remember { mutableStateListOf<String?>() }
                                    row.note?.let {
                                        it.imageUrl.forEach {
                                            if (!images.contains(it)) images.add(it)
                                        }
                                    }
                                    val launcher =
                                        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                            images.add(uri.toString())
                                            details[indexD] = details[indexD].copy(
                                                rows = details[indexD].rows.toMutableList()
                                                    .apply {
                                                        this[index] =
                                                            this[index].copy(
                                                                note = row.note!!.copy(
                                                                    imageUrl = images
                                                                )
                                                            )
                                                    })
                                        }
                                    var isEmpty by remember { mutableStateOf(false) }

                                    var stateBox = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { it ->
                                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                                details[indexD] =
                                                    details[indexD].copy(
                                                        rows = details[indexD].rows.toMutableList()
                                                            .apply {
                                                                removeAt(index)
                                                            })
                                            }

                                            it != SwipeToDismissBoxValue.EndToStart

                                        }
                                    )


                                    SwipeToDismissBox(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .animateContentSize(),
                                        state = stateBox,
                                        enableDismissFromEndToStart = true,
                                        enableDismissFromStartToEnd = false,
                                        backgroundContent = {
                                            if (expandedRows[indexD]) {
                                                if (stateBox.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(error)
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.minimumInteractiveComponentSize(),
                                                            imageVector = Icons.Filled.Delete,
                                                            contentDescription = null,
                                                            tint = white
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                    ) {
                                        if (expandedRows[indexD]) {
                                            Box {
                                                Column(
                                                    modifier = Modifier.background(
                                                        textFieldColor
                                                    )
                                                ) {
                                                    TextField(
                                                        value = row.description!!,
                                                        onValueChange = {
                                                            details[indexD] =
                                                                details[indexD].copy(
                                                                    rows = details[indexD].rows.toMutableList()
                                                                        .apply {
                                                                            this[index] =
                                                                                this[index].copy(
                                                                                    description = it
                                                                                )
                                                                        })
                                                            isEmpty = it.isEmpty()
                                                        },
                                                        textStyle = LocalTextStyle.current.copy(
                                                            fontSize = 15.sp,
                                                        ),
                                                        label = {
                                                            if (isEmpty) {
                                                                Text(
                                                                    "Row${index + 1}* " + stringResource(
                                                                        R.string.emptyRow
                                                                    )
                                                                )
                                                            } else {
                                                                Text("Row${index + 1}")
                                                            }
                                                        },
                                                        isError = isEmpty,
                                                        modifier = Modifier
                                                            .background(textFieldColor)
                                                            .padding(
                                                                horizontal = 5.dp
                                                            )
                                                            .padding(top = 5.dp)
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(20.dp)),
                                                        colors = TextFieldDefaults.colors(
                                                            focusedContainerColor = secondary,
                                                            unfocusedLabelColor = textColor,
                                                            unfocusedPlaceholderColor = textColor,
                                                            unfocusedContainerColor = Transparent,
                                                            focusedTextColor = textColor,
                                                            unfocusedTextColor = textColor
                                                        ),
                                                        trailingIcon = {
                                                            Row {
                                                                AnimatedVisibility(
                                                                    visible = isEmpty,
                                                                    enter = fadeIn() + scaleIn(),
                                                                    exit = fadeOut() + scaleOut()
                                                                ) {
                                                                    IconButton(
                                                                        onClick = {
                                                                        },
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Filled.Info,
                                                                            contentDescription = "Error",
                                                                            tint = error
                                                                        )
                                                                    }
                                                                }

                                                                IconButton(
                                                                    onClick = {
                                                                        expanded =
                                                                            !expanded
                                                                        keyboard?.hide()
                                                                    },
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Filled.MoreVert,
                                                                        contentDescription = "Menu Row",
                                                                        tint = textColor
                                                                    )

                                                                }

                                                            }
                                                        },
                                                        keyboardOptions = KeyboardOptions(
                                                            imeAction = if (index != detail.rows.size - 1) ImeAction.Next
                                                            else ImeAction.Done
                                                        ),
                                                        keyboardActions = KeyboardActions(
                                                            onNext = {
                                                                focusManager.moveFocus(
                                                                    FocusDirection.Down
                                                                )
                                                            },
                                                            onDone = {
                                                                keyboard?.hide()
                                                            }
                                                        )
                                                    )
                                                    if (row.noteAdded) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .wrapContentHeight()
                                                                .background(textFieldColor)
                                                        ) {
                                                            TextField(
                                                                modifier = Modifier
                                                                    .padding(horizontal = 20.dp)
                                                                    .background(textFieldColor)
                                                                    .fillMaxWidth(),
                                                                value = row.note!!.text!!,
                                                                onValueChange = {
                                                                    details[indexD] =
                                                                        details[indexD].copy(
                                                                            rows = details[indexD].rows.toMutableList()
                                                                                .apply {
                                                                                    this[index] =
                                                                                        this[index].copy(
                                                                                            note = row.note!!.copy(
                                                                                                text = it
                                                                                            )
                                                                                        )
                                                                                })

                                                                },
                                                                colors = TextFieldDefaults.colors(
                                                                    focusedContainerColor = textFieldColor,
                                                                    unfocusedLabelColor = textColor,
                                                                    unfocusedPlaceholderColor = textColor,
                                                                    unfocusedContainerColor = basic,
                                                                    focusedTextColor = textColor,
                                                                    unfocusedTextColor = textColor
                                                                )
                                                            )

                                                            LazyRow(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(textFieldColor)
                                                                    .heightIn(
                                                                        0.dp,
                                                                        200.dp
                                                                    )
                                                            ) {
                                                                itemsIndexed(row.note!!.imageUrl) { indexI, uri ->
                                                                    AsyncImage(
                                                                        model = uri,
                                                                        modifier = Modifier
                                                                            .fillMaxSize()
                                                                            .padding(10.dp)
                                                                            .clip(
                                                                                RoundedCornerShape(
                                                                                    20.dp
                                                                                )
                                                                            )
                                                                            .combinedClickable(
                                                                                onClick = {},
                                                                                onLongClick = {
                                                                                    images.removeAt(
                                                                                        indexI
                                                                                    )
                                                                                    details[indexD] =
                                                                                        details[indexD].copy(
                                                                                            rows = details[indexD].rows.toMutableList()
                                                                                                .apply {
                                                                                                    this[index] =
                                                                                                        this[index].copy(
                                                                                                            note = row.note!!.copy(
                                                                                                                imageUrl = images
                                                                                                            )
                                                                                                        )
                                                                                                })
                                                                                }
                                                                            ),
                                                                        contentScale = ContentScale.FillWidth,
                                                                        contentDescription = "Note Image"
                                                                    )
                                                                }
                                                                item {
                                                                    AsyncImage(
                                                                        model = R.drawable.baseline_photo_camera_24,
                                                                        modifier = Modifier
                                                                            .wrapContentHeight()
                                                                            .clip(
                                                                                RoundedCornerShape(
                                                                                    20.dp
                                                                                )
                                                                            )
                                                                            .clickable {
                                                                                launcher.launch(
                                                                                    "image/*"
                                                                                )
                                                                            },
                                                                        colorFilter = ColorFilter.tint(
                                                                            textColor
                                                                        ),
                                                                        contentScale = ContentScale.Fit,
                                                                        contentDescription = "Note Image"
                                                                    )
                                                                }

                                                            }
                                                        }

                                                    }

                                                }
                                                Row(Modifier.align(Alignment.TopEnd)) {
                                                    AnimatedVisibility(
                                                        visible = expanded,
                                                        enter = fadeIn() + scaleIn(),
                                                        exit = fadeOut() + scaleOut(),
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .padding(vertical = 10.dp)
                                                                .padding(end = 50.dp)
                                                                .background(
                                                                    white,
                                                                    RoundedCornerShape(
                                                                        20.dp
                                                                    )
                                                                )
                                                                .wrapContentWidth()
                                                                .padding(12.dp)
                                                        ) {
                                                            Column(
                                                                modifier = Modifier
                                                                    .width(IntrinsicSize.Max)
                                                            ) {

                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .clickable {
                                                                            expanded =
                                                                                false
                                                                            details[indexD] =
                                                                                details[indexD].copy(
                                                                                    rows = details[indexD].rows.toMutableList()
                                                                                        .apply {
                                                                                            this[index] =
                                                                                                this[index].copy(
                                                                                                    noteAdded = !row.noteAdded
                                                                                                )
                                                                                        })

                                                                        },
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Icon(
                                                                        imageVector = ImageVector.vectorResource(
                                                                            if (row.noteAdded) R.drawable.remove_note
                                                                            else R.drawable.add_note
                                                                        ),
                                                                        contentDescription = "Note",
                                                                        tint = textColor
                                                                    )

                                                                    Text(
                                                                        modifier = Modifier
                                                                            .wrapContentWidth()
                                                                            .align(
                                                                                Alignment.CenterVertically
                                                                            )
                                                                            .padding(
                                                                                start = 10.dp
                                                                            ),
                                                                        text = stringResource(
                                                                            if (row.noteAdded) R.string.removeNote
                                                                            else R.string.addNote
                                                                        ),
                                                                        style = MaterialTheme.typography.bodyLarge,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = textColor
                                                                    )

                                                                }
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .padding(top = 12.dp)
                                                                        .clickable {
                                                                            expanded =
                                                                                false
                                                                            openRepeat =
                                                                                true

                                                                        },
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Icon(
                                                                        imageVector = ImageVector.vectorResource(
                                                                            R.drawable.baseline_repeat_24
                                                                        ),
                                                                        contentDescription = "Repeat",
                                                                        tint = textColor
                                                                    )

                                                                    Text(
                                                                        modifier = Modifier
                                                                            .wrapContentWidth()
                                                                            .align(
                                                                                Alignment.CenterVertically
                                                                            )
                                                                            .padding(
                                                                                start = 10.dp
                                                                            ),
                                                                        text = stringResource(
                                                                            R.string.repeatUnitl
                                                                        ),
                                                                        style = MaterialTheme.typography.bodyLarge,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = textColor
                                                                    )

                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (openRepeat) {
                                        RepeatAlertDialog(
                                            onDismiss = { openRepeat = false },
                                            index = index,
                                        ) {
                                            openRepeat =
                                                false
                                            if (repeat > index) {
                                                for (i in index + 1..repeat - 1) {
                                                    details[indexD] =
                                                        details[indexD].copy(
                                                            rows = details[indexD].rows.toMutableList()
                                                                .apply {
                                                                    add(
                                                                        i,
                                                                        row
                                                                    )
                                                                })
                                                }
                                            }
                                        }
                                    }

                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(textFieldColor),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            accent_secondary
                                        ),
                                        onClick = {
                                            details[indexD] = details[indexD].copy(
                                                rows = details[indexD].rows.toMutableList()
                                                    .apply {
                                                        add(
                                                            RowCrochet(
                                                                "",
                                                                Note(
                                                                    "",
                                                                    mutableListOf()
                                                                ),
                                                                false,
                                                                0
                                                            )
                                                        )
                                                    })
                                        }
                                    ) {
                                        Text(stringResource(R.string.addRow))

                                    }
                                    AnimatedVisibility(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .imePadding(),
                                        visible = (indexD == details.size - 1),
                                        enter = fadeIn() + scaleIn(),
                                        exit = fadeOut() + scaleOut()
                                    ) {
                                        Button(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                accent_secondary
                                            ),
                                            onClick = {
                                                details.add(
                                                    Detail(
                                                        "",
                                                        mutableListOf(
                                                            RowCrochet(
                                                                "",
                                                                Note(
                                                                    "",
                                                                    mutableListOf("")
                                                                ),
                                                                false,
                                                                0
                                                            )
                                                        )
                                                    )
                                                )
                                                expandedRows.add(true)
                                            }
                                        ) {
                                            Text(stringResource(R.string.addDetail))

                                        }
                                    }
                                }

                            }
                        }
                    }

                    Category.Knitting -> {
                        item {

                            Column {
                                OutlinedTextField(
                                    value = "",
                                    readOnly = true,
                                    onValueChange = {},
                                    singleLine = true,
                                    modifier = Modifier
                                        .background(textFieldColor)
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    label = { Text(stringResource(R.string.stepByStep)) },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = secondary,
                                        unfocusedLabelColor = textColor,
                                        unfocusedPlaceholderColor = textColor,
                                        unfocusedContainerColor = white,
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor
                                    ),
                                    trailingIcon = {
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    expandedSteps = !expandedSteps
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowDropDown,
                                                    contentDescription = null,

                                                    )
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                )
                                steps.forEachIndexed { indexS, step ->
                                    step.rows.forEachIndexed { index, row ->
                                        lastStep = index
                                        var repeat by remember { mutableIntStateOf(index + 2) }
                                        var expanded by remember { mutableStateOf(false) }
                                        var openRepeat by remember {
                                            mutableStateOf(
                                                false
                                            )
                                        }
                                        var images =
                                            remember { mutableStateListOf<String?>() }
                                        row.note?.let {
                                            it.imageUrl.forEach {
                                                if (!images.contains(it)) images.add(it)
                                            }
                                        }
                                        val launcher =
                                            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                                images.add(uri.toString())
                                                steps[indexS] = steps[indexS].copy(
                                                    rows = steps[indexS].rows.toMutableList()
                                                        .apply {
                                                            this[index] =
                                                                this[index].copy(
                                                                    note = row.note!!.copy(
                                                                        imageUrl = images
                                                                    )
                                                                )
                                                        })
                                            }

                                        var stateBox = rememberSwipeToDismissBoxState(
                                            confirmValueChange = { it ->
                                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                                    steps[indexS] =
                                                        steps[indexS].copy(
                                                            rows = steps[indexS].rows.toMutableList()
                                                                .apply {
                                                                    removeAt(index)
                                                                })
                                                }

                                                it != SwipeToDismissBoxValue.EndToStart

                                            }
                                        )


                                        SwipeToDismissBox(
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .animateContentSize(),
                                            state = stateBox,
                                            enableDismissFromEndToStart = true,
                                            enableDismissFromStartToEnd = false,
                                            backgroundContent = {
                                                if (expandedSteps) {
                                                    if (stateBox.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(error)
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.minimumInteractiveComponentSize(),
                                                                imageVector = Icons.Filled.Delete,
                                                                contentDescription = null,
                                                                tint = white
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                        ) {
                                            if (expandedSteps) {
                                                Box {
                                                    Column(
                                                        modifier = Modifier.background(
                                                            textFieldColor
                                                        )
                                                    ) {
                                                        TextField(
                                                            value = row.description!!,
                                                            onValueChange = {
                                                                steps[indexS] =
                                                                    steps[indexS].copy(
                                                                        rows = steps[indexS].rows.toMutableList()
                                                                            .apply {
                                                                                this[index] =
                                                                                    this[index].copy(
                                                                                        description = it
                                                                                    )
                                                                            })
                                                            },
                                                            textStyle = LocalTextStyle.current.copy(
                                                                fontSize = 15.sp,
                                                            ),
                                                            label = {
                                                                Text("Step${index + 1}")
                                                            },
                                                            modifier = Modifier
                                                                .background(textFieldColor)
                                                                .padding(
                                                                    horizontal = 5.dp
                                                                )
                                                                .padding(top = 5.dp)
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(20.dp)),
                                                            colors = TextFieldDefaults.colors(
                                                                focusedContainerColor = secondary,
                                                                unfocusedLabelColor = textColor,
                                                                unfocusedPlaceholderColor = textColor,
                                                                unfocusedContainerColor = Transparent,
                                                                focusedTextColor = textColor,
                                                                unfocusedTextColor = textColor
                                                            ),
                                                            trailingIcon = {
                                                                Row {
                                                                    IconButton(
                                                                        onClick = {
                                                                            expanded =
                                                                                !expanded
                                                                            keyboard?.hide()
                                                                        },
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Filled.MoreVert,
                                                                            contentDescription = "Menu Row",
                                                                            tint = textColor
                                                                        )

                                                                    }

                                                                }
                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                imeAction = if (index != step.rows.size - 1) ImeAction.Next
                                                                else ImeAction.Done
                                                            ),
                                                            keyboardActions = KeyboardActions(
                                                                onNext = {
                                                                    focusManager.moveFocus(
                                                                        FocusDirection.Down
                                                                    )
                                                                },
                                                                onDone = {
                                                                    keyboard?.hide()
                                                                }
                                                            )
                                                        )
                                                        if (row.noteAdded) {
                                                            Column(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .wrapContentHeight()
                                                                    .background(textFieldColor)
                                                            ) {
                                                                TextField(
                                                                    modifier = Modifier
                                                                        .padding(horizontal = 20.dp)
                                                                        .background(textFieldColor)
                                                                        .fillMaxWidth(),
                                                                    value = row.note!!.text!!,
                                                                    onValueChange = {
                                                                        steps[indexS] =
                                                                            steps[indexS].copy(
                                                                                rows = steps[indexS].rows.toMutableList()
                                                                                    .apply {
                                                                                        this[index] =
                                                                                            this[index].copy(
                                                                                                note = row.note!!.copy(
                                                                                                    text = it
                                                                                                )
                                                                                            )
                                                                                    })

                                                                    },
                                                                    colors = TextFieldDefaults.colors(
                                                                        focusedContainerColor = textFieldColor,
                                                                        unfocusedLabelColor = textColor,
                                                                        unfocusedPlaceholderColor = textColor,
                                                                        unfocusedContainerColor = basic,
                                                                        focusedTextColor = textColor,
                                                                        unfocusedTextColor = textColor
                                                                    )
                                                                )

                                                                LazyRow(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .background(textFieldColor)
                                                                        .heightIn(
                                                                            0.dp,
                                                                            200.dp
                                                                        )
                                                                ) {
                                                                    itemsIndexed(row.note!!.imageUrl) { indexI, uri ->
                                                                        AsyncImage(
                                                                            model = uri,
                                                                            modifier = Modifier
                                                                                .fillMaxSize()
                                                                                .padding(10.dp)
                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        20.dp
                                                                                    )
                                                                                )
                                                                                .combinedClickable(
                                                                                    onClick = {},
                                                                                    onLongClick = {
                                                                                        images.removeAt(
                                                                                            indexI
                                                                                        )
                                                                                        steps[indexS] =
                                                                                            steps[indexS].copy(
                                                                                                rows = steps[indexS].rows.toMutableList()
                                                                                                    .apply {
                                                                                                        this[index] =
                                                                                                            this[index].copy(
                                                                                                                note = row.note!!.copy(
                                                                                                                    imageUrl = images
                                                                                                                )
                                                                                                            )
                                                                                                    })
                                                                                    }
                                                                                ),
                                                                            contentScale = ContentScale.FillWidth,
                                                                            contentDescription = "Note Image"
                                                                        )
                                                                    }
                                                                    item {
                                                                        AsyncImage(
                                                                            model = R.drawable.baseline_photo_camera_24,
                                                                            modifier = Modifier
                                                                                .wrapContentHeight()
                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        20.dp
                                                                                    )
                                                                                )
                                                                                .clickable {
                                                                                    launcher.launch(
                                                                                        "image/*"
                                                                                    )
                                                                                },
                                                                            colorFilter = ColorFilter.tint(
                                                                                textColor
                                                                            ),
                                                                            contentScale = ContentScale.Fit,
                                                                            contentDescription = "Note Image"
                                                                        )
                                                                    }

                                                                }
                                                            }

                                                        }

                                                    }
                                                    Row(Modifier.align(Alignment.TopEnd)) {
                                                        AnimatedVisibility(
                                                            visible = expanded,
                                                            enter = fadeIn() + scaleIn(),
                                                            exit = fadeOut() + scaleOut(),
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .padding(vertical = 10.dp)
                                                                    .padding(end = 50.dp)
                                                                    .background(
                                                                        white,
                                                                        RoundedCornerShape(
                                                                            20.dp
                                                                        )
                                                                    )
                                                                    .wrapContentWidth()
                                                                    .padding(12.dp)
                                                            ) {
                                                                Column(
                                                                    modifier = Modifier
                                                                        .width(IntrinsicSize.Max)
                                                                ) {

                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxSize()
                                                                            .clickable {
                                                                                expanded =
                                                                                    false
                                                                                steps[indexS] =
                                                                                    steps[indexS].copy(
                                                                                        rows = steps[indexS].rows.toMutableList()
                                                                                            .apply {
                                                                                                this[index] =
                                                                                                    this[index].copy(
                                                                                                        noteAdded = !row.noteAdded
                                                                                                    )
                                                                                            })

                                                                            },
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = ImageVector.vectorResource(
                                                                                if (row.noteAdded) R.drawable.remove_note
                                                                                else R.drawable.add_note
                                                                            ),
                                                                            contentDescription = "Note",
                                                                            tint = textColor
                                                                        )

                                                                        Text(
                                                                            modifier = Modifier
                                                                                .wrapContentWidth()
                                                                                .align(
                                                                                    Alignment.CenterVertically
                                                                                )
                                                                                .padding(
                                                                                    start = 10.dp
                                                                                ),
                                                                            text = stringResource(
                                                                                if (row.noteAdded) R.string.removeNote
                                                                                else R.string.addNote
                                                                            ),
                                                                            style = MaterialTheme.typography.bodyLarge,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = textColor
                                                                        )

                                                                    }
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxSize()
                                                                            .padding(top = 12.dp)
                                                                            .clickable {
                                                                                expanded =
                                                                                    false
                                                                                openRepeat =
                                                                                    true

                                                                            },
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = ImageVector.vectorResource(
                                                                                R.drawable.baseline_repeat_24
                                                                            ),
                                                                            contentDescription = "Repeat",
                                                                            tint = textColor
                                                                        )

                                                                        Text(
                                                                            modifier = Modifier
                                                                                .wrapContentWidth()
                                                                                .align(
                                                                                    Alignment.CenterVertically
                                                                                )
                                                                                .padding(
                                                                                    start = 10.dp
                                                                                ),
                                                                            text = stringResource(
                                                                                R.string.repeatUntilStep
                                                                            ),
                                                                            style = MaterialTheme.typography.bodyLarge,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = textColor
                                                                        )

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (openRepeat) {
                                            RepeatAlertDialog(
                                                onDismiss = { openRepeat = false },
                                                index = index,
                                            ) {
                                                openRepeat =
                                                    false
                                                if (repeat > index) {
                                                    for (i in index + 1..repeat - 1) {
                                                        steps[indexS] =
                                                            steps[indexS].copy(
                                                                rows = steps[indexS].rows.toMutableList()
                                                                    .apply {
                                                                        add(
                                                                            i,
                                                                            row
                                                                        )
                                                                    })
                                                    }
                                                }
                                            }
                                        }

                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(textFieldColor),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(4.dp)
                                                .clip(CircleShape)
                                                .fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                accent_secondary
                                            ),
                                            onClick = {
                                                steps[indexS] = steps[indexS].copy(
                                                    rows = steps[indexS].rows.toMutableList()
                                                        .apply {
                                                            add(
                                                                RowCrochet(
                                                                    "",
                                                                    Note(
                                                                        "",
                                                                        mutableListOf()
                                                                    ),
                                                                    false,
                                                                    0
                                                                )
                                                            )
                                                        })
                                            }
                                        ) {
                                            Text(stringResource(R.string.addStep))

                                        }
                                    }
                                }
                            }
                        }

                        item {


                            Column {
                                Button(
                                    onClick = { knittingPatternConstructorEnabled = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = headers_activeElement,
                                        contentColor = white
                                    )
                                ) {
                                    Text(
                                        stringResource(
                                            if (pattern?.gridState?.isNotEmpty() == true) R.string.createNewPattern
                                            else R.string.createPattern
                                        )
                                    )
                                }
                                if (patternId?.isNotEmpty() == true && pattern == null) loading = true
                                if (pattern != null)
                                    PatternGrid(
                                        pattern!!.columns,
                                        pattern!!.gridState.toTypedArray()
                                    ) { }

                            }
                        }
                    }

                    Category.Blog -> {
                        item {
                            val launcher =
                                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                    uri?.let { blogImages.add(it.toString()) }
                                    blog =
                                        blog.copy(additionalImages = blogImages.toMutableList())
                                    Log.d("images", blogImages.toString())
                                }
                            LazyRow(
                                modifier = Modifier
                                    .background(textFieldColor)
                                    .fillMaxWidth()
                                    .heightIn(0.dp, 200.dp)
                            ) {

                                itemsIndexed(blogImages) { indexI, uri ->

                                    AsyncImage(
                                        model = uri,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    20.dp
                                                )
                                            )
                                            .combinedClickable(
                                                onClick = {},
                                                onLongClick = {
                                                    blogImages.removeAt(
                                                        indexI
                                                    )
                                                    blog =
                                                        blog.copy(additionalImages = blogImages.toMutableList())
                                                }
                                            ),
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = "Blog Image"
                                    )
                                }
                                item {
                                    AsyncImage(
                                        model = R.drawable.baseline_photo_camera_24,
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .clip(
                                                RoundedCornerShape(
                                                    20.dp
                                                )
                                            )
                                            .clickable {
                                                launcher.launch("image/*")
                                            },
                                        colorFilter = ColorFilter.tint(
                                            textColor
                                        ),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = "Blog Image"
                                    )
                                }

                            }
                        }

                    }
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )
                }
            }
        }
        val uniqueUUID = UUID.randomUUID().toString()
        fun saveProject() {
            loadingEnabled = true
            if (category == Category.Blog) {
                composableScope.launch {
                    var newImagesList = mutableListOf<String?>()

                    val blog = Blog(
                        credits = credits,
                        category = category,
                        projectData = ProjectData(
                            likes = if (blog_ != null) blog_.projectData!!.likes
                            else Likes(),
                            reviews = if (blog_ != null) blog_.projectData!!.reviews
                            else 0,
                            projectId = if (blog_ != null) blog_.projectData!!.projectId
                            else uniqueUUID,
                            title = title?.trimIndent(),
                            date = if (blog_ != null) blog_.projectData!!.date
                            else System.currentTimeMillis(),
                            description = description,
                            authorID = userData.value.userId,
                            cover = if (imageBitmap != null) getData(
                                "blogs",
                                uploadFile(
                                    "blogs",
                                    imageBitmapToByteArray(imageBitmap!!)
                                ).toString()
                            ) else initialImageUri
                        ),
                        additionalImages = newImagesList.apply {
                            blogImages.forEach { string ->
                                var file: File? = null
                                string?.contains("cxyqsghvgdqyxrjnvrxn.supabase.co")
                                    ?.let {
                                        if (!it) {
                                            file =
                                                getFileFromUri(
                                                    context,
                                                    Uri.parse(string)
                                                )
                                            file?.let {
                                                newImagesList.add(
                                                    getData(
                                                        "projects",
                                                        uploadFile(
                                                            "projects",
                                                            it.readBytes()
                                                        ).toString()
                                                    )
                                                )
                                            }
                                            blog.additionalImages = blogImages
                                        } else newImagesList.add(string)
                                    }
                            }
                        }
                    )
                    if (blog_ != null) {
                        FirebaseDB.updateBlog(blog)
                    } else FirebaseDB.storeBlog(blog, uniqueUUID)
                    try {
                        SupportingDatabase(context).deleteBlogDraft(blog)
                    } catch (_: Exception) {
                    }
                    editableBlog = null

                }

            } else {
                var guide = if (category == Category.Knitting) steps else details
                composableScope.launch {
                    val project = Project(
                        credits = credits,
                        category = category,
                        patternId = if (category == Category.Knitting) patternId else "",
                        projectData = ProjectData(
                            likes = if (currentProject != null) currentProject.projectData!!.likes
                            else Likes(),
                            reviews = if (currentProject != null) currentProject.projectData!!.reviews
                            else 0,
                            projectId = if (currentProject != null) currentProject.projectData!!.projectId
                            else uniqueUUID,
                            title = title?.trimIndent(),
                            date = if (currentProject != null) currentProject.projectData!!.date
                            else System.currentTimeMillis(),
                            description = description,
                            authorID = userData.value.userId,
                            cover = if (imageBitmap != null) getData(
                                "projects",
                                uploadFile(
                                    "projects",
                                    imageBitmapToByteArray(imageBitmap!!)
                                ).toString()
                            ) else initialImageUri,
                        ),
                        tool = "${tool}mm",
                        yarns = yarns,
                        details = guide.apply {
                            this.forEach { detail ->
                                detail.rows.forEach { row ->
                                    if (row.noteAdded) {
                                        var newNoteList = mutableListOf<String?>()
                                        Log.d("image", row.note!!.imageUrl.toString())

                                        row.note?.imageUrl?.forEachIndexed { index, string ->

                                            if (string != null && string != "null" && string != "") {
                                                var file: File? = null
                                                if (!string.contains("cxyqsghvgdqyxrjnvrxn.supabase.co")) {
                                                    file =
                                                        getFileFromUri(
                                                            context,
                                                            Uri.parse(string)
                                                        )
                                                    file?.let {
                                                        newNoteList.add(
                                                            getData(
                                                                "projects",
                                                                uploadFile(
                                                                    "projects",
                                                                    file.readBytes()
                                                                ).toString()
                                                            )
                                                        )
                                                    }
                                                } else newNoteList.add(string)

                                            }
                                        }
                                        row.note?.imageUrl = newNoteList

                                    } else {
                                        row.note = null
                                    }
                                }
                            }
                        })
                    if (currentProject != null) {
                        FirebaseDB.updateProject(project)
                    } else FirebaseDB.storeProject(project, uniqueUUID)
                    try {
                        SupportingDatabase(context).deleteDraft(project)
                    } catch (_: Exception) {
                    }
                    editableProject = null
                }

            }

        }

        fun saveDraft() {
            loadingEnabled = true
            composableScope.launch {
                var guide = if (category == Category.Knitting) steps else details
                when (category) {
                    Category.Blog -> {
                        composableScope.launch {
                            var newImagesList = mutableListOf<String?>()
                            val blog = Blog(
                                credits = credits,
                                category = category,
                                projectData = ProjectData(
                                    likes = if (blog_ != null) blog_.projectData!!.likes
                                    else Likes(),
                                    reviews = if (blog_ != null) blog_.projectData!!.reviews
                                    else 0,
                                    projectId = if (blog_ != null) blog_.projectData!!.projectId
                                    else uniqueUUID,
                                    title = title?.trimIndent(),
                                    date = if (blog_ != null) blog_.projectData!!.date
                                    else System.currentTimeMillis(),
                                    description = description,
                                    authorID = userData.value.userId,
                                    cover = if (imageBitmap != null) getData(
                                        "blogs",
                                        uploadFile(
                                            "blogs",
                                            imageBitmapToByteArray(imageBitmap!!)
                                        ).toString()
                                    ) else initialImageUri

                                ),
                                additionalImages = newImagesList.apply {
                                    blogImages.forEach { imageUri ->
                                        if (imageUri != null && imageUri != "null" && imageUri != "") {
                                            var file: File? = null
                                            if (!imageUri.contains("cxyqsghvgdqyxrjnvrxn.supabase.co")) {
                                                file =
                                                    getFileFromUri(
                                                        context,
                                                        Uri.parse(imageUri)
                                                    )
                                                file?.let {
                                                    newImagesList.add(
                                                        getData(
                                                            "blogs",
                                                            uploadFile(
                                                                "blogs",
                                                                file.readBytes()
                                                            ).toString()
                                                        )
                                                    )
                                                }
                                            } else newImagesList.add(imageUri)

                                        }
                                    }
                                })
                            if (blog_ != null) {
                                SupportingDatabase(context).updateBlogDraft(blog)
                            } else SupportingDatabase(context).addBlogDraft(blog, uniqueUUID)
                            navController.popBackStack()
                        }
                    }

                    else -> {
                        val project = Project(
                            credits = credits,
                            category = category,
                            patternId = if (category == Category.Knitting) patternId else "",
                            projectData = ProjectData(
                                projectId = if (currentProject != null) currentProject.projectData!!.projectId else uniqueUUID,
                                title = title?.trimIndent(),
                                date = System.currentTimeMillis(),
                                description = description,
                                authorID = userData.value.userId,
                                cover = if (imageBitmap != null) getData(
                                    "projects",
                                    uploadFile(
                                        "projects",
                                        imageBitmapToByteArray(imageBitmap!!)
                                    ).toString()
                                ) else initialImageUri,
                            ),
                            tool = "${tool}mm",
                            yarns = yarns,
                            details = guide.apply {
                                this.forEach { detail ->
                                    detail.rows.forEach { row ->
                                        if (row.noteAdded) {
                                            var newNoteList = mutableListOf<String?>()
                                            row.note?.imageUrl?.forEachIndexed { index, imageUri ->
                                                if (imageUri != null && imageUri != "null" && imageUri != "") {
                                                    var file: File? = null
                                                    if (!imageUri.contains("cxyqsghvgdqyxrjnvrxn.supabase.co")) {
                                                        file =
                                                            getFileFromUri(
                                                                context,
                                                                Uri.parse(imageUri)
                                                            )
                                                        file?.let {
                                                            newNoteList.add(
                                                                getData(
                                                                    "projects",
                                                                    uploadFile(
                                                                        "projects",
                                                                        file.readBytes()
                                                                    ).toString()
                                                                )
                                                            )
                                                        }
                                                    } else newNoteList.add(imageUri)

                                                }
                                            }
                                            row.note?.imageUrl = newNoteList
                                        } else {
                                            row.note = null
                                        }
                                    }
                                }
                            })
                        if (currentProject != null) {
                            SupportingDatabase(context).updateDraft(project).toString()
                        } else SupportingDatabase(context).addProjectDraft(project, uniqueUUID)

                        navController.popBackStack()


                    }
                }
            }
        }

        var showAlertDialog by remember { mutableStateOf(false) }

        BackHandler {
            showAlertDialog = true
        }
        if (showAlertDialog) {
            SaveAlertDialog(::saveDraft, { showAlertDialog = false })
        }
        if (deleteProjectDialog) {
            DeleteAlertDialog(
                {
                    if (currentProject != null) {
                        FirebaseDB.deleteProject(
                            currentProject.projectData?.projectId,
                            blog.projectData?.authorID
                        )
                        SupportingDatabase(context).deleteDraft(currentProject)
                    } else {
                        FirebaseDB.deleteBlog(
                            blog.projectData?.projectId,
                            blog.projectData?.authorID
                        )
                        SupportingDatabase(context).deleteBlogDraft(blog)
                    }
                    editableProject = null
                    navController.popBackStack()
                },
                { deleteProjectDialog = false }
            )
        }
        if (category != Category.Blog) {
            Box(
                Modifier
                    .padding(bottom = 60.dp)
                    .animateContentSize()
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                CustomFabTools(
                    expandable = true,
                    onFabClick = {
                        expandedSave = !expandedSave
                    },
                    calculatorDecrease = {
                        calculator = true
                        calculateValue = context.getString(R.string.decreases)
                    },
                    calculatorIncrease = {
                        calculator = true
                        calculateValue = context.getString(R.string.increases)
                    },
                    fabIcon = ImageVector.vectorResource(R.drawable.calculator),
                )
            }
        }
        Box(
            Modifier
                .animateContentSize()
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            CustomFloatingActionButton(
                expandable = true,
                saveProjectEnabled = saveEnabled,
                onFabClick = {
                    saveEnabled =
                        if (title!!.isEmpty() || (imageBitmap == null && initialImageUri == "")) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.provide),
                                Toast.LENGTH_LONG
                            ).show()
                            false
                        } else {
                            true
                        }
                    when (category) {
                        Crocheting -> {
                            details.forEach { detail ->
                                if (detail.title!!.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.untitledDetail),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                detail.rows.forEachIndexed { index, it ->
                                    if (it.description!!.isEmpty()) {
                                        saveEnabled = false
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.emptyRow) + (index + 1) + ": ${detail.title}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    expandedSave = !expandedSave
                },
                saveProject = { saveProject() },
                saveDraft = { saveDraft() },
                fabIcon = Icons.Default.Done,
            )
        }

        if (calculator) {
            Calculator(
                details = details,
                lastRow = lastRow,
                context,
                onCancel = { calculator = false },
                calculateValue = calculateValue
            ) { detailRow, apply ->
                detailRow.detail?.let {
                    details[it] =
                        details[it].copy(
                            rows = details[it].rows.toMutableList()
                                .apply {
                                    detailRow.row?.let { it1 ->
                                        this[it1] =
                                            this[it1].copy(
                                                description = apply
                                            )
                                    }
                                })
                }
            }
        }
        AnimatedVisibility(visible = loadingEnabled) { LoadingAnimation(circleSize = 50.dp) }
        if (knittingPatternConstructorEnabled) KnittingPatternConstructor {
            knittingPatternConstructorEnabled = false; patternId = it
        }
        if (loading) {
            LoadingAnimation()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatAlertDialog(onDismiss: () -> Unit, index: Int, onConfirm: () -> Unit) {
    var repeat by remember { mutableIntStateOf(index + 2) }

    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(
                    16.dp
                )
            ) {
                OutlinedTextField(
                    value = repeat.toString(),
                    onValueChange = {
                        repeat =
                            if (it.isNotEmpty()) {
                                it.toInt()
                            } else {
                                0
                            }
                    },
                    maxLines = 1,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    isError = (repeat < index + 2),
                    label = {
                        if (repeat < index + 2) Text(
                            stringResource(
                                R.string.repeatUnitl
                            ) + "* " + stringResource(
                                R.string.repeatMustBe
                            )
                        )
                        else Text(
                            stringResource(
                                R.string.repeatUnitl
                            )
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedLabelColor = textColor,
                        unfocusedPlaceholderColor = textColor,
                        unfocusedContainerColor = Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    trailingIcon = {
                        Row {
                            AnimatedVisibility(
                                visible = (repeat < index + 2),
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                IconButton(
                                    onClick = {
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Error",
                                        tint = error
                                    )
                                }
                            }

                        }
                    }

                )

                Button(
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        accent_secondary
                    ),
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(
                        stringResource(
                            R.string.confirm
                        )
                    )

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculator(
    details: List<Detail>,
    lastRow: Int,
    context: Context,
    calculateValue: String,
    onCancel: () -> Unit,
    apply: (DetailRows, String) -> Unit
) {
    var errorStitches by remember { mutableStateOf(false) }
    var valuesError by remember { mutableStateOf(false) }
    var detailError by remember { mutableStateOf(false) }
    var rowError by remember { mutableStateOf(false) }

    var detail by remember { mutableStateOf(details.last()) }
    var applyOnRow by remember { mutableIntStateOf(lastRow) }
    var stitchesAmount by remember { mutableIntStateOf(1) }
    var valuesCalculate by remember { mutableIntStateOf(1) }
    var detailInput by remember { mutableStateOf(detail.title) }

    var result by remember { mutableStateOf(context.getString(R.string.result)) }
    BasicAlertDialog(
        onDismissRequest = { onCancel() }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            val options = listOf(
                context.getString(R.string.uniform),
                context.getString(R.string.start),
                context.getString(R.string.end)
            )
            var selectedOptionText by remember { mutableStateOf(options[0]) }

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .animateContentSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.calculate) + " $calculateValue",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        fontSize = 18.sp,
                        color = textColor

                    )
                }

                item {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(8.dp),
                        value = stitchesAmount.toString(),
                        onValueChange = { it ->
                            if (it.toIntOrNull() != null) {
                                stitchesAmount = it.toInt()
                            }
                            errorStitches = stitchesAmount <= 0
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = textFieldColor,
                            unfocusedContainerColor = textFieldColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = textColor,
                            unfocusedLabelColor = textColor,
                            errorContainerColor = error
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = errorStitches,
                        label = {
                            Text(
                                if (errorStitches) stringResource(R.string.currentStitchesAmount) + "* ${
                                    context.getString(
                                        R.string.lessThanZero
                                    )
                                }"
                                else stringResource(R.string.currentStitchesAmount)
                            )
                        }

                    )
                }

                item {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(8.dp),
                        value = valuesCalculate.toString(),
                        onValueChange = { it ->
                            if (it.toIntOrNull() != null) {
                                valuesCalculate = it.toInt()
                            }
                            valuesError = valuesCalculate > stitchesAmount

                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = textFieldColor,
                            unfocusedContainerColor = textFieldColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = textColor,
                            unfocusedLabelColor = textColor,
                            errorContainerColor = error
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = valuesError,
                        label = {
                            Text(
                                if (valuesError) "$calculateValue " +
                                        "${stringResource(R.string.mustBeLess)} $stitchesAmount"
                                else calculateValue
                            )
                        }

                    )
                }
                item {
                    Row(Modifier.padding(vertical = 8.dp, horizontal = 10.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .wrapContentWidth()
                                .weight(1f)
                                .padding(end = 8.dp),
                            value = detailInput.toString(),
                            onValueChange = { it ->
                                detailInput = it
                                if (details.isEmpty()) {
                                    detailError = true
                                } else {
                                    for (it in details) {
                                        detailError = true
                                        if (it.title?.replace(" ", "") == detailInput?.replace(
                                                " ",
                                                ""
                                            )
                                        ) {
                                            detailError = false
                                            detail = it
                                            break
                                        }
                                    }
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = textFieldColor,
                                unfocusedContainerColor = textFieldColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedLabelColor = textColor,
                                unfocusedLabelColor = textColor,
                                errorContainerColor = error
                            ),
                            isError = detailError,
                            label = {
                                Text(
                                    if (detailError) stringResource(R.string.detail) + " ${
                                        stringResource(
                                            R.string.noSuchDetail
                                        )
                                    }* "
                                    else stringResource(R.string.detail)
                                )
                            }
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .wrapContentWidth()
                                .weight(1f),
                            value = applyOnRow.toString(),
                            onValueChange = { it ->
                                if (it.toIntOrNull() != null) {
                                    applyOnRow = it.toInt()
                                }
                                rowError = if (applyOnRow > detail.rows.size) {
                                    true
                                } else if (applyOnRow <= 0) {
                                    true
                                } else {
                                    false
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = textFieldColor,
                                unfocusedContainerColor = textFieldColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedLabelColor = textColor,
                                unfocusedLabelColor = textColor,
                                errorContainerColor = error
                            ),
                            isError = rowError,
                            label = {
                                Text(
                                    if (rowError) "Row* ${stringResource(R.string.noSuchRow)}* "
                                    else "Row"
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),

                            )
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            options[0],
                            fontWeight = FontWeight.Medium,
                            color = if (selectedOptionText == options[0]) white
                            else textColor,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(Shapes.small)
                                .background(
                                    if (selectedOptionText == options[0]) accent_secondary
                                    else secondary
                                )
                                .clickable {
                                    selectedOptionText = options[0]
                                }
                                .padding(vertical = 6.dp, horizontal = 14.dp)

                        )
                        Text(
                            options[1],
                            fontWeight = FontWeight.Medium,
                            color = if (selectedOptionText == options[1]) white
                            else textColor,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(Shapes.small)
                                .background(
                                    if (selectedOptionText == options[1]) accent_secondary
                                    else secondary
                                )
                                .clickable {
                                    selectedOptionText = options[1]
                                }
                                .padding(vertical = 6.dp, horizontal = 14.dp)

                        )
                        Text(
                            options[2],
                            fontWeight = FontWeight.Medium,
                            color = if (selectedOptionText == options[2]) white
                            else textColor,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(Shapes.small)
                                .background(
                                    if (selectedOptionText == options[2]) accent_secondary
                                    else secondary
                                )
                                .clickable {
                                    selectedOptionText = options[2]
                                }
                                .padding(vertical = 6.dp, horizontal = 14.dp)
                        )

                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            if (!errorStitches && !valuesError) {
                                result = if (calculateValue == context.getString(R.string.increases)) {
                                    distributeIncreases(
                                        context, stitchesAmount, valuesCalculate, selectedOptionText
                                    )
                                } else {
                                    distributeDecreases(
                                        context, stitchesAmount, valuesCalculate, selectedOptionText
                                    )
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.calculate))

                    }
                }
                item {
                    Text(
                        text = result,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 5.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        fontSize = 16.sp,
                        color = textColor

                    )
                }

                item {
                    Row(
                        Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                            onClick = {
                                onCancel()
                            }
                        ) {
                            Text(stringResource(R.string.cancel))

                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                if (!errorStitches && !valuesError && !detailError && !rowError && result != context.getString(
                                        R.string.result
                                    )
                                ) {
                                    headers_activeElement
                                } else Gray
                            ),
                            onClick = {
                                if (!errorStitches && !valuesError && !detailError && !rowError && result != context.getString(
                                        R.string.result
                                    )
                                ) {
                                    apply(
                                        DetailRows(details.indexOf(detail), applyOnRow - 1),
                                        result
                                    )
                                    onCancel()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.apply))

                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAlertDialog(onDelete: () -> Unit, onCancel: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onCancel() }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.deleteProject),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = textColor

                )


                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onCancel()
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
                            onDelete()
                        }
                    ) {
                        Text(stringResource(R.string.delete))

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAlertDialog(onSaveDraft: () -> Unit, onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.saveCancel),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = textColor

                )


                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.dontSave))

                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onSaveDraft()
                        }
                    ) {
                        Text(stringResource(R.string.saveDraft))

                    }
                }
            }
        }
    }
}
