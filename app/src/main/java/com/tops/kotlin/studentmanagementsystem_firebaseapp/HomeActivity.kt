package com.tops.kotlin.studentmanagementsystem_firebaseapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tops.kotlin.studentmanagementsystem_firebaseapp.adapters.UserAdapter
import com.tops.kotlin.studentmanagementsystem_firebaseapp.databinding.ActivityHomeBinding
import com.tops.kotlin.studentmanagementsystem_firebaseapp.databinding.UserEditDialogBinding
import com.tops.kotlin.studentmanagementsystem_firebaseapp.models.User

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val courses =
        arrayOf("Select Course", "Android", "Flutter", "Java", "Python", "C++", "C", "C#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.roleSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("All", "Students", "Teachers")
            )

        firebaseAuth = FirebaseAuth.getInstance()

        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val database = FirebaseDatabase.getInstance()

        val studentsRef = database.getReference("students")
        val teachersRef = database.getReference("teachers")

        val users = mutableListOf<User>()

        binding.roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedRole = parent.getItemAtPosition(position).toString()
                users.clear()

                binding.progressBar.visibility = View.VISIBLE

                when (selectedRole) {
                    "All" -> {
                        binding.progressBar.visibility = View.VISIBLE
                        fetchAllUsers(studentsRef, teachersRef, users)
                    }

                    "Students" -> {
                        binding.progressBar.visibility = View.VISIBLE
                        fetchStudents(studentsRef, users)
                    }

                    "Teachers" -> {
                        binding.progressBar.visibility = View.VISIBLE
                        fetchTeachers(teachersRef, users)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }


        binding.roleSpinner.setSelection(0)
    }

    private fun fetchTeachers(teachersRef: DatabaseReference, users: MutableList<User>) {
        teachersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val teacher = child.getValue(User::class.java)
                    teacher?.let {
                        it.role = "Teacher"
                        users.add(it)
                    }
                }
                updateRecyclerView(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch teachers!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun fetchStudents(studentsRef: DatabaseReference, users: MutableList<User>) {
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val student = child.getValue(User::class.java)
                    student?.let {
                        it.role = "Student"
                        users.add(it)
                    }
                }
                updateRecyclerView(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch students!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun fetchAllUsers(
        studentsRef: DatabaseReference,
        teachersRef: DatabaseReference,
        users: MutableList<User>
    ) {
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                binding.progressBar.visibility = View.VISIBLE

                for (child in snapshot.children) {
                    val student = child.getValue(User::class.java)
                    student?.let {
                        it.role = "Student"
                        users.add(it)
                    }
                }

                teachersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            val teacher = child.getValue(User::class.java)
                            teacher?.let {
                                it.role = "Teacher"
                                users.add(it)
                            }
                        }
                        updateRecyclerView(users)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to fetch teachers!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch students!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun updateRecyclerView(users: MutableList<User>) {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        val adapter = UserAdapter(users, onItemClick = { user ->
            showEditDialog(user)
        })
        binding.rvUsers.adapter = adapter

        binding.progressBar.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun showEditDialog(user: User) {
        val dialog = Dialog(this)
        val dialogBinding = UserEditDialogBinding.inflate(LayoutInflater.from(this), null, false)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvUpdateRole.text = "Update ${user.role}"

        dialogBinding.etName.setText(user.name)
        dialogBinding.etContact.setText(user.contact)
        dialogBinding.etEmail.setText(user.email)
        when (user.gender) {
            "Male" -> dialogBinding.radioMale.isChecked = true
            "Female" -> dialogBinding.radioFemale.isChecked = true
        }
        dialogBinding.chkHindi.isChecked = user.languages?.contains("Hindi") == true
        dialogBinding.chkEnglish.isChecked = user.languages?.contains("English") == true
        dialogBinding.chkGujarati.isChecked = user.languages?.contains("Gujarati") == true
        dialogBinding.spCourse.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)
        dialogBinding.spCourse.setSelection(courses.indexOf(user.course))

        val databaseReference = if (user.role == "Student") {
            FirebaseDatabase.getInstance().getReference("students").child(user.id.toString())
        } else {
            FirebaseDatabase.getInstance().getReference("teachers").child(user.id.toString())
        }

        dialogBinding.btnUpdate.setOnClickListener {
            val name = dialogBinding.etName.text.toString().trim()
            val contact = dialogBinding.etContact.text.toString().trim()
            val email = dialogBinding.etEmail.text.toString().trim()
            val gender = when {
                dialogBinding.radioMale.isChecked -> "Male"
                dialogBinding.radioFemale.isChecked -> "Female"
                else -> ""
            }
            val languages = mutableListOf<String>().apply {
                if (dialogBinding.chkHindi.isChecked) add("Hindi")
                if (dialogBinding.chkEnglish.isChecked) add("English")
                if (dialogBinding.chkGujarati.isChecked) add("Gujarati")
            }
            val course = dialogBinding.spCourse.selectedItem.toString()
            val updatedUser = User(user.id, name, contact, email, gender, languages, course)
            databaseReference.setValue(updatedUser).addOnCompleteListener {
                Toast.makeText(this, "Updated Successfully!!!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update !!!", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnDelete.setOnClickListener {
            databaseReference.removeValue().addOnCompleteListener {
                Toast.makeText(this, "Deleted Successfully!!!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to delete !!!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            (resources.displayMetrics.heightPixels * 0.74).toInt()
        )
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                firebaseAuth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}