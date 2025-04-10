package com.hotelka.knitlyWants.Data

import androidx.compose.ui.graphics.Color
import com.hotelka.knitlyWants.nav.StitchType
import java.security.Timestamp

data class CellData(
    val stitchType: StitchType? = StitchType.None,
    val color: Color? = Color.White
) {
    constructor(): this(null,null)
}
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

data class PatternData(
    var rows: Int = 10,
    var columns: Int = 10,
    var gridState: List<CellData> = listOf(),
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
    val date: Long? = 0L,
    val description: String = "Description",
    var authorID: String? = "",
    var reviews: Int = 0,
    var likes: Likes = Likes(),
    val cover: String? = ""
)

data class Comment(
    val id: String? = "",
    val text: String? = "",
    val userId: String? = "",
    val timestamp: Long? = 0L,
    var likes: Likes = Likes(),
    var additionalImages: MutableList<String?>? = mutableListOf(),
    var replies: MutableMap<String, Comment>? = LinkedHashMap(),
)
data class Likes(
    var total: Int? = 0,
    var users: HashMap<String, Boolean>? = LinkedHashMap()
)

data class Project(
    val category: String? = "",
    val credits: String? = "",
    val projectData: ProjectData? = ProjectData(),
    val tool: String? = "0.0",
    val yarns: String? = "",
    var comments: MutableMap<String, Comment>? = LinkedHashMap(),
    var details: MutableList<Detail>? = mutableListOf(),
    var patternId: String? = "",
)

data class ProjectsArchive(
    val project: Project? = Project(),
    var progress: Float = 0f,
    var timeInProgress: String? = "0",
    var detailRows: DetailRows? = DetailRows()
)

data class DetailRows(var detail: Int? = 0, var row: Int? = 0)
data class Chat(
    val id: String? = "",
    val users: List<String>? = listOf(),
    var messages: Map<String, Message> = LinkedHashMap(),
)
data class Message(
    var messageReplyTo: String? = "",
    var replyTo: String? = "",
    var id: String? = "",
    var user: String? = "",
    var text: String? = "",
    var additionalImages: MutableList<String> = mutableListOf(),
    var time: Long = 0L,
    var isChecked: Boolean = false,
    var edited: Boolean? = false,

)