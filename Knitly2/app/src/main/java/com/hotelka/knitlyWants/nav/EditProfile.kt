package com.hotelka.knitlyWants.nav

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.ui.ImageCropperDialog
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refUsers
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.imageBitmapToByteArray
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.error
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.urlToBitmap
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditProfile() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val composableScope = rememberCoroutineScope()

    val nameError = rememberSaveable { mutableStateOf(false) }
    val usernameError = rememberSaveable { mutableStateOf(false) }
    var isUserExist by rememberSaveable { mutableStateOf(false) }


    var username = remember { mutableStateOf(userData.value.username!!) }
    var bio = remember { mutableStateOf(userData.value.bio!!) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var name = remember { mutableStateOf(userData.value.name!!) }
    var lastName = remember { mutableStateOf(userData.value.lastName!!) }

    if (userData.value.profilePictureUrl != null && userData.value.profilePictureUrl != "") {
        urlToBitmap(composableScope,
            userData.value.profilePictureUrl!!,
            context,
            onError = { Log.e("Error", it.message.toString()) }) { bitmap ->
            imageBitmap = bitmap.asImageBitmap()
        }
    }
    fun usernameNotAvailable(usernameWanted: String): Boolean {
        isUserExist = false
        FirebaseDB.refUsers.get().addOnSuccessListener {
            it.children.forEach { user ->
                val username = user.getValue<UserData>(UserData::class.java)?.username.toString()
                if (usernameWanted.replace(" ", "").contains(username)) isUserExist = true
            }
        }
        return isUserExist
    }

    fun usernameError(usernameWanted: String) {
        if (usernameWanted.length < 5 || usernameWanted.length > 17) {
            usernameError.value = true
        }
    }

    fun nameRequired() {
        if (name.value.isEmpty()) {
            nameError.value = true
        }
    }

    val imageCropper = rememberImageCropper()
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = basic // Background color
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                Row {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .fillMaxSize()
                                .clip(CircleShape)

                                .border(
                                    2.dp,
                                    Color.Gray,
                                    CircleShape
                                ),

                            )
                    } else {
                        AsyncImage(
                            model = R.drawable.baseline_account_circle_24,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .fillMaxSize()
                                .clip(CircleShape)

                                .border(
                                    2.dp,
                                    Color.Gray,
                                    CircleShape
                                ),

                            )
                    }
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Bottom),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            launcher.launch("image/*")
                        }) {
                        Text(text = stringResource(R.string.changeProfilePhoto), color = white)
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    singleLine = true,
                    value = username.value,
                    onValueChange = {
                        username.value = it
                        isUserExist = false
                        usernameError.value = false
                        usernameError(username.value)
                        usernameNotAvailable(username.value)
                    },
                    label = {
                        Text(
                            if (isUserExist) stringResource(R.string.username) + "*" + stringResource(
                                R.string.unavailable_username
                            )
                            else if (usernameError.value) stringResource(R.string.username) + "*" + stringResource(
                                R.string.username_valid
                            )
                            else stringResource(R.string.username)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedContainerColor = textFieldColor
                    ),
                    isError = isUserExist || usernameError.value,
                    trailingIcon = {
                        if (usernameError.value) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "error",
                                tint = error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    singleLine = true,
                    value = name.value,
                    onValueChange = {
                        name.value = it
                        nameError.value = false
                        nameRequired()
                    },
                    label = {
                        val additional =
                            if (usernameError.value) "*${stringResource(R.string.required)}" else ""
                        Text(stringResource(R.string.name) + additional)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedContainerColor = textFieldColor
                    ),
                    isError = nameError.value,
                    trailingIcon = {
                        if (nameError.value) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "error",
                                tint = error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    singleLine = true,
                    value = lastName.value,
                    onValueChange = { lastName.value = it },
                    label = { Text(stringResource(R.string.last_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedContainerColor = textFieldColor
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = bio.value,
                    onValueChange = { bio.value = it },
                    label = { Text(stringResource(R.string.bio)) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedContainerColor = textFieldColor
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()) {
                    Button(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.BottomEnd),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            if ((isUserExist == false) && (usernameError.value == false) && (nameError.value == false)) {
                                val map: MutableMap<String, Any> = LinkedHashMap()
                                map["username"] = username.value
                                map["name"] = name.value
                                map["lastName"] = lastName.value
                                map["bio"] = bio.value
                                composableScope.launch {
                                    map["profilePictureUrl"] = getData(
                                        "avatars",
                                        uploadFile(
                                            "avatars",
                                            userData.value.username!!,
                                            imageBitmapToByteArray(imageBitmap!!)
                                        ).toString()
                                    )

                                    FirebaseDB.updateUser(map)
                                }


                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.saveChanges), color = white)
                    }
                }
            }
        }
    }
}