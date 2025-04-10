package com.hotelka.knitlyWants.nav

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Cards.CommentItem
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Category
import com.hotelka.knitlyWants.Data.Category.Companion.Crocheting
import com.hotelka.knitlyWants.Data.Comment
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.PatternData
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refBlogs
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjects
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjectsInProgress
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refUsers
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.getFileFromUri
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.Tools.ImageViewer
import com.hotelka.knitlyWants.blogCurrent
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.editableBlog
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.extractUrls
import com.hotelka.knitlyWants.formatNumber
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.projectCurrent
import com.hotelka.knitlyWants.toDateTimeString
import com.hotelka.knitlyWants.ui.theme.LoadingAnimation
import com.hotelka.knitlyWants.ui.theme.Shapes
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import com.hotelka.knitlyWants.userWatching
import com.hotelka.knitlyWants.users
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.text.isNotEmpty

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectOverview(projectData: Project? = null, blog: Blog? = null) {
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    var additionalImagesComments = remember { mutableStateListOf<String?>() }

    var comments by remember {
        mutableStateOf(
            if (projectData != null) projectData.comments!!.values
            else blog?.comments!!.values
        )
    }
    var likes by remember {
        mutableStateOf(
            if (projectData != null) projectData.projectData?.likes
            else blog?.projectData!!.likes
        )
    }
    var likeCount by remember {
        mutableStateOf(
            if (projectData != null) projectData.projectData?.likes?.total
            else blog?.projectData!!.likes.total
        )
    }
    val id =
        if (projectData != null) projectData.projectData?.projectId
        else blog?.projectData?.projectId

    val cover =
        if (projectData != null) projectData.projectData?.cover
        else blog?.projectData?.cover

    val credits =
        if (projectData != null) projectData.credits
        else blog?.credits

    val category =
        if (projectData != null) projectData.category
        else blog?.category

    val title =
        if (projectData != null) projectData.projectData?.title
        else blog?.projectData?.title

    val description =
        if (projectData != null) projectData.projectData?.description
        else blog?.projectData?.description

    val date =
        if (projectData != null) projectData.projectData?.date
        else blog?.projectData?.date

    val authorId =
        if (projectData != null) projectData.projectData?.authorID
        else blog?.projectData?.authorID

    var author = SupportingDatabase(LocalContext.current).getUser(authorId!!)
    var isFollowing by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var imageViewer by remember { mutableStateOf(false) }

    isFollowing = author?.subscribers?.contains(userData.value.userId)!!

    //Knitting
    var pattern: PatternData? by remember { mutableStateOf(null) }
    var patternId = projectData?.patternId
    val tool = projectData?.tool
    val details = projectData?.details
    val steps = projectData?.details
    val yarns = projectData?.yarns
    var expandedRows = remember { mutableStateListOf<Boolean>() }
    details?.forEach { expandedRows.add(false) }

    //Blog
    var additionalImages = (blog?.additionalImages)

    var links by remember { mutableStateOf(extractUrls(credits.toString())) }
    val annotatedCredits = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = textColor,
            ),
        ) { append(credits.toString()) }
        links.forEach { urlData ->
            addLink(
                url = LinkAnnotation.Url(url = urlData.url),
                start = urlData.start,
                end = urlData.end
            )
            addStyle(
                style = SpanStyle(
                    color = headers_activeElement,
                    textDecoration = TextDecoration.Underline
                ),
                start = urlData.start,
                end = urlData.end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = urlData.url,
                start = urlData.start,
                end = urlData.end
            )

        }
    }
    var images = remember { mutableStateListOf<String?>() }
    var startIndex by remember { mutableIntStateOf(0) }

    FirebaseDB.getPattern(patternId.toString(), authorId) {
        pattern = it
        loading = false
    }

    BackHandler {
        if (imageViewer) {
            imageViewer = false
        } else {
            projectCurrent = null
            blogCurrent = null
            navController.popBackStack()
        }
    }
    fun sendComment(onDone: () -> Unit) {
        var additionalImages = mutableListOf<String?>()
        scope.launch {
            var comment = Comment(
                id = UUID.randomUUID().toString(),
                text = comment,
                userId = userData.value.userId,
                timestamp = System.currentTimeMillis(),
                likes = Likes(),
                additionalImages = additionalImages.apply {
                    additionalImagesComments.forEachIndexed { index, uri ->
                        var file: File? = null
                        file = getFileFromUri(context, Uri.parse(uri))
                        file?.let { file ->
                            additionalImages.add(
                                getData(
                                    "comments",
                                    uploadFile(
                                        "comments",
                                        file.readBytes()
                                    ).toString()
                                )
                            )
                        }
                    }
                }
            )
            FirebaseDB.sendComment(
                comment,
                id.toString(),
                if (projectData != null) refProjects else refBlogs
            )
            onDone()
        }
    }

    var delete by remember { mutableStateOf(false) }
    var commentToDelete by remember { mutableStateOf(Comment()) }
    var replyToDelete by remember { mutableStateOf<Comment?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(textFieldColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var lazyListState = rememberLazyListState()
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
                            .background(textFieldColor)
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
                                    .clickable {
                                        images.apply {
                                            clear()
                                            add(cover)
                                            startIndex = 0
                                            imageViewer = true
                                        }
                                    }
                            )
                            if (authorId == userData.value.userId) {
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                        .background(
                                            white,
                                            RoundedCornerShape(20.dp)
                                        ),
                                    onClick = {
                                        if (projectData != null) {
                                            editableProject = projectData; editableBlog = null
                                        } else {
                                            editableProject = null; editableBlog = blog
                                        }

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
                                        .padding(vertical = 15.dp, horizontal = 15.dp),
                                    textStyle = TextStyle(fontSize = 20.sp),
                                    colors = TextFieldDefaults.colors(
                                        disabledTextColor = textColor,
                                        disabledContainerColor = Transparent,
                                        disabledLabelColor = textColor
                                    ),
                                )
                            }

                        }


                    }

                }
                item {
                    if (credits?.isNotEmpty() == true) {
                        ClickableText(
                            text = annotatedCredits,
                            modifier = Modifier
                                .background(textFieldColor)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp)
                                .padding(horizontal = 10.dp),
                            style = LocalTextStyle.current.copy(fontSize = 14.sp),
                            onClick = { offset ->
                                annotatedCredits
                                    .getStringAnnotations("URL", offset, offset)
                                    .firstOrNull()?.let { stringAnnotation ->
                                        Log.d("url", stringAnnotation.item)
                                    }

                            })
                    }
                    if (description?.isNotEmpty() == true) {
                        TextField(
                            enabled = false,
                            value = description,
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
                                value = if (yarns.toString()
                                        .isEmpty()
                                ) stringResource(R.string.optional)
                                else yarns.toString(),
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
                                        Box(Modifier.animateContentSize()) {
                                            if (expandedRows[indexD]) {
                                                Column(
                                                    modifier = Modifier.background(
                                                        textFieldColor
                                                    )
                                                ) {
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
                                                    if (row.noteAdded) {
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
                                                                        AsyncImage(
                                                                            model = url,
                                                                            modifier = Modifier
                                                                                .fillMaxSize()
                                                                                .padding(10.dp)
                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        20.dp
                                                                                    )
                                                                                )
                                                                                .clickable {
                                                                                    startIndex =
                                                                                        indexI
                                                                                    images.apply {
                                                                                        clear()
                                                                                        addAll(row.note!!.imageUrl)
                                                                                    }
                                                                                    imageViewer =
                                                                                        true
                                                                                },
                                                                            contentScale = ContentScale.FillWidth,
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
                        steps?.let {
                            itemsIndexed(it) { indexS, step ->
                                if (!(step.rows.isEmpty() || step.rows.get(0).description?.isEmpty() == true)) {
                                    Column {
                                        var expandedSteps by remember { mutableStateOf(false) }
                                        OutlinedTextField(
                                            enabled = false,
                                            value = stringResource(R.string.stepByStep),
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
                                                disabledLabelColor = textColor
                                            ),
                                            trailingIcon = {
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
                                        )
                                        step.rows.forEachIndexed { index, row ->
                                            Box(Modifier.animateContentSize()) {
                                                if (expandedSteps) {
                                                    Column(
                                                        modifier = Modifier.background(
                                                            textFieldColor
                                                        )
                                                    ) {
                                                        TextField(
                                                            enabled = false,

                                                            value = row.description!!,
                                                            onValueChange = {},
                                                            textStyle = LocalTextStyle.current.copy(
                                                                fontSize = 15.sp,
                                                            ),
                                                            label = { Text("Step${index + 1}") },
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
                                                        if (row.noteAdded) {
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
                                                                            AsyncImage(
                                                                                model = url,
                                                                                modifier = Modifier
                                                                                    .fillMaxSize()
                                                                                    .padding(10.dp)
                                                                                    .clip(
                                                                                        RoundedCornerShape(
                                                                                            20.dp
                                                                                        )
                                                                                    )
                                                                                    .clickable {
                                                                                        startIndex =
                                                                                            indexI
                                                                                        images.apply {
                                                                                            clear()
                                                                                            addAll(
                                                                                                row.note!!.imageUrl
                                                                                            )
                                                                                        }
                                                                                        imageViewer =
                                                                                            true
                                                                                    },
                                                                                contentScale = ContentScale.FillWidth,
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

                        item {
                            if (patternId != "" && pattern == null) loading = true
                            if (pattern != null)
                                PatternGrid(
                                    pattern!!.columns,
                                    pattern!!.gridState.toTypedArray()
                                ) { }
                        }
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
                                    AsyncImage(
                                        model = url,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    20.dp
                                                )
                                            )
                                            .clickable {
                                                startIndex = indexI
                                                images.apply {
                                                    clear()
                                                    addAll(additionalImages)
                                                }
                                                imageViewer = true
                                            },
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = "Blog Image"
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(textFieldColor)
                            .padding(end = 20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(R.drawable.reviews),
                            contentDescription = "Reviews",
                            Modifier
                                .size(30.dp),
                            colorFilter = ColorFilter.tint(textColor)
                        )
                        Text(
                            text = formatNumber(
                                if (projectData != null) projectData.projectData!!.reviews
                                else blog!!.projectData!!.reviews
                            ),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Image(
                            imageVector = if (likes?.users?.contains(
                                    userData.value.userId
                                ) == true
                            ) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            Modifier
                                .size(30.dp)
                                .padding(start = 5.dp)
                                .clickable {
                                    likes?.let {
                                        FirebaseDB.sendLikeBlog(
                                            blog?.projectData!!.projectId.toString(),
                                            it
                                        ) { sent, like ->
                                            likes = like
                                            likeCount = if (sent) {
                                                likeCount!! + 1

                                            } else {
                                                likeCount!! - 1

                                            }

                                        }
                                    }
                                },
                            colorFilter = if (likes?.users?.contains(
                                    userData.value.userId
                                ) == true
                            ) ColorFilter.tint(headers_activeElement)
                            else ColorFilter.tint(Color.Gray)
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
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                userWatching = author
                                navController.navigate("userProfile")
                            }
                            .background(textFieldColor)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = if (author.profilePictureUrl != "") author.profilePictureUrl
                            else R.drawable.baseline_account_circle_24,
                            contentDescription = "Author Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = author.username!!,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = date!!.toDateTimeString(),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        if (authorId != userData.value.userId) {
                            Button(
                                onClick = {
                                    FirebaseDB.subscribe(authorId, isFollowing) {
                                        isFollowing = !isFollowing
                                        val db = SupportingDatabase(context)
                                        refUsers.get().addOnSuccessListener { snapshot ->
                                            for (user in snapshot.children) {
                                                user.getValue(UserData::class.java)?.let {
                                                    db.addUser(it)
                                                }
                                            }
                                            users.value = db.getAllUsers()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFollowing == true) Color.LightGray else headers_activeElement,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .height(36.dp)
                                    .wrapContentWidth()
                            ) {
                                Text(
                                    text = if (isFollowing == true) stringResource(R.string.unsubscribe) else stringResource(
                                        R.string.subscribe
                                    ),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                if (projectData?.category == Category.Crocheting) {
                    item {
                        var started by remember { mutableStateOf(context.getString(R.string.started)) }
                        refProjectsInProgress.child(userData.value.userId)
                            .child(projectData.projectData!!.projectId!!).get()
                            .addOnSuccessListener {
                                started = if (it.exists()) context.getString(R.string.started)
                                else context.getString(R.string.startProject)
                            }
                        var project by remember {
                            mutableStateOf<ProjectsArchive>(
                                ProjectsArchive()
                            )
                        }
                        FirebaseDB.getProjectInProgress(
                            projectData.projectData.projectId
                        ){
                            project = it
                        }
                        Button(
                            onClick = {

                                if (started == context.getString(R.string.started)) {
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
                                    if (started == context.getString(R.string.started)) {
                                        if (project.progress != 1f) R.string.continueWorking else R.string.projectIsDone
                                    } else R.string.startProject
                                )
                            )
                        }
                    }
                }

                item {
                    Column(Modifier.background(textFieldColor)) {
                        val launcher =
                            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                uri?.let { additionalImagesComments.add(it.toString()) }
                            }
                        Row(
                            Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = if (userData.value.profilePictureUrl != "") userData.value.profilePictureUrl
                                else R.drawable.baseline_account_circle_24,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 15.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.Top),
                                contentScale = ContentScale.Crop

                            )
                            TextField(
                                value = comment,
                                onValueChange = { comment = it },
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 15.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)),
                                textStyle = TextStyle(fontSize = 16.sp),
                                label = { Text(text = stringResource(R.string.leaveComment)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = white,
                                    unfocusedContainerColor = white,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    focusedLabelColor = textColor,
                                    unfocusedLabelColor = textColor
                                ),
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = {
                                            if (comment.isNotEmpty()) {
                                                sendComment {
                                                    comment = ""
                                                    additionalImagesComments.clear()
                                                    FirebaseDB.getComments(
                                                        if (projectData != null) refProjects else refBlogs,
                                                        id.toString()
                                                    ) { comments = it }
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = null,
                                                tint = if (comment.isNotEmpty()) headers_activeElement else Color.Gray
                                            )
                                        }

                                        IconButton(onClick = {
                                            launcher.launch("image/*")
                                        }) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.attach),
                                                contentDescription = null,
                                                tint = headers_activeElement
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(0.dp, 200.dp)
                                .padding(start = 50.dp)
                        ) {
                            itemsIndexed(additionalImagesComments) { index, uri ->
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
                                                additionalImagesComments.removeAt(
                                                    index
                                                )
                                            }
                                        ),
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = "Comment Image"
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (comments.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .background(textFieldColor)
                                .fillMaxWidth()
                                .padding(top = 50.dp),
                            text = stringResource(R.string.noComments),
                            textAlign = TextAlign.Center,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    } else {

                        Column(
                            Modifier
                                .background(textFieldColor)
                                .animateContentSize()
                        ) {
                            comments.sortedByDescending { it.timestamp }
                                .forEachIndexed { index, comment ->
                                    var toReply by remember { mutableStateOf(false) }

                                    val context = LocalContext.current
                                    val scope = rememberCoroutineScope()
                                    var reply by remember { mutableStateOf("") }
                                    var additionalImagesComments =
                                        remember { mutableStateListOf<String?>() }

                                    fun sendReply(onDone: () -> Unit) {
                                        var additionalImages = mutableListOf<String?>()
                                        scope.launch {
                                            var comment_ = Comment(
                                                id = UUID.randomUUID().toString(),
                                                text = reply,
                                                userId = userData.value.userId,
                                                timestamp = System.currentTimeMillis(),
                                                likes = Likes(),
                                                additionalImages = additionalImages.apply {
                                                    additionalImagesComments.forEachIndexed { index, uri ->
                                                        var file: File? = null
                                                        file =
                                                            getFileFromUri(context, Uri.parse(uri))
                                                        file?.let { file ->
                                                            additionalImages.add(
                                                                getData(
                                                                    "comments",
                                                                    uploadFile(
                                                                        "comments",
                                                                        file.readBytes()
                                                                    ).toString()
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            )
                                            FirebaseDB.sendCommentReply(
                                                comment_,
                                                id.toString(),
                                                comment.id.toString(),
                                                if (projectData != null) refProjects else refBlogs
                                            )
                                            onDone()
                                        }
                                    }

                                    var user by remember { mutableStateOf<UserData>(UserData()) }
                                    FirebaseDB.getUser(comment.userId.toString()) { user = it }
                                    CommentItem(
                                        postType = if (projectData != null) projectData else blog,
                                        postId = id.toString(),
                                        commentId = comment.id.toString(),
                                        userData = user,
                                        commentText = comment.text.toString(),
                                        timestamp = comment.timestamp!!.toDateTimeString(),
                                        likes = comment.likes,
                                        additionalImages = comment.additionalImages!!,
                                        onImageClick = {
                                            images.apply {
                                                comment.additionalImages?.let {
                                                    clear(); images.addAll(
                                                    it
                                                ); imageViewer = true
                                                }
                                            }
                                        },
                                        callDelete = { delete = true; commentToDelete = comment },
                                        onReply = { toReply = it }
                                    )
                                    if (toReply) {
                                        Column(
                                            Modifier
                                                .background(textFieldColor)
                                                .padding(bottom = 5.dp)
                                        ) {
                                            val launcher =
                                                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                                                    uri?.let { additionalImagesComments.add(it.toString()) }
                                                }
                                            Row(
                                                Modifier
                                                    .padding(top = 5.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                AsyncImage(
                                                    model = if (userData.value.profilePictureUrl != "") userData.value.profilePictureUrl
                                                    else R.drawable.baseline_account_circle_24,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .align(Alignment.Top),
                                                    contentScale = ContentScale.Crop

                                                )
                                                Spacer(Modifier.width(8.dp))
                                                TextField(
                                                    value = reply,
                                                    onValueChange = { reply = it },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(
                                                            RoundedCornerShape(
                                                                topEnd = 20.dp,
                                                                topStart = 20.dp
                                                            )
                                                        ),
                                                    textStyle = TextStyle(fontSize = 16.sp),
                                                    label = { Text(text = stringResource(R.string.leaveComment)) },
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = white,
                                                        unfocusedContainerColor = white,
                                                        focusedTextColor = textColor,
                                                        unfocusedTextColor = textColor,
                                                        focusedLabelColor = textColor,
                                                        unfocusedLabelColor = textColor
                                                    ),
                                                    trailingIcon = {
                                                        Row {
                                                            IconButton(onClick = {
                                                                if (reply.isNotEmpty()) {
                                                                    sendReply {
                                                                        keyboard?.hide()
                                                                        toReply = false
                                                                        reply = ""
                                                                        additionalImagesComments.clear()
                                                                        FirebaseDB.getComments(
                                                                            if (projectData != null) refProjects else refBlogs,
                                                                            id.toString()
                                                                        ) { comments = it }
                                                                    }
                                                                }
                                                            }) {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                                                    contentDescription = null,
                                                                    tint = if (reply.isNotEmpty()) headers_activeElement else Color.Gray
                                                                )
                                                            }

                                                            IconButton(onClick = {
                                                                launcher.launch("image/*")
                                                            }) {
                                                                Icon(
                                                                    imageVector = ImageVector.vectorResource(
                                                                        R.drawable.attach
                                                                    ),
                                                                    contentDescription = null,
                                                                    tint = headers_activeElement
                                                                )
                                                            }
                                                        }
                                                    }
                                                )
                                            }
                                            LazyRow(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(0.dp, 200.dp)
                                            ) {
                                                itemsIndexed(additionalImagesComments) { index, uri ->
                                                    AsyncImage(
                                                        model = uri,
                                                        modifier = Modifier
                                                            .padding(start = 8.dp)
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
                                                                    additionalImagesComments.removeAt(
                                                                        index
                                                                    )
                                                                }
                                                            ),
                                                        contentScale = ContentScale.FillWidth,
                                                        contentDescription = "Comment Image"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    comment.replies!!.values.sortedBy { it.timestamp }
                                        .forEach { replyComment ->
                                            var toReply by remember { mutableStateOf(false) }

                                            Column(
                                                modifier = Modifier
                                                    .padding(start = 40.dp)
                                                    .animateContentSize()
                                            ) {
                                                var user by remember {
                                                    mutableStateOf<UserData>(
                                                        UserData()
                                                    )
                                                }
                                                FirebaseDB.getUser(replyComment.userId.toString()) {
                                                    user = it
                                                }
                                                CommentItem(
                                                    parentCommentId = comment.id.toString(),
                                                    isReply = true,
                                                    postType = if (projectData != null) projectData else blog,
                                                    postId = id.toString(),
                                                    commentId = replyComment.id!!,
                                                    userData = user,
                                                    commentText = replyComment.text!!,
                                                    timestamp = replyComment.timestamp!!.toDateTimeString(),
                                                    likes = replyComment.likes,
                                                    additionalImages = replyComment.additionalImages!!,
                                                    onImageClick = {
                                                        images.apply {
                                                            replyComment.additionalImages?.let {
                                                                clear(); images.addAll(
                                                                it
                                                            ); imageViewer = true
                                                            }
                                                        }
                                                    },
                                                    callDelete = {
                                                        delete = true; commentToDelete =
                                                        comment; replyToDelete = replyComment
                                                    },
                                                    onReply = { toReply = it }
                                                )
                                                if (toReply) {
                                                    Column(
                                                        Modifier
                                                            .background(textFieldColor)
                                                            .padding(bottom = 5.dp)
                                                    ) {
                                                        val launcher =
                                                            rememberLauncherForActivityResult(
                                                                contract = ActivityResultContracts.GetContent()
                                                            ) { uri ->
                                                                uri?.let {
                                                                    additionalImagesComments.add(
                                                                        it.toString()
                                                                    )
                                                                }
                                                            }
                                                        Row(
                                                            Modifier
                                                                .fillMaxWidth()
                                                        ) {
                                                            AsyncImage(
                                                                model = if (userData.value.profilePictureUrl != "") userData.value.profilePictureUrl
                                                                else R.drawable.baseline_account_circle_24,
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(40.dp)
                                                                    .clip(CircleShape)
                                                                    .align(Alignment.Top),
                                                                contentScale = ContentScale.Crop

                                                            )
                                                            Spacer(Modifier.width(8.dp))

                                                            TextField(
                                                                value = reply,
                                                                onValueChange = { reply = it },
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clip(
                                                                        RoundedCornerShape(
                                                                            topEnd = 20.dp,
                                                                            topStart = 20.dp
                                                                        )
                                                                    ),
                                                                textStyle = TextStyle(fontSize = 16.sp),
                                                                label = {
                                                                    Text(
                                                                        text = stringResource(
                                                                            R.string.leaveComment
                                                                        )
                                                                    )
                                                                },
                                                                colors = TextFieldDefaults.colors(
                                                                    focusedContainerColor = white,
                                                                    unfocusedContainerColor = white,
                                                                    focusedTextColor = textColor,
                                                                    unfocusedTextColor = textColor,
                                                                    focusedLabelColor = textColor,
                                                                    unfocusedLabelColor = textColor
                                                                ),
                                                                trailingIcon = {
                                                                    Row {
                                                                        IconButton(onClick = {
                                                                            if (reply.isNotEmpty()) {
                                                                                keyboard?.hide()
                                                                                toReply = false
                                                                                sendReply {
                                                                                    reply = ""
                                                                                    additionalImagesComments.clear()
                                                                                    FirebaseDB.getComments(
                                                                                        if (projectData != null) refProjects else refBlogs,
                                                                                        id.toString()
                                                                                    ) {
                                                                                        comments =
                                                                                            it
                                                                                    }
                                                                                }
                                                                            }
                                                                        }) {
                                                                            Icon(
                                                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                                                contentDescription = null,
                                                                                tint = if (reply.isNotEmpty()) headers_activeElement else Color.Gray
                                                                            )
                                                                        }

                                                                        IconButton(onClick = {
                                                                            launcher.launch("image/*")
                                                                        }) {
                                                                            Icon(
                                                                                imageVector = ImageVector.vectorResource(
                                                                                    R.drawable.attach
                                                                                ),
                                                                                contentDescription = null,
                                                                                tint = headers_activeElement
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            )
                                                        }
                                                        LazyRow(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .heightIn(0.dp, 200.dp)
                                                        ) {
                                                            itemsIndexed(additionalImagesComments) { index, uri ->
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
                                                                                additionalImagesComments.removeAt(
                                                                                    index
                                                                                )
                                                                            }
                                                                        ),
                                                                    contentScale = ContentScale.FillWidth,
                                                                    contentDescription = "Comment Image"
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
                item {
                    Spacer(modifier = Modifier.height(200.dp))
                }

            }
        }
        if (loading) {
            LoadingAnimation()
        }
        AnimatedVisibility(
            visible = imageViewer,
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
            ImageViewer(images.toList(), startIndex = startIndex) { imageViewer = false }
        }


        if (delete) DeleteComment(onCancel = { delete = false }) {
            if (replyToDelete != null) {
                FirebaseDB.deleteCommentReply(
                    comment = commentToDelete,
                    typeDbRef = if (projectData != null) refProjects else refBlogs,
                    replyId = replyToDelete!!.id.toString(),
                    id = if (projectData != null) projectData.projectData?.projectId.toString() else blog?.projectData?.projectId.toString()
                ) {
                    FirebaseDB.getComments(
                        if (projectData != null) refProjects else refBlogs,
                        id.toString()
                    ) { comments = it }
                }
                replyToDelete = null
            } else {
                FirebaseDB.deleteComment(
                    comment = commentToDelete,
                    typeDbRef = if (projectData != null) refProjects else refBlogs,
                    id = if (projectData != null) projectData.projectData?.projectId.toString() else blog?.projectData?.projectId.toString()
                ) {
                    FirebaseDB.getComments(
                        if (projectData != null) refProjects else refBlogs,
                        id.toString()
                    ) { comments = it }
                }
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteComment(onCancel: () -> Unit, onDelete: () -> Unit) {
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
                    text = stringResource(R.string.deleteComment),
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
                            onCancel()
                        }
                    ) {
                        Text(stringResource(R.string.delete))

                    }
                }
            }
        }
    }
}
