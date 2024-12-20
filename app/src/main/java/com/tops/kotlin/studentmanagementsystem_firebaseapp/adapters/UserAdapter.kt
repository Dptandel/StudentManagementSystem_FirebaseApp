package com.tops.kotlin.studentmanagementsystem_firebaseapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tops.kotlin.studentmanagementsystem_firebaseapp.databinding.ItemUserBinding
import com.tops.kotlin.studentmanagementsystem_firebaseapp.models.User

class UserAdapter(
    private val users: MutableList<User>,
    private val onItemClick: ((user: User) -> Unit)? = null
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvUsername.text = user.name
        holder.binding.tvEmail.text = user.email
        holder.binding.tvCourse.text = user.course

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(user)
        }
    }
}
