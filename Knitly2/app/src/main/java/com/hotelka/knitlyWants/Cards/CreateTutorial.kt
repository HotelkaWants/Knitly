package com.hotelka.knitlyWants.Cards

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Bitmap
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.ui.ImageCropperDialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.hotelka.knitlyWants.Data.Tutorial
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.imageBitmapToByteArray
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

enum class BrushType {
    CIRCLE, ROUNDED_SQUARE, HEART, CRAYON, NONE
}

data class DrawingPath(
    val outlined: Boolean = false,
    val path: Path = Path(),
    val brushType: BrushType = BrushType.NONE,
    val thickness: Float = 0f,
    val color: Color = headers_activeElement
)

data class DraggableShape(
    val outlined: Boolean = false,
    val type: BrushType,
    val position: Offset,
    val size: Size,
    val thickness: Float = 0f,
    val color: Color = Color.Black
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateTutorial(tutorial: Tutorial = Tutorial()) {
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    var selectedBrushType by remember { mutableStateOf(BrushType.NONE) }
    var user by remember { mutableStateOf<UserData>(UserData()) }
    var brushSize by remember { mutableStateOf(10f) }
    var brushColor by remember { mutableStateOf(headers_activeElement) }

    var selectOutlined by remember { mutableStateOf(false) }
    var selectColor by remember { mutableStateOf(false) }
    var selectSize by remember { mutableStateOf(false) }
    var toolsExpanded by remember { mutableStateOf(false) }

    //Shapes
    var shapes = remember { mutableStateListOf<DraggableShape>() }
    // Drawing state
    val drawingPaths = remember { mutableStateListOf<DrawingPath>() }

    val undonePaths = remember { mutableStateListOf<Any>() }
    val states = remember { mutableStateListOf<Any>() }
    // For saving the drawing
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    Box(Modifier.background(white)) {
        var path = remember { DrawingPath() }
        var currentShape by remember { mutableStateOf<DraggableShape?>(null) }

        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawingPaths.forEach {
                    drawPath(
                        it.path,
                        it.color,
                        style = Stroke(it.thickness, cap = StrokeCap.Round),
                    )
                }
                if (currentPosition != Offset.Unspecified) {
                    drawPath(
                        path.path,
                        path.color,
                        style = Stroke(path.thickness, cap = StrokeCap.Round)
                    )
                }
                shapes.forEach { shape ->
                    drawShape(shape)
                }

                // Draw current shape being dragged
                currentShape?.let { shape ->
                    drawShape(shape)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Transparent),
                userScrollEnabled = selectedBrushType == BrushType.NONE
            ) {

                item {
                    val imageCropper = rememberImageCropper()
                    val composableScope = rememberCoroutineScope()
                    val cropState = imageCropper.cropState
                    if (cropState != null) ImageCropperDialog(
                        state = cropState,
                        dialogPadding = PaddingValues(
                            top = 0.dp,
                            start = 0.dp,
                            end = 0.dp,
                            bottom = 80.dp
                        ),
                        topBar = {
                            TopAppBar(
                                title = {},
                                navigationIcon = {
                                    IconButton(onClick = { cropState.done(accept = false) }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            null,
                                            tint = white
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { cropState.reset() }) {
                                        Icon(
                                            painterResource(R.drawable.restore),
                                            null,
                                            tint = white
                                        )
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
                    if (imageBitmap != null) {
                        Image(
                            modifier = Modifier
                                .heightIn(0.dp, 300.dp)
                                .background(basic)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { launcher.launch("image/*") },
                            bitmap = imageBitmap!!,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .background(basic)
                                .clickable { launcher.launch("image/*") },
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_24),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(textColor)
                        )
                    }
                }

                item {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(basic)
                            .padding(horizontal = 10.dp),
                        textStyle = TextStyle(fontSize = 28.sp),
                        colors = TextFieldDefaults.colors(
                            unfocusedLabelColor = textColor,
                            unfocusedPlaceholderColor = textColor,
                            unfocusedContainerColor = Transparent,
                            focusedContainerColor = Transparent,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        label = {
                            Text(
                                text = stringResource(R.string.title),
                                fontSize = 18.sp,
                                color = textColor
                            )
                        }
                    )
                }

                item {
                    Column(Modifier.fillMaxSize()) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            textStyle = TextStyle(fontSize = 18.sp),
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
                item {
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }
            if (selectedBrushType != BrushType.NONE) {
                Box(Modifier
                    .fillMaxSize()
                    .pointerInput(Unit, brushColor, brushSize, selectedBrushType) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (selectedBrushType == BrushType.CRAYON) {
                                    path = DrawingPath(
                                        brushType = selectedBrushType,
                                        thickness = brushSize,
                                        color = brushColor
                                    )
                                    path.path.moveTo(offset.x, offset.y)
                                    currentPosition = offset
                                } else {
                                    currentPosition = offset
                                    currentShape = DraggableShape(
                                        outlined = selectOutlined,
                                        type = selectedBrushType,
                                        position = offset,
                                        size = Size.Zero,
                                        thickness = brushSize,
                                        color = brushColor
                                    )
                                }
                            },
                            onDrag = { change, _ ->
                                if (selectedBrushType == BrushType.CRAYON) {

                                    path.path.lineTo(change.position.x, change.position.y)
                                    currentPosition = change.position
                                } else {
                                    currentShape = currentShape?.let { shape ->
                                        val width = abs(change.position.x - currentPosition.x)
                                        val height = abs(change.position.y - currentPosition.y)

                                        // Calculate position to maintain drag direction
                                        val left = minOf(currentPosition.x, change.position.x)
                                        val top = minOf(currentPosition.y, change.position.y)

                                        shape.copy(
                                            position = Offset(left, top),
                                            size = Size(width, height)
                                        )
                                    }
                                }
                            },
                            onDragEnd = {
                                if (selectedBrushType == BrushType.CRAYON) {
                                    drawingPaths.add(path)
                                    states.add(path)
                                    currentPosition = Offset.Unspecified
                                } else {
                                    currentShape?.let { shape ->
                                        if (shape.size.width > 10f && shape.size.height > 10f) {
                                            shapes.add(shape)
                                            states.add(shape)
                                        }
                                    }
                                    currentShape = null
                                    currentPosition = Offset.Unspecified

                                }
                            }
                        )
                    }) {

                }
            }
        }


        Row(
            Modifier
                .align(Alignment.TopEnd)
                .wrapContentWidth()
                .fillMaxHeight()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(180.dp)
                    .align(Alignment.CenterVertically),
            ) {

                if (selectColor) {
                    ClassicColorPicker(
                        color = brushColor,
                        showAlphaBar = true,
                        onColorChanged = { hsvColor ->
                            brushColor = hsvColor.toColor()
                        })
                }
                if (selectSize) {

                    Slider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        value = brushSize,
                        onValueChange = { brushSize = it },
                        valueRange = 5f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = textColor,
                            activeTickColor = textColor,
                            activeTrackColor = textColor,
                            inactiveTrackColor = secondary
                        )
                    )

                }
            }
            Column(
                modifier = Modifier
                    .padding(end = 8.dp, top = 10.dp)

            ) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = textFieldColor),
                    elevation = CardDefaults.elevatedCardElevation(5.dp)
                ) {
                    Column {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                toolsExpanded = !toolsExpanded
                            }
                        ) {
                            Icon(
                                imageVector =
                                if (toolsExpanded) Icons.Default.ArrowBack
                                else ImageVector.vectorResource(R.drawable.brush),
                                contentDescription = null,
                                tint = textColor,
                            )
                        }
                        if (toolsExpanded) {
                            BrushSelect(
                                selectedBrushType = selectedBrushType,
                                brushType = BrushType.CRAYON,
                                icon = Icons.Default.Edit,
                                onClick = {
                                    selectedBrushType =
                                        if (selectedBrushType == BrushType.CRAYON) BrushType.NONE else BrushType.CRAYON
                                },
                            )
                            BrushSelect(
                                selectedBrushType = selectedBrushType,
                                brushType = BrushType.CIRCLE,
                                icon = if (selectOutlined) ImageVector.vectorResource(R.drawable.circle) else ImageVector.vectorResource(
                                    R.drawable.circle_filled
                                ),
                                onClick = {
                                    selectedBrushType =
                                        if (selectedBrushType == BrushType.CIRCLE) BrushType.NONE else BrushType.CIRCLE
                                },
                            )
                            BrushSelect(
                                selectedBrushType = selectedBrushType,
                                brushType = BrushType.ROUNDED_SQUARE,
                                icon = if (selectOutlined) ImageVector.vectorResource(R.drawable.baseline_crop_square_24) else ImageVector.vectorResource(
                                    R.drawable.square
                                ),
                                onClick = {
                                    selectedBrushType =
                                        if (selectedBrushType == BrushType.ROUNDED_SQUARE) BrushType.NONE else BrushType.ROUNDED_SQUARE
                                },
                            )
//                            BrushSelect(
//                                selectedBrushType = selectedBrushType,
//                                brushType = BrushType.HEART,
//                                icon = if (selectOutlined) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
//                                onClick = {
//                                    selectedBrushType =
//                                        if (selectedBrushType == BrushType.HEART) BrushType.NONE else BrushType.HEART
//                                },
//                            )
                            IconButton(
                                modifier = Modifier,
                                onClick = {
                                    selectColor = !selectColor
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.circle_filled),
                                    contentDescription = null,
                                    tint = brushColor,
                                )
                            }
                            IconButton(
                                modifier = Modifier,
                                onClick = {
                                    selectSize = !selectSize
                                }
                            ) {
                                Text(
                                    text = brushSize.roundToInt().toString(),
                                    fontSize = 15.sp,
                                    color = textColor
                                )
                            }

                        }
                    }
                }
                if (toolsExpanded) {

                    Switch(
                        modifier = Modifier,
                        checked = selectOutlined,
                        onCheckedChange = { selectOutlined = !selectOutlined },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = headers_activeElement,
                            checkedTrackColor = secondary,
                            checkedBorderColor = headers_activeElement,
                            uncheckedThumbColor = darkBasic,
                            uncheckedTrackColor = basic,
                            uncheckedBorderColor = darkBasic
                        )
                    )
                    Text(
                        modifier = Modifier.padding(start = 3.dp),
                        text = stringResource(R.string.outline),
                        color = headers_activeElement,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = textFieldColor),
                        elevation = CardDefaults.elevatedCardElevation(5.dp)
                    ) {
                        Column {
                            IconButton(
                                onClick = {
                                    Log.d("check", states.isNotEmpty().toString())
                                    if (states.isNotEmpty()) {
                                        when (states[states.size - 1]) {
                                            is DrawingPath -> {
                                                val index = drawingPaths.size - 1
                                                undonePaths.add(
                                                    drawingPaths.removeAt(
                                                        index
                                                    )
                                                )
                                                states.removeAt(states.size - 1)
                                            }

                                            else -> {
                                                val index = shapes.size - 1
                                                undonePaths.add(shapes.removeAt(index))
                                                states.removeAt(states.size - 1)

                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.baseline_undo_24),
                                    contentDescription = "Undo",
                                    tint = textColor
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (undonePaths.isNotEmpty()) {
                                        val add = undonePaths.removeAt(undonePaths.size - 1)
                                        when (add) {
                                            is DrawingPath -> drawingPaths.add(
                                                add
                                            )

                                            else -> shapes.add(add as DraggableShape)
                                        }
                                        states.add(add)

                                    }
                                },
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.baseline_redo_24),
                                    contentDescription = "Redo",
                                    tint = textColor
                                )
                            }

                            IconButton(
                                onClick = { drawingPaths.clear(); undonePaths.clear();shapes.clear();states.clear() },
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear",
                                    tint = textColor
                                )
                            }

                            IconButton(
                                onClick = {


                                    bitmap?.let {
                                        scope.launch {
                                            var image = getData(
                                                "tutorials",
                                                uploadFile(
                                                    "tutorial", userData.value.username.toString(),
                                                    imageBitmapToByteArray(it.asImageBitmap())
                                                ).toString()
                                            )
                                            Log.d("image", image)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = "Save",
                                    tint = textColor
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BrushSelect(
    selectedBrushType: BrushType,
    brushType: BrushType,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier
            .background(
                color = if (selectedBrushType == brushType) headers_activeElement
                else textFieldColor,
                shape = RoundedCornerShape(5.dp)
            ),
        onClick = {
            onClick()
        }
    ) {
        Icon(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            imageVector = icon,
            contentDescription = null,
            tint = if (selectedBrushType == brushType) white else textColor,
        )
    }
}


private fun DrawScope.drawShape(shape: DraggableShape) {
    when (shape.type) {
        BrushType.CIRCLE -> {
            drawOval(
                color = shape.color,
                topLeft = shape.position,
                size = shape.size,
                style = if (shape.outlined) Stroke(width = shape.thickness) else Fill
            )
        }

        BrushType.ROUNDED_SQUARE -> {
            drawRoundRect(
                color = shape.color,
                topLeft = shape.position,
                size = shape.size,
                cornerRadius = CornerRadius(20f, 20f),
                style = if (shape.outlined) Stroke(width = shape.thickness) else Fill
            )
        }

//        BrushType.HEART -> {
//            drawHeart(shape)
//        }

        else -> {}
    }
}

//private fun DrawScope.drawHeart(shape: DraggableShape) {
//    val path = Path().apply {
//        val width = size.width
//        val height = size.height
//        val centerX = shape.position.x + width / 2
//        val centerY = shape.position.y + height / 2
//
//        // Start at top center (point of the heart)
//        moveTo(centerX, centerY - height / 2)
//
//        // Right curve (now going downward)
//        cubicTo(
//            centerX + width / 2, centerY,
//            centerX + width / 2, centerY + height / 2,
//            centerX, centerY + height / 2
//        )
//
//        // Left curve (now going upward back to point)
//        cubicTo(
//            centerX - width / 2, centerY + height / 2,
//            centerX - width / 2, centerY,
//            centerX, centerY - height / 2
//        )
//
//        close()
//    }
//
//    drawPath(
//        path = path,
//        color = shape.color,
//        style = if (shape.outlined) Stroke(width = shape.thickness) else Fill
//    )
//}
