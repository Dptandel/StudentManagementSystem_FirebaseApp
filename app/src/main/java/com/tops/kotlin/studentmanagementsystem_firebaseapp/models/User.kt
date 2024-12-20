package com.tops.kotlin.studentmanagementsystem_firebaseapp.models

data class User(
    val id: String? = null,
    val name: String? = null,
    val contact: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val languages: List<String>? = null,
    val course: String? = null,
    var role: String? = null
)
