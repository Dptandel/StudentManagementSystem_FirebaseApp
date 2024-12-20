package com.tops.kotlin.studentmanagementsystem_firebaseapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tops.kotlin.studentmanagementsystem_firebaseapp.databinding.ActivitySignUpBinding
import com.tops.kotlin.studentmanagementsystem_firebaseapp.models.User

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

        binding.spCourse.adapter =
            ArrayAdapter(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                courses
            )

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            databaseReference = when {
                binding.radioStudent.isChecked -> FirebaseDatabase.getInstance()
                    .getReference("students")

                binding.radioTeacher.isChecked -> FirebaseDatabase.getInstance()
                    .getReference("teachers")

                else -> {
                    Toast.makeText(this, "Please Select Student or Teacher!!!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }
            val id = databaseReference.push().key!!
            val name = binding.etName.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val gender = when {
                binding.radioMale.isChecked -> "Male"
                binding.radioFemale.isChecked -> "Female"
                else -> ""
            }
            val languages = mutableListOf<String>().apply {
                if (binding.chkHindi.isChecked) add("Hindi")
                if (binding.chkEnglish.isChecked) add("English")
                if (binding.chkGujarati.isChecked) add("Gujarati")
            }
            val course = binding.spCourse.selectedItem.toString()

            saveRecord(id, name, contact, email, password, gender, languages, course)
        }

    }

    private fun saveRecord(
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

        if (name.isNotEmpty() && contact.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && gender.isNotEmpty() && languages.isNotEmpty() && course != "Select Course") {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        val user = User(id, name, contact, email, gender, languages, course)
                        databaseReference.child(id).setValue(user).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this, "Account Created Successfully!!!", Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                                resetFocus()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
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

    private fun clearFields() {
        binding.etName.text?.clear()
        binding.etContact.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.radioMale.isChecked = false
        binding.radioFemale.isChecked = false
        binding.chkHindi.isChecked = false
        binding.chkEnglish.isChecked = false
        binding.chkGujarati.isChecked = false
        binding.spCourse.setSelection(0)
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