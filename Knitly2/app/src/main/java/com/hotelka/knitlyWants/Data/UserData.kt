package com.hotelka.knitlyWants.Data

data class UserData(
    val userId: String = "",
    var username: String? = "",
    val name: String? = "",
    val lastName: String? = "",
    val email: String = "",
    val bio: String? = "",
    val profilePictureUrl: String? = "",
    val Projects: Map<String, String>? = null,
    val blogs: Map<String, String>? = null,
    val linkedAccountsId: String? = null,
    var subscribers: List<String>? = listOf(),
    var subscriptions: List<String>? = listOf(),
    )
