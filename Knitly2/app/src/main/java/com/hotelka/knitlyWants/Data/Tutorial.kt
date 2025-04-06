package com.hotelka.knitlyWants.Data

data class Tutorial(
    var id: String? = "",
    var cover: String? = "",
    var likes: Likes= Likes(),
    var reviews: Int = 0,
    var title: String? = "",
    var text: String? = "",
    var credits: String? = "",
    var authorId: String? = "",
    var background: String? = ""
) {
}