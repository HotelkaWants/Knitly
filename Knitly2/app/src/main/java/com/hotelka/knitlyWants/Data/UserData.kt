package com.hotelka.knitlyWants.Data

data class UserData(
    val userId: String = "",
    var username: String? = "",
    val name: String? = "",
    val lastName: String? = "",
    val email: String = "",
    val bio: String? = "",
    val profilePictureUrl: String? = "",
    val Projects: Map<String, String>? = mapOf(),
    val blogs: Map<String, String>? = mapOf(),
    val linkedAccountsId: String? = "",
    var subscribers: List<String>? = listOf(),
    var subscriptions: List<String>? = listOf(),
    var chats: List<String> = listOf(),
    var isOnline: Boolean = false,
    )
