package com.hotelka.knitlyWants.Auth

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.ui.ImageCropperDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseAuthenticationHelper
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.MainActivity
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.signInWithFirebase
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.imageBitmapToByteArray
import com.hotelka.knitlyWants.ui.theme.KnitlyTheme
import com.hotelka.knitlyWants.ui.theme.accent_secondary
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
import kotlinx.coroutines.runBlocking
import java.util.UUID

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            KnitlyTheme {
                Surface(color = basic) {
                    navController = rememberNavController()
                    NavHostContainer(navController)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun NavHostContainer(
        navController: NavHostController,
    ) {
        var extra = intent.getBooleanExtra("linkedAccount", false)
        NavHost(
            navController = navController,

            startDestination = if (!extra) "register" else "infoScreen",

            builder = {
                composable("infoScreen") {
                    provideInformationScreen(this@RegisterActivity, extra)
                }
                composable("register") {
                    RegisterScreen(::googleSignIn, navController, this@RegisterActivity)
                }
                composable("signIn") {
                    SignInScreen(::googleSignIn, navController, this@RegisterActivity)
                }

            })
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    firebaseAuthWithGoogle(token)
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseDB.checkExistUser(task.result.user?.uid.toString(),
                        rememberId = { id ->
                            var prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
                            val editor = prefs.edit()
                            editor.putString("idSelected", id)
                            editor.apply()
                        },
                        onExist = { UpdateUI(task.result.user!!, this) },
                        onNotExist = {
                            runBlocking { signInWithFirebase() }
                            FirebaseDB.createUser(task.result.user!!)
                            navController.navigate("infoScreen")
                        })

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @Preview()
    @Composable
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun RegisterPreview() {
        navController = rememberNavController()
        RegisterScreen(::googleSignIn, navController, this@RegisterActivity)
    }


    companion object {
        const val RC = 123
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onGoogleClick: () -> Unit,
    navController: NavHostController,
    activity: RegisterActivity
) {
    var passwordReset by rememberSaveable { mutableStateOf(false) }
    if (passwordReset) {
        PasswordResetAlert({ passwordReset = false }) { email ->
            FirebaseAuthenticationHelper.sendPasswordReset(email) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.linkIsSend),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .background(basic),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        val focusManager = LocalFocusManager.current
        val keyboard = LocalSoftwareKeyboardController.current
        val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"

        var isEmailError = rememberSaveable { mutableStateOf(false) }
        var arePasswordsMatches = rememberSaveable { mutableStateOf(false) }
        var isPasswordValid = rememberSaveable { mutableStateOf(false) }

        val context = LocalContext.current
        var email = remember { mutableStateOf("") }
        var password = remember { mutableStateOf("") }

        fun emailMatchesRegex(email: String) {
            if (!email.matches(Regex(emailRegex))) {
                isEmailError.value = true
            }
        }

        fun isPasswordInvalid(password: String) {
            if (password.length < 7) {
                isPasswordValid.value = true
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp),
            text = stringResource(R.string.signInWelcome),
            fontSize = 32.sp,
            color = textColor,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    lineHeight = 1.2.em
                )
            )
        )


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            singleLine = true,
            value = email.value,
            onValueChange = {
                isEmailError.value = false
                emailMatchesRegex(email.value)
                email.value = it
            },
            label = {
                if (isEmailError.value) {
                    Text("Email*" + context.getString(R.string.emailInvalid))
                } else {
                    Text("Email")
                }
            },
            isError = isEmailError.value,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = secondary,
                unfocusedContainerColor = textFieldColor,
            ),
            trailingIcon = {
                if (isEmailError.value) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "error",
                        tint = error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = {
                password.value = it
                isPasswordValid.value = false
                isPasswordInvalid(password.value)
            },
            label = {
                if (isPasswordValid.value) Text(
                    "${stringResource(R.string.password)}*${
                        stringResource(
                            R.string.invalidPassword
                        )
                    }"
                )
                else Text(stringResource(R.string.password))
            },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = secondary,
                unfocusedContainerColor = textFieldColor
            ),
            isError = isPasswordValid.value,
            trailingIcon = {
                if (isPasswordValid.value) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "error",
                        tint = error
                    )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {

                if (!isEmailError.value && !isPasswordValid.value && !arePasswordsMatches.value) {
                    FirebaseAuthenticationHelper.signInUserWithEmailAndPassword(
                        email.value,
                        password.value,
                        activity,
                    )

                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = accent_secondary)
        ) {
            Text(stringResource(R.string.signIn))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onGoogleClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement) // Google red color
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon), // Replace with your Google icon resource
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.signInGoogle), color = Color.White)
            }
        }

        Text(
            text = stringResource(R.string.needAccount),
            modifier = Modifier
                .align(Alignment.End)
                .padding(10.dp)
                .clickable { navController.navigate("register") },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = stringResource(R.string.forgotPassword),
            modifier = Modifier
                .align(Alignment.End)
                .padding(10.dp)
                .clickable {
                    passwordReset = true
                }
                .imePadding(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetAlert(onDismiss: () -> Unit, onSendClicked: (String) -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(basic),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                val keyboard = LocalSoftwareKeyboardController.current
                val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"
                var isEmailError = rememberSaveable { mutableStateOf(false) }
                fun emailMatchesRegex(email: String) {
                    if (!email.matches(Regex(emailRegex))) {
                        isEmailError.value = true
                    }
                }

                val context = LocalContext.current
                var email = remember { mutableStateOf("") }
                TextField(
                    singleLine = true,
                    value = email.value,
                    onValueChange = {
                        isEmailError.value = false
                        emailMatchesRegex(email.value)
                        email.value = it
                    },
                    label = {
                        if (isEmailError.value) {
                            Text("Email*" + context.getString(R.string.emailInvalid))
                        } else {
                            Text("Email")
                        }
                    },
                    isError = isEmailError.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = secondary,
                        unfocusedContainerColor = textFieldColor,
                    ),
                    trailingIcon = {
                        if (isEmailError.value) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "error",
                                tint = error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboard?.hide()
                        }
                    )
                )
                Row(
                    Modifier
                        .wrapContentSize()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onSendClicked(email.value)
                        }
                    ) {
                        Text(stringResource(R.string.sendLink))

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun provideInformationScreen(register: ComponentActivity, linking: Boolean = false) {
    val focusManager = LocalFocusManager.current
    val composableScope = rememberCoroutineScope()
    val nameError = rememberSaveable { mutableStateOf(false) }
    val usernameError = rememberSaveable { mutableStateOf(false) }
    var isUserExist by rememberSaveable { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance().currentUser!!

    var username = remember { mutableStateOf("") }
    var bio = remember { mutableStateOf("") }
    var name = remember { mutableStateOf("") }
    var lastName = remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    if (auth.photoUrl != null) {
        urlToBitmap(composableScope,
            auth.photoUrl.toString(),
            context,
            onError = { Log.e("Error", it.message.toString()) }) { bitmap ->
            imageBitmap = bitmap.asImageBitmap()
        }
    }
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState
    if (cropState != null) ImageCropperDialog(
        state = cropState,
        dialogPadding = PaddingValues(bottom = 80.dp),
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 40.dp)
            .background(basic),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier.padding(10.dp),
                text = stringResource(R.string.provideInfo),
                fontSize = 32.sp, color = textColor,
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 1.2.em
                    )
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (imageBitmap == null) {
                AsyncImage(
                    model = R.drawable.outline_account_circle_24,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable(onClick = {
                            launcher.launch("image/*")
                        })
                        .border(
                            2.dp,
                            Color.Gray,
                            CircleShape
                        ),

                    )
            } else {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable(onClick = {
                            launcher.launch("image/*")
                        })
                        .border(
                            2.dp,
                            Color.Gray,
                            CircleShape
                        ),

                    )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        if (isUserExist) stringResource(R.string.username) + "*" + stringResource(R.string.unavailable_username)
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

            Spacer(modifier = Modifier.height(8.dp))

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
            Spacer(modifier = Modifier.height(8.dp))

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
            Spacer(modifier = Modifier.height(8.dp))

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
            Button(
                onClick = {
                    if ((isUserExist == false) && (usernameError.value == false) && (nameError.value == false)) {

                        composableScope.launch {

                            val id = if (!linking) auth.uid else UUID.randomUUID().toString()


                            val parsedUser = UserData(
                                userId = id,
                                username = username.value,
                                name = name.value,
                                lastName = lastName.value,
                                email = auth.email.toString(),
                                profilePictureUrl = getData(
                                    "avatars",
                                    uploadFile("avatars", id, imageBitmapToByteArray(imageBitmap!!)).toString()
                                ),
                                bio = bio.value,
                                linkedAccountsId = userData.value.userId
                            )
                            var prefs =
                                register.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
                            val editor = prefs.edit()
                            editor.putString("idSelected", id)
                            editor.apply()

                            FirebaseDB.uploadUserInfoReg(parsedUser, {
                                if (linking) {
                                    FirebaseDB.refUsers.child(userData.value.userId)
                                        .child("linkedAccountsId").setValue(id)
                                }
                                register.startActivity(
                                    Intent(
                                        register,
                                        MainActivity::class.java
                                    )
                                )
                                register.finish()
                            })

                        }

                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                colors = ButtonDefaults.buttonColors(containerColor = accent_secondary)
            ) {
                Text(stringResource(R.string.create_profile))
            }
        }
    }

}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onGoogleClick: () -> Unit,
    navController: NavHostController,
    activity: RegisterActivity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .background(basic),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        val focusManager = LocalFocusManager.current
        val keyboard = LocalSoftwareKeyboardController.current
        val emailRegex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"

        var isEmailError = rememberSaveable { mutableStateOf(false) }
        var arePasswordsMatches = rememberSaveable { mutableStateOf(false) }
        var isPasswordValid = rememberSaveable { mutableStateOf(false) }

        val context = LocalContext.current
        var email = remember { mutableStateOf("") }
        var password = remember { mutableStateOf("") }
        var repeatPassword = remember { mutableStateOf("") }

        fun emailMatchesRegex(email: String) {
            if (!email.matches(Regex(emailRegex))) {
                isEmailError.value = true
            }
        }

        fun arePasswordsNotMatches(passwordRepeat: String) {
            if (password.value != passwordRepeat) {
                arePasswordsMatches.value = true
            }
        }

        fun isPasswordInvalid(password: String) {
            if (password.length < 7) {
                isPasswordValid.value = true
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp),
            text = stringResource(R.string.registration),
            fontSize = 32.sp,
            color = textColor,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    lineHeight = 1.2.em
                )
            )
        )


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            singleLine = true,
            value = email.value,
            onValueChange = {
                isEmailError.value = false
                emailMatchesRegex(email.value)
                email.value = it
            },
            label = {
                if (isEmailError.value) {
                    Text("Email*" + context.getString(R.string.emailInvalid))
                } else {
                    Text("Email")
                }
            },
            isError = isEmailError.value,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = secondary,
                unfocusedContainerColor = textFieldColor,
            ),
            trailingIcon = {
                if (isEmailError.value) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "error",
                        tint = error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input Field
        TextField(
            value = password.value,
            onValueChange = {
                password.value = it
                isPasswordValid.value = false
                isPasswordInvalid(password.value)
            },
            label = {
                if (isPasswordValid.value) Text(
                    "${stringResource(R.string.password)}*${
                        stringResource(
                            R.string.invalidPassword
                        )
                    }"
                )
                else Text(stringResource(R.string.password))
            },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = secondary,
                unfocusedContainerColor = textFieldColor
            ),
            isError = isPasswordValid.value,
            trailingIcon = {
                if (isPasswordValid.value) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "error",
                        tint = error
                    )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        // Repeat Password Input Field
        TextField(
            value = repeatPassword.value,
            onValueChange = {
                repeatPassword.value = it
                arePasswordsMatches.value = false
                arePasswordsNotMatches(repeatPassword.value)
            },
            label = {
                if (arePasswordsMatches.value) "${Text(stringResource(R.string.repeat_password))}*${
                    Text(
                        stringResource(R.string.password_dont_match)
                    )
                }"
                else Text(stringResource(R.string.repeat_password))
            },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = secondary,
                unfocusedContainerColor = textFieldColor
            ),
            isError = arePasswordsMatches.value,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (arePasswordsMatches.value) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "error",
                        tint = error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {

                if (!isEmailError.value && !isPasswordValid.value && !arePasswordsMatches.value) {
                    FirebaseAuthenticationHelper.createUserWithEmailAndPassword(
                        email.value,
                        password.value,
                        activity,
                        navController
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = accent_secondary)
        ) {
            Text(stringResource(R.string.register))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onGoogleClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement) // Google red color
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon), // Replace with your Google icon resource
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sign in with Google", color = Color.White)
            }
        }
        Text(
            text = stringResource(R.string.haveAccount),
            modifier = Modifier
                .align(Alignment.End)
                .padding(10.dp)
                .clickable { navController.navigate("signIn") }
                .imePadding(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

fun UpdateUI(user: FirebaseUser?, startActivity: ComponentActivity) {
    if (user != null) {
        startActivity.startActivity(Intent(startActivity, MainActivity::class.java))
        startActivity.finish()
    } else {
        startActivity.startActivity(Intent(startActivity, RegisterActivity::class.java))
        startActivity.finish()
    }
}

