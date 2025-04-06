package com.hotelka.knitlyWants.FirebaseUtils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.Data.Comment
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.Message
import com.hotelka.knitlyWants.Data.PatternData
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.Data.Tutorial
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.blogCurrent
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.read
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang3.RandomStringUtils
import java.net.URL
import java.util.UUID
import kotlin.String
import kotlin.collections.plus


const val CHILD_USERS = "Users"
const val PROJECTS = "Projects"
const val BLOGS = "Blogs"
const val TUTORIALS = "Tutorials"
const val PATTERNS = "Patterns"
const val CHATS = "Chats"
const val PROJECTS_IN_PROGRESS = "ProjectsInProgress"

class FirebaseDB {
    companion object {
        private val ref = FirebaseDatabase.getInstance().getReference()
        val refUsers = ref.child(CHILD_USERS)
        val refProjects = ref.child(PROJECTS)
        val refBlogs = ref.child(BLOGS)
        val refPatterns = ref.child(PATTERNS)
        val refChats = ref.child(CHATS)
        val refTutorials = ref.child(TUTORIALS)
        val refProjectsInProgress = ref.child(PROJECTS_IN_PROGRESS)

        fun getTutorials(onDone: (Tutorial) -> Unit){
            refTutorials.get().addOnSuccessListener{
                for (tutorial in it.children){
                    val tutorial = tutorial.getValue(Tutorial::class.java)
                    tutorial?.let { p1 -> onDone(p1) }
                }
            }
        }

        fun saveTutorial(id: String, tutorial: Tutorial, onDone: () -> Unit){
            refTutorials.child(id).setValue(tutorial).addOnSuccessListener{
                onDone()
            }
        }
        fun isOnlineGet(id: String, init: (Boolean) -> Unit) {
            refUsers.child(id).child("isOnline").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Boolean::class.java)?.let { init(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        fun sendMessage(message: Message, chatId: String, onDone: () -> Unit) {
            refChats.child(chatId).child("messages").child(message.id.toString()).setValue(message)
                .addOnSuccessListener {
                    onDone()
                }
        }

        fun isOnlineSend(isOnline: Boolean, idSupport: String) {
            refUsers.child(idSupport).child("isOnline").setValue(isOnline)
        }

        fun chatChecked(id: String, message: Message, onDone: () -> Unit) {
            if (message.user.userId != userData.value.userId) {
                refChats.child(id).child("messages").child(message.id.toString()).child("checked")
                    .setValue(true).addOnSuccessListener {
                        onDone()
                    }
            }
        }

        fun getIfMessageChecked(chatId: String, messageId: String, onDone: (Boolean) -> Unit) {
            refChats.child(chatId).child("messages").child(messageId).child("checked")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val checked = snapshot.getValue(Boolean::class.java)
                        checked?.let { onDone(it) }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        fun getChat(id: String, onDone: (Chat?) -> Unit) {
            refChats.child(id.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chat = snapshot.getValue(Chat::class.java)
                    onDone(chat)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        fun getChats(onDone: (Chat?) -> Unit) {
            userData.value.chats.forEach { id ->
                refChats.child(id.toString()).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chat = snapshot.getValue(Chat::class.java)
                        onDone(chat)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }
        }

        fun getChatMessages(id: String, onDone: (Message) -> Unit) {
            refChats.child(id.toString()).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val message = it.getValue(Message::class.java)
                            message?.let { p1 -> onDone(p1) }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }

        fun createChat(user: UserData, onDone: (Chat) -> Unit) {
            val id = UUID.randomUUID().toString()
            val chat = Chat(
                id = id,
                users = listOf(userData.value.userId, user.userId),
                messages = LinkedHashMap()
            )
            refUsers.child(userData.value.userId).child("chats").get()
                .addOnSuccessListener { snapshot ->
                    snapshot.child(snapshot.childrenCount.toString()).ref.setValue(id)
                }
            refUsers.child(user.userId).child("chats").get().addOnSuccessListener { snapshot ->
                snapshot.child(snapshot.childrenCount.toString()).ref.setValue(id)
            }
            refChats.child(id).setValue(chat).addOnSuccessListener {
                onDone(chat)
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun getPattern(id: String, authorId: String, onDone: (PatternData) -> Unit) {
            refPatterns.child(authorId).child(id)
                .get().addOnSuccessListener { snapshot ->
                    Log.e("snapshot", id)
                    runBlocking {
                        GlobalScope.launch {
                            try {
                                var url = URL(snapshot.value.toString())
                                val pattern = Gson().fromJson(url.read(), PatternData::class.java)
                                onDone(pattern)
                            } catch (e: Exception) {
                            }

                        }
                    }
                }
        }


        fun savePattern(fileName: String, onDone: (String) -> Unit) {
            val id = UUID.randomUUID().toString()
            refPatterns.child(userData.value.userId).child(id)
                .setValue(fileName).addOnSuccessListener {
                    onDone(id)
                }
        }

        fun createSupportingDatabase(context: Context) {
            var blogsIds = listOf<String>()
            var projectsIds = listOf<String>()
            var projectsInProgressIds = listOf<String>()
            var usersIds = listOf<String>()

            val db = SupportingDatabase(context)
            refUsers.get().addOnSuccessListener { snapshot ->
                for (user in snapshot.children) {
                    user.getValue(UserData::class.java)?.let {
                        db.addUser(it)
                        usersIds += it.userId
                    }
                }
                db.getAllUsers().forEach { user ->
                    if (!usersIds.contains(user.userId)) {
                        db.deleteUser(user)
                    }
                }
            }
            refProjects.get().addOnSuccessListener { snapshot ->
                for (project in snapshot.children) {
                    project.getValue(Project::class.java)?.let {
                        db.addProject(it, it.projectData!!.projectId!!)
                        projectsIds += it.projectData.projectId
                    }
                }
                db.getAllProjects().forEach { project ->
                    if (!projectsIds.contains(project.projectData!!.projectId!!)) {
                        db.deleteProject(project)
                    }
                }
            }
            refBlogs.get().addOnSuccessListener { snapshot ->
                for (project in snapshot.children) {
                    project.getValue(Blog::class.java)?.let {
                        db.addBlog(it, it.projectData!!.projectId!!)
                        blogsIds += it.projectData!!.projectId!!
                    }
                }
                db.getAllBlog().forEach { blog ->
                    if (!blogsIds.contains(blog.projectData!!.projectId!!)) {
                        db.deleteBlog(blog)
                    }
                }
            }
            refProjectsInProgress.child(userData.value.userId).get()
                .addOnSuccessListener { snapshot ->
                    for (project in snapshot.children) {
                        project.getValue(ProjectsArchive::class.java)?.let {
                            db.addProjectInProgress(it, it.project?.projectData!!.projectId!!)
                            projectsInProgressIds += it.project.projectData.projectId
                        }
                    }
                    db.getAllProjectInProgress().forEach { project ->
                        if (!projectsIds.contains(project.project!!.projectData!!.projectId!!)) {
                            db.deleteProjectInProgress(project)
                        }
                    }
                }

        }

        fun updateProject(project: Project) {
            refProjects.child(project.projectData!!.projectId.toString()).setValue(project)
            refProjectsInProgress.get().addOnSuccessListener { snapshot ->
                snapshot.children.forEach { user ->
                    if (user.child(project.projectData.projectId.toString()).exists())
                        user.child(project.projectData.projectId.toString())
                            .child("project").ref.setValue(project)
                }
            }
            editableProject = null
            navController.popBackStack()

        }

        fun updateBlog(blog: Blog) {
            refBlogs.child(blog.projectData!!.projectId.toString()).setValue(blog)
            blogCurrent = null
            navController.popBackStack()

        }

        fun storeBlog(blog: Blog, uniqueUUID: String) {
            refBlogs.child(uniqueUUID).setValue(blog)
            refUsers.child(FirebaseAuth.getInstance().currentUser!!.uid).child(BLOGS).get()
                .addOnSuccessListener {
                    it.ref.child("Blog ${it.children.toList().size + 1}").setValue(uniqueUUID)
                    navController.popBackStack()
                }
        }

        fun storeProjectCrocheting(project: Project, uniqueUUID: String) {
            refProjects.child(uniqueUUID).setValue(project)
            refUsers.child(FirebaseAuth.getInstance().currentUser!!.uid).child(PROJECTS).get()
                .addOnSuccessListener {
                    it.ref.child("Project ${it.children.toList().size + 1}").setValue(uniqueUUID)
                    navController.popBackStack()
                }

        }

        fun sendReview(projectId: String, reviews: Int) {
            refProjects.child(projectId).child("projectData").child("reviews")
                .setValue(
                    reviews.plus(1)
                )
        }

        fun sendReviewBlog(projectId: String, reviews: Int) {
            refBlogs.child(projectId).child("projectData").child("reviews")
                .setValue(
                    reviews.plus(1)
                )
        }

        fun getComments(
            reference: DatabaseReference,
            id: String,
            onDataLoaded: (MutableList<Comment>) -> Unit
        ) {
            var comments = mutableListOf<Comment>()
            reference.child(id).child("comments")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (comment in snapshot.children) {
                            var comment = comment.getValue(Comment::class.java)!!
                            comments.add(comment)
                        }
                        onDataLoaded(comments)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        fun sendCommentReply(comment: Comment, postId: String, id: String, typeDbRef: DatabaseReference) {
            typeDbRef.child(postId).child("comments").child(id.toString()).child("replies").child(comment.id.toString()).setValue(comment)
        }
        fun sendComment(comment: Comment, id: String, typeDbRef: DatabaseReference) {
            typeDbRef.child(id).child("comments").child(comment.id.toString()).setValue(comment)
        }

        fun deleteComment(comment: Comment, id: String, typeDbRef: DatabaseReference) {
            typeDbRef.child(id).child("comments").child(comment.id.toString()).removeValue()
        }

        fun sendLikeComment(commentId: String, id: String, typeDbRef: DatabaseReference) {
            typeDbRef.child(id).child("comments").child(commentId).child("likes").get()
                .addOnSuccessListener {
                    var like = it.getValue<Likes>(Likes::class.java)!!
                    if (like.users?.contains(userData.value.userId.toString()) == true) {
                        typeDbRef.child(id).child("comments").child(commentId).child("likes")
                            .child("total")
                            .setValue(
                                like.total?.minus(1)
                            )
                        typeDbRef.child(id).child("comments").child(commentId).child("likes")
                            .child("users").get().addOnSuccessListener {
                                it.children.forEach {
                                    if (it.value == userData.value.userId) {
                                        it.ref.removeValue()
                                    }
                                }
                            }
                    } else {
                        typeDbRef.child(id).child("comments").child(commentId).child("likes")
                            .child("total")
                            .setValue(
                                like.total?.plus(1)
                            )
                        var map: MutableMap<String?, Any> = LinkedHashMap()
                        map[like.total?.plus(1).toString()] = userData.value.userId
                        typeDbRef.child(id).child("comments").child(commentId).child("likes")
                            .child("users")
                            .updateChildren(map)
                        like.apply {
                            total = total?.plus(1)
                            users = users?.plus(userData.value.userId)
                        }
                    }
                }
        }

        fun sendLike(projectId: String, like: Likes, onSent: (Boolean, Likes) -> Unit): Likes {
            if (like.users?.contains(userData.value.userId.toString()) == true) {
                refProjects.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.minus(1)
                    )
                refProjects.child(projectId).child("projectData").child("likes").child("users")
                    .get().addOnSuccessListener {
                        it.children.forEach {
                            if (it.value == userData.value.userId) {
                                it.ref.removeValue()
                            }
                        }
                    }
                like.apply {
                    total = total?.minus(1)
                    users = users?.minus(userData.value.userId)
                }
                onSent(false, like)
            } else {
                refProjects.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.plus(1)
                    )
                var map: MutableMap<String?, Any> = LinkedHashMap()
                map[like.total?.plus(1).toString()] = userData.value.userId
                refProjects.child(projectId).child("projectData").child("likes").child("users")
                    .updateChildren(map)
                like.apply {
                    total = total?.plus(1)
                    users = users?.plus(userData.value.userId)
                }
                onSent(true, like)
            }
            return like
        }

        fun sendLikeBlog(projectId: String, like: Likes, onSent: (Boolean, Likes) -> Unit): Likes {
            if (like.users?.contains(userData.value.userId.toString()) == true) {
                refBlogs.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.minus(1)
                    )
                refBlogs.child(projectId).child("projectData").child("likes").child("users").get()
                    .addOnSuccessListener {
                        it.children.forEach {
                            if (it.value == userData.value.userId) {
                                it.ref.removeValue()
                            }
                        }
                    }
                like.apply {
                    total = total?.minus(1)
                    users = users?.minus(userData.value.userId)
                }
                onSent(false, like)
            } else {
                refBlogs.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.plus(1)
                    )
                var map: MutableMap<String?, Any> = LinkedHashMap()
                map[like.total?.plus(1).toString()] = userData.value.userId
                refBlogs.child(projectId).child("projectData").child("likes").child("users")
                    .updateChildren(map)
                like.apply {
                    total = total?.plus(1)
                    users = users?.plus(userData.value.userId)
                }
                onSent(true, like)
            }
            return like
        }

        fun createUser(currentUser: FirebaseUser) {
            val parsedUser = UserData(
                userId = currentUser.uid,
                username = "guest_" + RandomStringUtils.randomAlphanumeric(10),
                email = currentUser.email.toString(),
                profilePictureUrl = currentUser.photoUrl.toString(),
                bio = ""
            )
            refUsers.child(currentUser.uid).setValue(parsedUser)
        }

        fun deleteProject(id: String?, authorID: String?) {
            id?.let { refProjects.child(it).removeValue() }
            refProjectsInProgress.get().addOnSuccessListener {
                for (child in it.children) {
                    id?.let { path ->
                        child.child(path).ref.removeValue().addOnSuccessListener { }

                    }
                }
            }
            refUsers.child(userData.value.userId).child(PROJECTS).get().addOnSuccessListener {
                for (child in it.children) {
                    if (child.value.toString().contains(id.toString())) {
                        child.ref.removeValue()
                    }
                }
            }
        }

        fun deleteBlog(id: String?, authorID: String?) {
            id?.let { refBlogs.child(it).removeValue() }
            refUsers.child(authorID.toString()).child(BLOGS).get().addOnSuccessListener {
                for (child in it.children) {
                    if (child.value == id) {
                        child.ref.removeValue()
                    }
                }
            }
        }

        fun uploadUserInfoReg(parsedUser: UserData, go: () -> Unit) {
            if (parsedUser.username?.isEmpty() == true) {
                parsedUser.username = RandomStringUtils.random(10)
            }
            refUsers.child(parsedUser.userId).setValue(parsedUser)
                .addOnSuccessListener { go() }
        }

        fun collectCurrentUserProjectsWorks(onDataLoaded: (ProjectsArchive) -> Unit) {
            refProjectsInProgress.child(userData.value.userId).get().addOnSuccessListener {
                for (child in it.children) {
                    val project = child.getValue(ProjectsArchive::class.java)
                    onDataLoaded(project!!)
                }
            }
        }

        fun saveProjectProgress(project: ProjectsArchive, onDone: () -> Unit) {
            refProjectsInProgress.child(userData.value.userId)
                .child(project.project!!.projectData!!.projectId!!).setValue(project)
                .addOnSuccessListener {
                    currentProjectInProgress = null
                    onDone()
                }
        }

        fun startProject(project: Project, onDone: () -> Unit) {
            val projectInProgress = ProjectsArchive(
                project = project,
            )
            refProjectsInProgress.child(userData.value.userId)
                .child(project.projectData!!.projectId!!).setValue(projectInProgress)
                .addOnSuccessListener {
                    currentProjectInProgress = projectInProgress
                    onDone()
                }

        }

        fun checkExistUser(
            id: String,
            onExist: () -> Unit,
            onNotExist: () -> Unit,
            rememberId: (String) -> Unit
        ) {
            refUsers.child(id).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    onExist()
                } else {
                    onNotExist()
                }
                rememberId(id)
            }
        }

        fun getUser(id: String, onLoaded: (UserData) -> Unit) {
            refUsers.child(id).get().addOnSuccessListener {
                val user = it.getValue(UserData::class.java)
                user?.let { p1 -> onLoaded(p1) }
            }
        }

        fun updateUser(map: MutableMap<String, Any>) {
            refUsers.child(userData.value.userId).updateChildren(map)
            navController.popBackStack()
        }

        fun subscribe(authorId: String?, follows: Boolean, onDone: () -> Unit) {
            refUsers.child(authorId.toString()).child("subscribers").get().addOnSuccessListener {
                if (!follows) {
                    it.child(it.childrenCount.toString()).ref.setValue(userData.value.userId)
                } else {
                    for (child in it.children) {
                        if (child.value == userData.value.userId) {
                            child.ref.removeValue()
                            break
                        }
                    }
                }

            }
            refUsers.child(userData.value.userId).child("subscriptions").get()
                .addOnSuccessListener {
                    if (!follows) {
                        it.child(it.childrenCount.toString()).ref.setValue(authorId.toString())
                            .addOnSuccessListener {
                                onDone()
                            }
                    } else {
                        for (child in it.children) {
                            if (child.value == authorId.toString()) {
                                child.ref.removeValue()
                                onDone()
                                break
                            }
                        }
                    }
                }

        }

        fun sendMessage(chatId: String, message: Message) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val data = hashMapOf(
                "chatId" to message.id,
                "senderId" to currentUser?.uid,
                "senderName" to message.user.name + " " + message.user.lastName,
                "text" to message.text,
                "timestamp" to message.time,
                "profilePicture" to message.user.profilePictureUrl
            )
        }

        fun sendToken() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isSuccessful) {
                    refUsers.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child("token").setValue(it.result)
                }
            }
        }
    }
}