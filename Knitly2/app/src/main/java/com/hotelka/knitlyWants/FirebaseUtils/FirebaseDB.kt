package com.hotelka.knitlyWants.FirebaseUtils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.currentProjectInProgress
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.userData
import org.apache.commons.lang3.RandomStringUtils


const val CHILD_USERS = "Users"
const val PROJECTS = "Projects"
const val BLOGS = "Blogs"
const val PROJECTS_IN_PROGRESS = "ProjectsInProgress"

class FirebaseDB {
    companion object {
        private val ref = FirebaseDatabase.getInstance().getReference()
        val refUsers = ref.child(CHILD_USERS)
        val refProjects = ref.child(PROJECTS)
        val refBlogs = ref.child(BLOGS)
        val refProjectsInProgress = ref.child(PROJECTS_IN_PROGRESS)

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
            navController.popBackStack()

        }
        fun storeBlog(blog: Blog, uniqueUUID: String){
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

        fun sendLike(projectId: String, like: Likes): Likes {
            if (like.users?.contains(userData.value.userId.toString()) == true) {
                refProjects.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.minus(1)
                    )
                refProjects.child(projectId).child("projectData").child("likes").child("users").get().addOnSuccessListener{
                    it.children.forEach {
                        if (it.value == userData.value.userId){
                            it.ref.removeValue()
                        }
                    }
                }
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
            }
            return like
        }
        fun sendLikeBlog(projectId: String, like: Likes): Likes {
            if (like.users?.contains(userData.value.userId.toString()) == true) {
                refBlogs.child(projectId).child("projectData").child("likes").child("total")
                    .setValue(
                        like.total?.minus(1)
                    )
                refBlogs.child(projectId).child("projectData").child("likes").child("users").get().addOnSuccessListener{
                    it.children.forEach {
                        if (it.value == userData.value.userId){
                            it.ref.removeValue()
                        }
                    }
                }
                like.apply {
                    total = total?.minus(1)
                    users = users?.minus(userData.value.userId)
                }
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
            }
            return like
        }

        fun createUser(currentUser: FirebaseUser) {
            val parsedUser = UserData(
                userId = currentUser.uid,
                username = "guest" + RandomStringUtils.randomAlphanumeric(10),
                email = currentUser.email.toString(),
                profilePictureUrl = currentUser.photoUrl.toString(),
                bio = ""
            )
            refUsers.child(currentUser.uid).setValue(parsedUser)
        }

        fun deleteProject(id: String?) {
            id?.let { refProjects.child(it).removeValue() }
            refProjectsInProgress.get().addOnSuccessListener {
                for (child in it.children) {
                    id?.let { path ->
                        child.child(path).ref.removeValue().addOnSuccessListener { }

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

        fun collectUsersRealtime(onDataLoaded: (UserData) -> Unit) {
            refUsers.get().addOnSuccessListener { snapshot ->
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(UserData::class.java)
                    onDataLoaded(item!!)
                }
            }
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

        fun checkExistUser(id: String, onExist:()-> Unit, onNotExist:() -> Unit) {
            refUsers.child(id).get().addOnSuccessListener{snapshot ->
                if (snapshot.exists()){
                    onExist()
                } else {
                    onNotExist()
                }
            }
        }

    }
}