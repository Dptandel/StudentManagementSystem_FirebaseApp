package com.tops.kotlin.studentmanagementsystem_firebaseapp

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tops.kotlin.studentmanagementsystem_firebaseapp.databinding.ActivitySignUpBinding
import com.tops.kotlin.studentmanagementsystem_firebaseapp.models.Student

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private val courses =
        arrayOf("Select Course", "Android", "Flutter", "Java", "Python", "C++", "C", "C#")

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().getReference("students")

        binding.spCourse.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, courses)

        binding.btnRegister.setOnClickListener {
            val id = databaseReference.push().key!!
            val name = binding.etName.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val gender = if (binding.radioMale.isChecked) "Male" else "Female"
            val languages = mutableListOf<String>()
            if (binding.chkHindi.isChecked) languages.add("Hindi")
            if (binding.chkEnglish.isChecked) languages.add("English")
            if (binding.chkGujarati.isChecked) languages.add("Gujarati")
            val course = binding.spCourse.selectedItem.toString()

            saveStudent(id, name, contact, email, password, gender, languages, course)
        }

    }

    private fun saveStudent(
        id: String,
        name: String,
        contact: String,
        email: String,
        password: String,
        gender: String,
        languages: MutableList<String>,
        course: String
    ) {

        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name"
        }
        if (contact.isEmpty()) {
            binding.etContact.error = "Please enter your contact"
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Please enter your email"
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Please enter your password"
        }
        if (!binding.radioMale.isChecked && !binding.radioFemale.isChecked) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
        }
        if (languages.isEmpty()) {
            Toast.makeText(this, "Please select your languages", Toast.LENGTH_SHORT).show()
        }
        if (binding.spCourse.selectedItem == "Select Course") {
            Toast.makeText(this, "Please select your course", Toast.LENGTH_SHORT).show()
        }

        if (name.isNotEmpty() && contact.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && gender.isNotEmpty() && languages.isNotEmpty() && course != "Select Course" ) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        val student = Student(id, name, contact, email, gender, languages, course)
                        databaseReference.child(id).setValue(student).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this, "Account Created Successfully!!!", Toast.LENGTH_SHORT
                                ).show()
                                binding.etName.text.clear()
                                binding.etContact.text.clear()
                                binding.etEmail.text.clear()
                                binding.etPassword.text.clear()
                                binding.radioMale.isChecked = false
                                binding.radioFemale.isChecked = false
                                binding.chkHindi.isChecked = false
                                binding.chkEnglish.isChecked = false
                                binding.chkGujarati.isChecked = false
                                binding.spCourse.setSelection(0)
                                resetFocus()
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Empty Fields Are Not Allowed!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetFocus() {
        binding.etName.clearFocus()
        binding.etContact.clearFocus()
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()
        binding.radioMale.clearFocus()
        binding.radioFemale.clearFocus()
        binding.chkHindi.clearFocus()
        binding.chkEnglish.clearFocus()
        binding.chkGujarati.clearFocus()
        binding.spCourse.clearFocus()
    }
}