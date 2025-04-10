package com.hotelka.knitlyWants.SupportingDatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Comment
import com.hotelka.knitlyWants.Data.Detail
import com.hotelka.knitlyWants.Data.HistoryData
import com.hotelka.knitlyWants.Data.HistoryTutorialsData
import com.hotelka.knitlyWants.Data.DetailRows
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.ProjectData
import com.hotelka.knitlyWants.Data.ProjectsArchive
import com.hotelka.knitlyWants.Data.UserData
import kotlin.String

class SupportingDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "KnitlySupportDB"
        private const val DATABASE_VERSION = 16

        private const val TABLE_PROJECTS_IN_PROGRESS = "ProjectsInProgress"
        private const val KEY_PROJECT = "project"
        private const val KEY_PROGRESS = "progress"
        private const val KEY_TIME_IN_PROGRESS = "timeInProgress"
        private const val KEY_PROGRESS_DETAILS = "progressDetails"

        private const val TABLE_PROJECTS_DRAFT = "ProjectsDraft"
        private const val TABLE_PROJECTS = "Projects"
        private const val KEY_PROJECT_ID = "projectId"
        private const val KEY_DETAILS = "details"
        private const val KEY_PROJECT_DATA = "projectData"
        private const val KEY_CATEGORY = "category"
        private const val KEY_COMMENTS = "comments"
        private const val KEY_CREDITS = "credits"
        private const val KEY_PATTERN_ID = "patternId"
        private const val KEY_TOOL = "tool"
        private const val KEY_YARNS = "yarns"

        private const val TABLE_BLOGS = "Blogs"
        private const val TABLE_BLOGS_DRAFTS = "BlogsDrafts"
        private const val KEY_ADDITIONAL_IMAGES = "additionalImages"

        private const val TABLE_USERS = "Users"
        private const val KEY_USER_ID = "userId"
        private const val KEY_PROJECTS = "Projects"
        private const val KEY_BIO = "bio"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_LAST_NAME = "lastName"
        private const val KEY_SUBSCRIBERS = "subscribers"
        private const val KEY_BLOGS = "blogs"
        private const val KEY_SUBSCRIPTIONS = "subscriptions"
        private const val KEY_PROFILE_PICTURE_URL = "profilePictureUrl"
        private const val KEY_LINKED_ACCOUNT_ID = "linkedAccountsId"
        private const val KEY_USERNAME = "username"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ("
                + "$KEY_USER_ID TEXT PRIMARY KEY,"
                + "$KEY_USERNAME TEXT,"
                + "$KEY_NAME TEXT,"
                + "$KEY_LAST_NAME TEXT,"
                + "$KEY_EMAIL TEXT,"
                + "$KEY_BIO TEXT,"
                + "$KEY_PROFILE_PICTURE_URL TEXT,"
                + "$KEY_PROJECTS TEXT,"
                + "$KEY_BLOGS TEXT,"
                + "$KEY_LINKED_ACCOUNT_ID TEXT,"
                + "$KEY_SUBSCRIBERS TEXT,"
                + "$KEY_SUBSCRIPTIONS TEXT)")

        val CREATE_PROJECTS_IN_PROGRESS = ("CREATE TABLE $TABLE_PROJECTS_IN_PROGRESS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_PROJECT TEXT,"
                + "$KEY_PROGRESS FLOAT,"
                + "$KEY_TIME_IN_PROGRESS TEXT,"
                + "$KEY_PROGRESS_DETAILS TEXT)")

        val CREATE_PROJECTS_TABLE = ("CREATE TABLE $TABLE_PROJECTS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_DETAILS TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_TOOL TEXT,"
                + "$KEY_YARNS TEXT,"
                + "$KEY_CATEGORY TEXT,"
                + "$KEY_COMMENTS TEXT,"
                + "$KEY_CREDITS TEXT,"
                + "$KEY_PATTERN_ID TEXT)")

        val CREATE_BLOGS_TABLE = ("CREATE TABLE $TABLE_BLOGS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_ADDITIONAL_IMAGES TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_CATEGORY TEXT,"
                + "$KEY_COMMENTS TEXT,"
                + "$KEY_CREDITS TEXT)")

        val CREATE_BLOGS_DRAFTS_TABLE = ("CREATE TABLE $TABLE_BLOGS_DRAFTS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_ADDITIONAL_IMAGES TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_CATEGORY TEXT,"
                + "$KEY_CREDITS TEXT)")

        val CREATE_PROJECTS_TABLE_DRAFT = ("CREATE TABLE $TABLE_PROJECTS_DRAFT ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_DETAILS TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_TOOL TEXT,"
                + "$KEY_YARNS TEXT,"
                + "$KEY_CATEGORY TEXT,"
                + "$KEY_CREDITS TEXT,"
                + "$KEY_PATTERN_ID TEXT)")

        db?.execSQL(CREATE_PROJECTS_IN_PROGRESS)
        db?.execSQL(CREATE_BLOGS_TABLE)
        db?.execSQL(CREATE_BLOGS_DRAFTS_TABLE)
        db?.execSQL(CREATE_PROJECTS_TABLE)
        db?.execSQL(CREATE_PROJECTS_TABLE_DRAFT)
        db?.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS_IN_PROGRESS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BLOGS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BLOGS_DRAFTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS_DRAFT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }


    fun deleteUser(user: UserData): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USERS, "$KEY_USER_ID = ?", arrayOf(user.userId))
    }

    fun addUser(user: UserData): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, user.userId)
            put(KEY_USERNAME, user.username)
            put(KEY_NAME, user.name)
            put(KEY_LAST_NAME, user.lastName)
            put(KEY_EMAIL, user.email)
            put(KEY_BIO, user.bio)
            put(KEY_PROFILE_PICTURE_URL, user.profilePictureUrl)
            put(KEY_PROJECTS, Gson().toJson(user.Projects))
            put(KEY_BLOGS, Gson().toJson(user.blogs))
            put(KEY_LINKED_ACCOUNT_ID, user.linkedAccountsId)
            put(KEY_SUBSCRIBERS, Gson().toJson(user.subscribers))
            put(KEY_SUBSCRIPTIONS, Gson().toJson(user.subscriptions))
        }
        return db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getUser(userId: String): UserData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(
                KEY_USER_ID,
                KEY_USERNAME,
                KEY_NAME,
                KEY_LAST_NAME,
                KEY_EMAIL,
                KEY_BIO,
                KEY_PROFILE_PICTURE_URL,
                KEY_PROJECTS,
                KEY_BLOGS,
                KEY_LINKED_ACCOUNT_ID,
                KEY_SUBSCRIBERS,
                KEY_SUBSCRIPTIONS,
            ),
            "$KEY_USER_ID=?", arrayOf(userId), null, null, null, null
        )
        val typeTokenMap = object : TypeToken<Map<String, String>>() {}.type
        return if (cursor.moveToFirst()) {
            val userId: String = cursor.getString(0)
            var username: String? = cursor.getString(1)
            val name: String? = cursor.getString(2)
            val lastName: String? = cursor.getString(3)
            val email: String = cursor.getString(4)
            val bio: String? = cursor.getString(5)
            val profilePictureUrl: String? = cursor.getString(6)
            val Projects: Map<String, String>? = Gson().fromJson(cursor.getString(7), typeTokenMap)
            val blogs: Map<String, String>? = Gson().fromJson(cursor.getString(8), typeTokenMap)
            val linkedAccountsId: String? = cursor.getString(9)
            var subscribers: List<String>? =
                Gson().fromJson(cursor.getString(10), Array<String>::class.java).toList()
            var subscriptions: List<String>? =
                Gson().fromJson(cursor.getString(11), Array<String>::class.java).toList()
            UserData(
                userId = userId,
                username = username,
                name = name,
                lastName = lastName,
                email = email,
                bio = bio,
                profilePictureUrl = profilePictureUrl,
                Projects = Projects,
                blogs = blogs,
                linkedAccountsId = linkedAccountsId,
                subscribers = subscribers,
                subscriptions = subscriptions,
            )
        } else {
            null
        }
    }

    fun getAllUsers(): List<UserData> {
        val users = mutableListOf<UserData>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        val typeTokenMap = object : TypeToken<Map<String, String>>() {}.type

        if (cursor.moveToFirst()) {
            do {
                val userId: String = cursor.getString(0)
                var username: String? = cursor.getString(1)
                val name: String? = cursor.getString(2)
                val lastName: String? = cursor.getString(3)
                val email: String = cursor.getString(4)
                val bio: String? = cursor.getString(5)
                val profilePictureUrl: String? = cursor.getString(6)
                val Projects: Map<String, String>? =
                    Gson().fromJson(cursor.getString(7), typeTokenMap)
                val blogs: Map<String, String>? = Gson().fromJson(cursor.getString(8), typeTokenMap)
                val linkedAccountsId: String? = cursor.getString(9)
                var subscribers: List<String>? =
                    Gson().fromJson(cursor.getString(10), Array<String>::class.java).toList()
                var subscriptions: List<String>? =
                    Gson().fromJson(cursor.getString(11), Array<String>::class.java).toList()
                users.add(
                    UserData(
                        userId = userId,
                        username = username,
                        name = name,
                        lastName = lastName,
                        email = email,
                        bio = bio,
                        profilePictureUrl = profilePictureUrl,
                        Projects = Projects,
                        blogs = blogs,
                        linkedAccountsId = linkedAccountsId,
                        subscribers = subscribers,
                        subscriptions = subscriptions,
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }



    fun updateBlogDraft(blog: Blog): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_DATA, Gson().toJson(blog.projectData))
            put(KEY_ADDITIONAL_IMAGES, Gson().toJson(blog.additionalImages))
            put(KEY_CATEGORY, blog.category)
            put(KEY_CREDITS, blog.credits)
        }
        return db.update(
            TABLE_BLOGS_DRAFTS,
            values,
            "$KEY_PROJECT_ID = ?",
            arrayOf(blog.projectData!!.projectId)
        )
    }

    fun deleteBlogDraft(blog: Blog?): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_BLOGS_DRAFTS,
            "$KEY_PROJECT_ID = ?",
            arrayOf(blog?.projectData?.projectId)
        )
    }

    fun getAllBlogDrafts(): List<Blog> {
        val blogs = mutableListOf<Blog>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_BLOGS_DRAFTS", null)

        if (cursor.moveToFirst()) {
            do {
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val additionalImages =
                    Gson().fromJson(cursor.getString(1), Array<String?>::class.java).toMutableList()
                val category = cursor.getString(3)
                val credits = cursor.getString(4)
                blogs.add(
                    Blog(
                        additionalImages = additionalImages,
                        projectData = projectData,
                        category = category,
                        credits = credits
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return blogs
    }

    fun addBlogDraft(blog: Blog, blogId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_ID, blogId)
            put(KEY_ADDITIONAL_IMAGES, Gson().toJson(blog.additionalImages))
            put(KEY_PROJECT_DATA, Gson().toJson(blog.projectData))
            put(KEY_CATEGORY, blog.category)
            put(KEY_CREDITS, blog.category)
        }
        return db.insertWithOnConflict(TABLE_BLOGS_DRAFTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteBlog(blog: Blog?): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_BLOGS, "$KEY_PROJECT_ID = ?", arrayOf(blog?.projectData?.projectId))
    }

    fun getAllBlog(): List<Blog> {
        val blogs = mutableListOf<Blog>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_BLOGS", null)
        val typeTokenMap = object : TypeToken<MutableMap<String, Comment>>() {}.type

        if (cursor.moveToFirst()) {
            do {
                val additionalImages =
                    Gson().fromJson(cursor.getString(1), Array<String?>::class.java).toMutableList()
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val category = cursor.getString(3)
                val comments: MutableMap<String, Comment> = Gson().fromJson(cursor.getString(4), typeTokenMap)
                val credits = cursor.getString(5)
                blogs.add(
                    Blog(
                        additionalImages = additionalImages,
                        projectData = projectData,
                        category = category,
                        credits = credits,
                        comments = comments
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return blogs
    }

    fun addBlog(blog: Blog, blogId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_ID, blogId)
            put(KEY_ADDITIONAL_IMAGES, Gson().toJson(blog.additionalImages))
            put(KEY_PROJECT_DATA, Gson().toJson(blog.projectData))
            put(KEY_CATEGORY, blog.category)
            put(KEY_CREDITS, blog.credits)
            put(KEY_COMMENTS, Gson().toJson(blog.comments))
        }
        return db.insertWithOnConflict(TABLE_BLOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateDraft(project: Project): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DETAILS, Gson().toJson(project.details))
            put(KEY_PROJECT_DATA, Gson().toJson(project.projectData))
            put(KEY_TOOL, project.tool)
            put(KEY_YARNS, project.yarns)
            put(KEY_CATEGORY, project.category)
            put(KEY_CREDITS, project.credits)

        }
        return db.update(
            TABLE_PROJECTS_DRAFT,
            values,
            "$KEY_PROJECT_ID = ?",
            arrayOf(project.projectData!!.projectId)
        )
    }

    fun deleteDraft(project: Project?): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_PROJECTS_DRAFT,
            "$KEY_PROJECT_ID = ?",
            arrayOf(project?.projectData?.projectId)
        )
    }

    fun deleteProjectInProgress(project: ProjectsArchive?): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PROJECTS_IN_PROGRESS, "$KEY_PROJECT_ID = ?", arrayOf(project?.project?.projectData?.projectId))
    }

    fun getProjectInProgressExist(projectId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PROJECTS_IN_PROGRESS,
            arrayOf(),
            "$KEY_PROJECT_ID=?",
            arrayOf(projectId),
            null,
            null,
            null,
            null
        )
        return cursor.moveToFirst()
    }

    fun getAllProjectInProgress(): List<ProjectsArchive> {
        val projects = mutableListOf<ProjectsArchive>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_PROJECTS_IN_PROGRESS", null)

        if (cursor.moveToFirst()) {
            do {
                val project = Gson().fromJson(cursor.getString(1), Project::class.java)
                val progress =
                    Gson().fromJson(cursor.getString(2), Float::class.java)
                val timeInProgress = cursor.getString(3)
                val detailRows =  Gson().fromJson(cursor.getString(1), DetailRows::class.java)
                projects.add(
                    ProjectsArchive(
                        project = project,
                        progress = progress,
                        timeInProgress = timeInProgress,
                        detailRows = detailRows,
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

    fun addProjectInProgress(project: ProjectsArchive, projectId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_ID, projectId)
            put(KEY_PROJECT, Gson().toJson(project.project))
            put(KEY_PROGRESS, project.progress)
            put(KEY_TIME_IN_PROGRESS, project.timeInProgress)
            put(KEY_PROGRESS_DETAILS, Gson().toJson(project.detailRows))
        }
        return db.insertWithOnConflict(TABLE_PROJECTS_IN_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun addProjectDraft(project: Project, projectId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_ID, projectId)
            put(KEY_DETAILS, Gson().toJson(project.details))
            put(KEY_PROJECT_DATA, Gson().toJson(project.projectData))
            put(KEY_TOOL, project.tool)
            put(KEY_YARNS, project.yarns)
            put(KEY_CATEGORY, project.category)
            put(KEY_CREDITS, project.credits)
            put(KEY_PATTERN_ID, project.patternId)

        }
        return db.insertWithOnConflict(TABLE_PROJECTS_DRAFT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllProjectsDraft(): List<Project> {
        val projects = mutableListOf<Project>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_PROJECTS_DRAFT", null)

        if (cursor.moveToFirst()) {
            do {
                val sections =
                    Gson().fromJson(cursor.getString(1), Array<Detail>::class.java).toList()
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val tool = cursor.getString(3)
                val yarns = cursor.getString(4)
                val category = cursor.getString(5)
                val credits = cursor.getString(6)
                val patternId = cursor.getString(7)

                projects.add(
                    Project(
                        details = sections.toMutableList(),
                        projectData = projectData,
                        tool = tool,
                        yarns = yarns,
                        category = category,
                        credits = credits,
                        patternId = patternId
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

    fun updateProject(project: Project): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DETAILS, Gson().toJson(project.details))
            put(KEY_PROJECT_DATA, Gson().toJson(project.projectData))
            put(KEY_TOOL, project.tool)
            put(KEY_YARNS, project.yarns)
            put(KEY_CATEGORY, project.category)
            put(KEY_CREDITS, project.credits)
            put(KEY_COMMENTS, Gson().toJson(project.comments))
            put(KEY_PATTERN_ID, project.patternId)

        }
        return db.update(
            TABLE_PROJECTS,
            values,
            "$KEY_PROJECT_ID = ?",
            arrayOf(project.projectData!!.projectId)
        )
    }

    fun deleteProject(project: Project?): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_PROJECTS,
            "$KEY_PROJECT_ID = ?",
            arrayOf(project?.projectData?.projectId)
        )
    }

    fun addProject(project: Project, projectId: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_ID, projectId)
            put(KEY_DETAILS, Gson().toJson(project.details))
            put(KEY_PROJECT_DATA, Gson().toJson(project.projectData))
            put(KEY_CATEGORY, project.category)
            put(KEY_TOOL, project.tool)
            put(KEY_YARNS, project.yarns)
            put(KEY_CREDITS, project.credits)
            put(KEY_COMMENTS, Gson().toJson(project.comments))
            put(KEY_PATTERN_ID,  project.patternId)

        }
        return db.insertWithOnConflict(TABLE_PROJECTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getProject(projectId: String): Project? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PROJECTS,
            arrayOf(KEY_DETAILS, KEY_PROJECT_DATA, KEY_TOOL, KEY_YARNS, KEY_CATEGORY, KEY_CREDITS, KEY_COMMENTS, KEY_PATTERN_ID),
            "$KEY_PROJECT_ID=?",
            arrayOf(projectId),
            null,
            null,
            null,
            null
        )
        val typeTokenMap = object : TypeToken<MutableMap<String, Comment>>() {}.type

        return if (cursor.moveToFirst()) {
            val sections = Gson().fromJson(cursor.getString(0), Array<Detail>::class.java).toList()
            val projectData = Gson().fromJson(cursor.getString(1), ProjectData::class.java)
            val tool = cursor.getString(2)
            val yarns = cursor.getString(3)
            val category = cursor.getString(4)
            val credits = cursor.getString(5)
            val comments: MutableMap<String, Comment> = Gson().fromJson(cursor.getString(6), typeTokenMap)
            val patternId = cursor.getString(7)

            Project(
                details = sections.toMutableList(),
                projectData = projectData,
                tool = tool,
                yarns = yarns,
                category = category,
                credits = credits,
                comments = comments,
                patternId = patternId
            )
        } else {
            null
        }
    }

    fun getAllProjects(): List<Project> {
        val projects = mutableListOf<Project>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_PROJECTS", null)
        val typeTokenMap = object : TypeToken<MutableMap<String, Comment>>() {}.type

        if (cursor.moveToFirst()) {
            do {
                val sections =
                    Gson().fromJson(cursor.getString(1), Array<Detail>::class.java).toList()
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val tool = cursor.getString(3)
                val yarns = cursor.getString(4)
                val category = cursor.getString(5)
                val comments: MutableMap<String, Comment> = Gson().fromJson(cursor.getString(6), typeTokenMap)
                val credits = cursor.getString(7)
                val patternId = cursor.getString(8)

                projects.add(
                    Project(
                        details = sections.toMutableList(),
                        projectData = projectData,
                        tool = tool,
                        yarns = yarns,
                        category = category,
                        comments = comments,
                        credits = credits,
                        patternId = patternId
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

}
