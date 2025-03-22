package com.hotelka.knitlyWants.nav

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.graphics.asAndroidBitmap
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
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.Note
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectData
import com.hotelka.knitlyWants.Data.RowCrochet
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.getFileFromUri
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.SupportingDatabase.RoomDatabase
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.imageBitmapToByteArray
import com.hotelka.knitlyWants.navController
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
import org.apache.commons.lang3.RandomStringUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun CreateProjectScreen(currentProject: Project? = null, blog_: Blog? = null) {
    val context = LocalContext.current
    var deleteProjectDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(if (currentProject != null) currentProject.projectData!!.title else if (blog_ != null) blog_.projectData!!.title else "") }
    var description by remember { mutableStateOf(if (currentProject != null) currentProject.projectData!!.description else if (blog_ != null) blog_.projectData!!.description else "") }
    var category by remember { mutableStateOf(if (currentProject != null) currentProject.category else Category.Blog) }
    var credits by remember { mutableStateOf(if (currentProject != null) currentProject.credits else if (blog_ != null) blog_.credits else "") }
    var initialImageUri =
        if (currentProject != null) currentProject.projectData?.cover else blog_?.projectData?.cover

    var tool by remember {
        mutableStateOf(
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

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    //Blog
    var blogImages = remember { mutableStateListOf<String?>() }
    var blog by remember { mutableStateOf(Blog()) }

    if (blog_ != null) {
        blog = blog_
        blogImages.addAll(blog.additionalImages)
    }
    val imageCropper = rememberImageCropper()
    val composableScope = rememberCoroutineScope()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val cropState = imageCropper.cropState
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
                            }
                            else {
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
                                TextField(
                                    value = title!!,
                                    onValueChange = { title = it },
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
                        Box {
                            TextField(
                                value = credits!!,
                                onValueChange = { credits = it },
                                modifier = Modifier
                                    .background(textFieldColor)
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(20.dp)),
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
                                imageVector = if (category == Category.Crocheting) ImageVector.vectorResource(
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

                                        ) {

                                            Box() {
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
                                                    AnimatedVisibility(
                                                        visible = row.noteAdded,
                                                        enter = fadeIn() + scaleIn(),
                                                        exit = fadeOut() + scaleOut()
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .wrapContentHeight()
                                                                .background(basic)
                                                        ) {
                                                            TextField(
                                                                modifier = Modifier
                                                                    .padding(horizontal = 20.dp)
                                                                    .background(basic)
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
                                                                            .animateEnterExit(
                                                                                exit = fadeOut() + scaleOut()
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
                                                if (openRepeat) {
                                                    BasicAlertDialog(
                                                        onDismissRequest = {
                                                            openRepeat = false
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
                                            .padding(4.dp),
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

                    }

                    Category.Blog -> {
                        item {
                            val launcher =
                                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                    uri?.let { blogImages.add(it.toString()) }
                                    blog =
                                        blog.copy(additionalImages = blogImages.toMutableList())
                                }
                            LazyRow(
                                modifier = Modifier
                                    .background(textFieldColor)
                                    .fillMaxWidth()
                                    .heightIn(0.dp, 200.dp)
                            ) {

                                itemsIndexed(blog.additionalImages) { indexI, uri ->

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
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy ")
            val formatted = now.format(formatter)
            when (category) {
                Crocheting -> {

                    composableScope.launch {
                        val project = Project(
                            credits = credits,
                            category = category,
                            projectData = ProjectData(
                                likes = if (currentProject != null) currentProject.projectData!!.likes
                                else Likes(),
                                reviews = if (currentProject != null) currentProject.projectData!!.reviews
                                else 0,
                                projectId = if (currentProject != null) currentProject.projectData!!.projectId
                                else uniqueUUID,
                                title = title,
                                date = if (currentProject != null) currentProject.projectData!!.date
                                else formatted,
                                description = description,
                                author = userData.value.username!!,
                                authorID = userData.value.userId,
                                cover = if (imageBitmap != null) getData(
                                    "projects",
                                    uploadFile(
                                        "projects",
                                        userData.value.username!!,
                                        imageBitmapToByteArray(imageBitmap!!)
                                    ).toString()
                                ) else initialImageUri
                            ),
                            tool = "${tool}mm",
                            yarns = yarns,
                            details = details.apply {
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
                                                                        userData.value.username!!,
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
                        } else FirebaseDB.storeProjectCrocheting(project, uniqueUUID)
                        try {
                            RoomDatabase(context).deleteDraft(project)
                        } catch (e: Exception) {
                        }
                    }
                }

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
                                title = title,
                                date = if (blog_ != null) blog_.projectData!!.date
                                else formatted,
                                description = description,
                                author = userData.value.username!!,
                                authorID = userData.value.userId,
                                cover = if (imageBitmap != null) getData(
                                    "blogs",
                                    uploadFile(
                                        "blogs",
                                        userData.value.username!!,
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
                                                                userData.value.username!!,
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
                            RoomDatabase(context).deleteBlogDraft(blog)
                        } catch (e: Exception) {
                        }

                    }

                }
            }

        }

        fun saveDraft() {
            loadingEnabled = true
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy ")
            val formatted = now.format(formatter)
            var file = context.getDir("Covers", Context.MODE_PRIVATE)
            file = File(file, "${RandomStringUtils.randomAlphanumeric(15)}.jpg")

            imageBitmap?.let {
                val stream = ByteArrayOutputStream()
                it.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream) // Use PNG or JPEG format
                val byteArray: ByteArray = stream.toByteArray()
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(byteArray)
                }
                Log.i("ImageSaving", file.absolutePath)

            }
            when (category) {
                Crocheting -> {

                    val project = Project(
                        credits = credits,
                        category = category,
                        projectData = ProjectData(
                            projectId = if (currentProject != null) currentProject.projectData!!.projectId else uniqueUUID,
                            title = title,
                            date = formatted,
                            description = description,
                            author = userData.value.username!!,
                            authorID = userData.value.userId,
                            cover = if (imageBitmap != null)
                                file.absolutePath
                            else initialImageUri
                        ),
                        tool = "${tool}mm",
                        yarns = yarns,
                        details = details.apply {
                            this.forEach { detail ->
                                detail.rows.forEach { row ->
                                    if (row.noteAdded) {
                                        var newNoteList = mutableListOf<String?>()
                                        row.note?.imageUrl?.forEachIndexed { index, string ->
                                            if (string != null && string != "null" && string != "") {
                                                var file =
                                                    context.getDir(
                                                        "Covers",
                                                        Context.MODE_PRIVATE
                                                    )
                                                file = File(
                                                    file,
                                                    "${
                                                        RandomStringUtils.randomAlphanumeric(
                                                            15
                                                        )
                                                    }.jpg"
                                                )
                                                val out = FileOutputStream(file)
                                                val bitmap =
                                                    MediaStore.Images.Media.getBitmap(
                                                        context.contentResolver,
                                                        Uri.parse(string)
                                                    );
                                                bitmap.compress(
                                                    Bitmap.CompressFormat.JPEG,
                                                    85,
                                                    out
                                                )
                                                out.flush()
                                                out.close()
                                                newNoteList.add(file.absolutePath)
                                                row.note?.imageUrl = newNoteList
                                            }
                                        }
                                    } else {
                                        row.note = null
                                    }
                                }
                            }
                        })
                    if (currentProject != null) {

                        Log.d(
                            "ImageSaving",
                            RoomDatabase(context).updateDraft(project).toString()
                        )
                    } else RoomDatabase(context).addProjectDraft(project, uniqueUUID)

                    navController.popBackStack()


                }

                Category.Blog -> {
                    composableScope.launch {
                        var newImagesList = mutableListOf<String?>()
                        val blog = Blog(
                            credits = credits,
                            category = category,
                            projectData = ProjectData(
                                likes = if (currentProject != null) currentProject.projectData!!.likes
                                else Likes(),
                                reviews = if (currentProject != null) currentProject.projectData!!.reviews
                                else 0,
                                projectId = if (currentProject != null) currentProject.projectData!!.projectId
                                else uniqueUUID,
                                title = title,
                                date = if (currentProject != null) currentProject.projectData!!.date
                                else formatted,
                                description = description,
                                author = userData.value.username!!,
                                authorID = userData.value.userId,
                                cover = if (imageBitmap != null)
                                    file.absolutePath
                                else initialImageUri
                            ),
                            additionalImages = newImagesList.apply {
                                blogImages.forEach { imageUri ->
                                    if (imageUri != null && imageUri != "null" && imageUri != "") {
                                        var file =
                                            context.getDir("Covers", Context.MODE_PRIVATE)
                                        file = File(
                                            file,
                                            "${RandomStringUtils.randomAlphanumeric(15)}.jpg"
                                        )
                                        val out = FileOutputStream(file)
                                        val bitmap = MediaStore.Images.Media.getBitmap(
                                            context.contentResolver,
                                            Uri.parse(imageUri)
                                        );
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                                        out.flush()
                                        out.close()
                                        newImagesList.add(file.absolutePath)

                                    }
                                }
                            })
                        if (blog_ != null) {
                            RoomDatabase(context).updateBlogDraft(blog)
                        } else RoomDatabase(context).addBlogDraft(blog, uniqueUUID)
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
            SaveAlertDialog(::saveDraft)
        }
        if (deleteProjectDialog) {
            DeleteAlertDialog(
                {
                    if (currentProject != null) {
                        FirebaseDB.deleteProject(
                            currentProject.projectData?.projectId,
                            blog.projectData?.authorID
                        )
                        RoomDatabase(context).deleteDraft(currentProject)
                    } else {
                        FirebaseDB.deleteBlog(
                            blog.projectData?.projectId,
                            blog.projectData?.authorID
                        )
                        RoomDatabase(context).deleteBlogDraft(blog)
                    }
                    editableProject = null
                    navController.popBackStack()
                },
                { deleteProjectDialog = false }
            )
        }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            CustomFloatingActionButton(
                expandable = true,
                saveProjectEnabled = saveEnabled,
                onFabClick = {
                    saveEnabled = if (title!!.isEmpty() || (imageBitmap == null && initialImageUri == "") ){
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
                        Category.Crocheting -> {
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


        AnimatedVisibility(visible = loadingEnabled) { LoadingAnimation(circleSize = 50.dp) }

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
fun SaveAlertDialog(onSaveDraft: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { }
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
