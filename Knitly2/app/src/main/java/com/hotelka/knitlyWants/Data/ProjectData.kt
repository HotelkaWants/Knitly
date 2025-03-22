package com.hotelka.knitlyWants.Data

class Category() {

    companion object {
        val Crocheting = "Crocheting"
        val Knitting = "Knitting"
        val Blog = "Blog"


        val allCategories = listOf(Crocheting, Knitting, Blog)
    }
}

data class Blog(
    var projectData: ProjectData? = ProjectData(),
    var additionalImages: MutableList<String?> = mutableListOf(),
    val category: String? = Category.Blog,
    val credits: String?="",
    var comments: MutableMap<String, Comment>? = LinkedHashMap(),

    )


data class Note(var text: String? = "", var imageUrl: MutableList<String?> = mutableListOf())
data class RowCrochet(
    var description: String? = "",
    var note: Note? = Note(),
    var noteAdded: Boolean = false,
    var total: Int? = 0
)

data class Detail(var title: String? = "", var rows: MutableList<RowCrochet> = mutableListOf())

data class ProjectData(
    val projectId: String? = "",
    val title: String? = "",
    val date: String = "01.01.1999",
    val description: String = "Description",
    val author: String? = "",
    var authorID: String? = "",
    var reviews: Int = 0,
    var likes: Likes = Likes(),
    val cover: String? = ""
) {}

data class Comment(
    val id: String? = "",
    val text: String? = "",
    val userId: String? = "",
    val timestamp: String? = "",
    var likes: Likes = Likes(),
)
data class Likes(
    var total: Int? = 0,
    var users: List<String>? = listOf()
)

data class Project(
    val category: String? = "",
    val credits: String? = "",
    val projectData: ProjectData? = ProjectData(),
    val tool: String? = "0.0",
    val yarns: String? = "",
    var comments: MutableMap<String, Comment>? = LinkedHashMap(),
    var details: MutableList<Detail>? = mutableListOf(),
)

data class ProjectsArchive(
    val project: Project? = Project(),
    var progress: Float = 0f,
    var timeInProgress: String? = "0",
    var progressDetails: ProgressDetails? = ProgressDetails()
)

data class ProgressDetails(var detail: Int? = 0, var row: Int? = 0)