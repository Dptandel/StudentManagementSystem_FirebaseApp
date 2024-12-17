package com.tops.kotlin.studentmanagementsystem_firebaseapp.models

data class Student(
    val id: String? = null,
    val name: String? = null,
    val contact: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val languages: List<String>? = null,
    val course: String? = null
)
