package com.hotelka.knitlyWants

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hotelka.knitlyWants.Auth.RegisterActivity
import com.hotelka.knitlyWants.Cards.CreateTutorial
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.CHILD_USERS
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseAuthenticationHelper
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseMessageReceiver
import com.hotelka.knitlyWants.Supbase.key
import com.hotelka.knitlyWants.Supbase.url
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.nav.ChatScreen
import com.hotelka.knitlyWants.nav.CreateProjectScreen
import com.hotelka.knitlyWants.nav.CurrentUserProfileScreen
import com.hotelka.knitlyWants.nav.DashBoard
import com.hotelka.knitlyWants.nav.EditProfile
import com.hotelka.knitlyWants.nav.HomeScreen
import com.hotelka.knitlyWants.nav.NotificationsAndChats
import com.hotelka.knitlyWants.nav.ProjectOverview
import com.hotelka.knitlyWants.nav.Tutorials
import com.hotelka.knitlyWants.nav.UserProfile
import com.hotelka.knitlyWants.nav.WorkingOnProject
import com.hotelka.knitlyWants.ui.theme.KnitlyTheme
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch

val supabase: SupabaseClient = createSupabaseClient(url, key) {
    install(Auth) { autoLoadFromStorage = false }
    install(Storage)

}
lateinit var userData: MutableState<UserData>
var chatOpened: Chat? = null
var projectCurrent: Project? = null
var editableProject: Project? = null
var editableBlog: Blog? = null
var userWatching: UserData? = null
var blogCurrent: Blog? = null
var currentProjectInProgress: ProjectsArchive? = null
lateinit var users: MutableState<List<UserData>>
lateinit var navController: NavHostController

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        setContent {
            KnitlyTheme {
                userData = remember { mutableStateOf(UserData()) }
                if (firebaseAuth.currentUser == null) {
                    startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                    finish()
                } else {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            101
                        );
                    }
                    LaunchedEffect(Unit) {
                        getUser()
                        FirebaseDB.sendToken()
                    }
                    users =
                        remember { mutableStateOf(SupportingDatabase(baseContext).getAllUsers()) }

                    Surface(color = basic) {
                        FirebaseDB.createSupportingDatabase(baseContext)
                        navController = rememberNavController()
                        Knitly(navController)
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        var prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val id = prefs.getString("idSelected", firebaseAuth.currentUser!!.uid)
        FirebaseDB.isOnlineSend(true, id.toString())
    }

    override fun onPause() {
        super.onPause()
        FirebaseDB.isOnlineSend(false, firebaseAuth.currentUser!!.uid)
    }
    fun getUser() {
        var prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val id = prefs.getString("idSelected", firebaseAuth.currentUser!!.uid)
        FirebaseDatabase.getInstance().getReference().child(CHILD_USERS)
            .child(id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userData.value = snapshot.getValue<UserData>(UserData::class.java)!!
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    fun HandleLogOut() {
        lifecycleScope.launch {
            FirebaseAuthenticationHelper.signOutGoogle(
                CredentialManager.create(this@MainActivity),
                this@MainActivity
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun NavHostContainer(
        navController: NavHostController,
        padding: PaddingValues,
    ) {

        NavHost(
            navController = navController,

            startDestination = "createTutorial",

            modifier = Modifier.padding(paddingValues = padding),

            builder = {
                composable("createProject") {
                    CreateProjectScreen(editableProject, editableBlog)
                }
                composable("myProfile") {
                    CurrentUserProfileScreen()
                }
                composable("notificationsAndChats") {
                    NotificationsAndChats()
                }
                composable("chat") {
                    if (chatOpened != null) {
                        ChatScreen(chat = chatOpened!!)
                    }
                }
                composable("editProfile") {
                    EditProfile()
                }
                composable("home") {
                    HomeScreen()
                }
                composable("dashboard") {
                    DashBoard()
                }
                composable("tutorials") {
                    Tutorials()
                }
                composable("createTutorial"){
                    CreateTutorial()
                }
                composable("userProfile") {
                    if (userWatching != null) {
                        UserProfile(userWatching!!)
                    }
                }
                composable("projectOverview") {
                    if (projectCurrent != null) {
                        ProjectOverview(projectCurrent!!)
                    } else if (blogCurrent != null) (
                            ProjectOverview(blog = blogCurrent)
                            )
                }
                composable("workingOnProject") {
                    if (currentProjectInProgress != null) {
                        WorkingOnProject(currentProjectInProgress!!)
                    }
                }

            })
    }


    data class NavItemState(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val hasBadge: Boolean,
        val messages: Int,
        val route: String
    )

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Knitly(navController: NavHostController, modifier: Modifier = Modifier) {
        var expanded by remember { mutableStateOf(false) }

        val items = listOf(
            NavItemState(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasBadge = false,
                messages = 0,
                route = "home"
            ),
            NavItemState(
                title = "Dashboard",
                selectedIcon = ImageVector.vectorResource(R.drawable.baseline_space_dashboard_24),
                unselectedIcon = ImageVector.vectorResource(R.drawable.outline_space_dashboard_24),
                hasBadge = false,
                messages = 0,
                route = "dashboard"
            ),

            NavItemState(
                title = "Tutorials",
                selectedIcon = ImageVector.vectorResource(R.drawable.tutorials),
                unselectedIcon = ImageVector.vectorResource(R.drawable.tutorials_outlined),
                hasBadge = false,
                messages = 0,
                route = "tutorials"

            )
        )
        var bottomNavState by rememberSaveable {
            mutableIntStateOf(0)
        }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            containerColor = basic,
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (navController.currentDestination?.route != "createProject") {
                                Text(
                                    text = "Artly",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                            } else {
                                Text(
                                    text = "Create Project",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                        }

                    },
                    modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(0.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    navigationIcon = {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            AsyncImage(
                                model = userData.value.profilePictureUrl,
                                contentDescription = "Current User",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        expanded = !expanded
                                    },
                                contentScale = ContentScale.Crop

                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("notificationsAndChats")
                        }) {
                            BadgedBox(badge = {
                                Badge(
                                    modifier.size(10.dp), containerColor = headers_activeElement
                                ) {
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    tint = textColor,
                                    contentDescription = "Fav icon"
                                )
                            }

                        }

                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = white
                    ),
                    scrollBehavior = scrollBehavior
                )

            },
            bottomBar = {
                NavigationBar(
                    modifier
                        .clip(RoundedCornerShape(20.dp, 20.dp)),
                    containerColor = white
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = bottomNavState == index,
                            onClick = {
                                bottomNavState = index
                                navController.navigate(item.route)
                            },
                            icon = {
                                BadgedBox(badge = {
                                    if (item.hasBadge) Badge(containerColor = headers_activeElement) { }
                                    if (item.messages != 0) Badge(containerColor = headers_activeElement) {
                                        Text(text = "${item.messages}")
                                    }
                                }) {
                                    Icon(
                                        tint = textColor,
                                        imageVector = if (bottomNavState == index) item.selectedIcon
                                        else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }

                            },
                            label = { Text(text = item.title, color = textColor) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = white,
                                selectedTextColor = textColor,
                                indicatorColor = accent_secondary
                            )
                        )
                    }
                }
            },
        ) { contentPadding ->

            NavHostContainer(navController = navController, padding = contentPadding)

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 120.dp, start = 10.dp)
                        .background(
                            white,
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)

                    ) {

                        Row(
                            modifier = Modifier
                                .clickable {
                                    expanded = false
                                    navController.navigate("myProfile")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(userData.value.profilePictureUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Text(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp),
                                text = stringResource(R.string.my_profile),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            thickness = 1.dp,
                            color = Color(106, 78, 66, 70)
                        )


                        var account by remember { mutableStateOf<UserData?>(null) }
                        if (userData.value.linkedAccountsId.toString() != "") {
                            FirebaseDB.refUsers.child(userData.value.linkedAccountsId.toString())
                                .get().addOnSuccessListener {
                                account =
                                    it.getValue<UserData>(UserData::class.java)
                            }
                        }
                        if (account != null) {
                            Row(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clickable {
                                        expanded = false
                                        var prefs =
                                            getSharedPreferences("Prefs", Context.MODE_PRIVATE)
                                        val editor = prefs.edit()
                                        editor.putString("idSelected", account!!.userId)
                                        editor.apply()
                                        getUser()
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = if (account!!.profilePictureUrl?.isNotEmpty() == true) account!!.profilePictureUrl
                                    else R.drawable.baseline_account_circle_24,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop

                                )

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .align(Alignment.CenterVertically)
                                        .padding(start = 10.dp),
                                    text = account!!.username!!,
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (account == userData.value) FontWeight.Bold else FontWeight.Normal
                                )

                            }
                        }
                        if (userData.value.linkedAccountsId == null || userData.value.linkedAccountsId == "") {
                            Row(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clickable {
                                        expanded = false
                                        val intent =
                                            Intent(this@MainActivity, RegisterActivity::class.java)
                                        intent.putExtra("linkedAccount", true)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_person_add_alt_1_24),
                                    colorFilter = ColorFilter.tint(textColor),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp),
                                )

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .align(Alignment.CenterVertically)
                                        .padding(start = 10.dp),
                                    text = stringResource(R.string.addAccount),
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .clickable {
                                    expanded = false
                                    HandleLogOut()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_logout_24),
                                colorFilter = ColorFilter.tint(textColor),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp),
                            )

                            Text(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp),
                                text = stringResource(R.string.log_out),
                                color = textColor,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal
                            )
                        }


                    }
                }
            }
        }

    }
}
