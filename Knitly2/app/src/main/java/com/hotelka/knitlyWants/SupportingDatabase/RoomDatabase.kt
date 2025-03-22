package com.hotelka.knitlyWants.SupportingDatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.Detail
import com.hotelka.knitlyWants.Data.HistoryData
import com.hotelka.knitlyWants.Data.HistoryTutorialsData
import com.hotelka.knitlyWants.Data.ProjectData

class RoomDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "KnitlyDB"
        private const val DATABASE_VERSION = 4

        private const val TABLE_PROJECTS_DRAFT = "Projects_Draft"
        private const val TABLE_PROJECTS = "Projects"
        private const val KEY_PROJECT_ID = "projectId"
        private const val KEY_DETAILS = "details"
        private const val KEY_PROJECT_DATA = "projectData"
        private const val KEY_CATEGORY = "category"
        private const val KEY_TOOL = "tool"
        private const val KEY_YARNS = "yarns"

        private const val TABLE_BLOGS_DRAFTS = "Blogs_Drafts"
        private const val KEY_ADDITIONAL_IMAGES = "additionalImages"

        private const val TABLE_HISTORY_TUTORIALS = "HistoryTutorials"
        private const val TABLE_HISTORY = "History"
        private const val KEY_HISTORY_ID = "historyId"
        private const val KEY_HISTORY_TITLE = "selectedResult"
        private const val KEY_RESULT_TYPE = "selectedResultType"

        private const val TABLE_USERS = "Users"
        private const val KEY_USER_ID = "userId"
        private const val KEY_PROJECTS = "Projects"
        private const val KEY_BIO = "bio"
        private const val KEY_EMAIL = "email"
        private const val KEY_LAST_NAME = "lastName"
        private const val KEY_NAME = "name"
        private const val KEY_PROFILE_PICTURE_URL = "profilePictureUrl"
        private const val KEY_USERNAME = "username"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PROJECTS_TABLE = ("CREATE TABLE $TABLE_PROJECTS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_DETAILS TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_TOOL TEXT,"
                + "$KEY_YARNS TEXT,"
                + "$KEY_CATEGORY TEXT)")

        val CREATE_BLOGS_DRAFTS_TABLE = ("CREATE TABLE $TABLE_BLOGS_DRAFTS ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_ADDITIONAL_IMAGES TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_CATEGORY TEXT)")

        val CREATE_PROJECTS_TABLE_DRAFT = ("CREATE TABLE $TABLE_PROJECTS_DRAFT ("
                + "$KEY_PROJECT_ID TEXT PRIMARY KEY,"
                + "$KEY_DETAILS TEXT,"
                + "$KEY_PROJECT_DATA TEXT,"
                + "$KEY_TOOL TEXT,"
                + "$KEY_YARNS TEXT,"
                + "$KEY_CATEGORY TEXT)")

        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ("
                + "$KEY_USER_ID TEXT PRIMARY KEY,"
                + "$KEY_PROJECTS TEXT,"
                + "$KEY_BIO TEXT,"
                + "$KEY_EMAIL TEXT,"
                + "$KEY_LAST_NAME TEXT,"
                + "$KEY_NAME TEXT,"
                + "$KEY_PROFILE_PICTURE_URL TEXT,"
                + "$KEY_USERNAME TEXT)")
        val CREATE_HISTORY_TABLE = ("CREATE TABLE $TABLE_HISTORY ("
                + "$KEY_HISTORY_ID TEXT PRIMARY KEY,"
                + "$KEY_RESULT_TYPE TEXT,"
                + "$KEY_HISTORY_TITLE TEXT)")

        val CREATE_HISTORY_TUTORIALS_TABLE = ("CREATE TABLE $TABLE_HISTORY_TUTORIALS ("
                + "$KEY_HISTORY_ID TEXT PRIMARY KEY,"
                + "$KEY_RESULT_TYPE TEXT,"
                + "$KEY_HISTORY_TITLE TEXT)")

        db?.execSQL(CREATE_BLOGS_DRAFTS_TABLE)
        db?.execSQL(CREATE_PROJECTS_TABLE)
        db?.execSQL(CREATE_HISTORY_TABLE)
        db?.execSQL(CREATE_HISTORY_TUTORIALS_TABLE)
        db?.execSQL(CREATE_PROJECTS_TABLE_DRAFT)
        db?.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BLOGS_DRAFTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS_DRAFT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY_TUTORIALS")
        onCreate(db)
    }
    fun addHistory(selectedResult: String, type: String, idHistory: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_HISTORY_ID, idHistory)
            put(KEY_HISTORY_TITLE, selectedResult)
            put(KEY_RESULT_TYPE, type)

        }
        return db.insert(TABLE_HISTORY, null, values)
    }
    fun getHistoryList(): MutableList<HistoryData> {
        val history = mutableListOf<HistoryData>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val historyTitle = cursor.getString(1)
                val resultType = cursor.getString(2)
                history.add(
                    HistoryData(
                        id = id,
                        historyTitle = historyTitle,
                        resultType = resultType
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return history
    }
    fun getHistoryTutorialsList(): MutableList<HistoryTutorialsData> {
        val history = mutableListOf<HistoryTutorialsData>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY_TUTORIALS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0)
                val historyTitle = cursor.getString(1)
                val resultType = cursor.getString(2)
                history.add(
                    HistoryTutorialsData(
                        id = id,
                        historyTitle = historyTitle,
                        resultType = resultType
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return history
    }
    fun addHistoryTutorials(selectedResult: String, type: String, idHistory: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_HISTORY_ID, idHistory)
            put(KEY_HISTORY_TITLE, selectedResult)
            put(KEY_RESULT_TYPE, type)

        }
        return db.insert(TABLE_HISTORY_TUTORIALS, null, values)
    }
    fun getHistory(historyId: String): HistoryData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_HISTORY, arrayOf(KEY_HISTORY_ID, KEY_HISTORY_TITLE, KEY_RESULT_TYPE),
            "$KEY_HISTORY_ID=?", arrayOf(historyId), null, null, null, null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getString(0)
            val historyTitle = cursor.getString(1)
            val resultType = cursor.getString(2)
            HistoryData(
                id = id,
                historyTitle = historyTitle,
                resultType = resultType
            )
        } else {
            null
        }
    }

    fun updateBlogDraft(blog: Blog): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PROJECT_DATA, Gson().toJson(blog.projectData))
            put(KEY_ADDITIONAL_IMAGES, Gson().toJson(blog.additionalImages))
            put(KEY_CATEGORY, blog.category)
        }
        return db.update(TABLE_BLOGS_DRAFTS, values, "$KEY_PROJECT_ID = ?", arrayOf(blog.projectData!!.projectId))
    }
    fun deleteBlogDraft(blog: Blog?): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_BLOGS_DRAFTS, "$KEY_PROJECT_ID = ?", arrayOf(blog?.projectData?.projectId))
    }
    fun getAllBlogDrafts(): List<Blog> {
        val blogs = mutableListOf<Blog>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_BLOGS_DRAFTS", null)

        if (cursor.moveToFirst()) {
            do {
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val additionalImages =  Gson().fromJson(cursor.getString(1), Array<String?>::class.java).toMutableList()
                val category = cursor.getString(3)
                blogs.add(Blog(additionalImages = additionalImages, projectData = projectData, category = category))
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
        }
        return db.insert(TABLE_BLOGS_DRAFTS, null, values)
    }

    fun updateDraft(project: Project): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DETAILS, Gson().toJson(project.details))
            put(KEY_PROJECT_DATA, Gson().toJson(project.projectData))
            put(KEY_TOOL, project.tool)
            put(KEY_YARNS, project.yarns)
            put(KEY_CATEGORY, project.category)
        }
        return db.update(TABLE_PROJECTS_DRAFT, values, "$KEY_PROJECT_ID = ?", arrayOf(project.projectData!!.projectId))
    }

    fun deleteDraft(project: Project?): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PROJECTS_DRAFT, "$KEY_PROJECT_ID = ?", arrayOf(project?.projectData?.projectId))
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
        }
        return db.insert(TABLE_PROJECTS_DRAFT, null, values)
    }
    fun getProjectDraft(projectId: String): Project? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PROJECTS_DRAFT, arrayOf(KEY_DETAILS, KEY_PROJECT_DATA, KEY_TOOL, KEY_YARNS, KEY_CATEGORY),
            "$KEY_PROJECT_ID=?", arrayOf(projectId), null, null, null, null
        )
        return if (cursor.moveToFirst()) {
            val sections = Gson().fromJson(cursor.getString(0), Array<Detail>::class.java).toList()
            val projectData = Gson().fromJson(cursor.getString(1), ProjectData::class.java)
            val tool = cursor.getString(2)
            val yarns = cursor.getString(3)
            val category = cursor.getString(4)
            Project(
                details = sections.toMutableList(),
                projectData = projectData,
                tool = tool,
                yarns = yarns,
                category =  category
            )
        } else {
            null
        }
    }
    fun getAllProjectsDraft(): List<Project> {
        val projects = mutableListOf<Project>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_PROJECTS_DRAFT", null)

        if (cursor.moveToFirst()) {
            do {
                val sections = Gson().fromJson(cursor.getString(1), Array<Detail>::class.java).toList()
                val projectData = Gson().fromJson(cursor.getString(2), ProjectData::class.java)
                val tool = cursor.getString(3)
                val yarns = cursor.getString(4)
                val category = cursor.getString(5)
                projects.add(Project(details = sections.toMutableList(), projectData = projectData, tool = tool, yarns = yarns, category = category))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return projects
    }

}
