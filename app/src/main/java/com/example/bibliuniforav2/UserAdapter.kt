package com.example.bibliuniforav2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private var users: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var filteredUsers: List<Usuario> = users

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userNameTextView)
        val userProfile: TextView = itemView.findViewById(R.id.userProfileTextView)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredUsers[position]

        holder.userName.text = user.nome
        holder.userProfile.text = user.perfil

        holder.editButton.setOnClickListener {
            onEditClick(user)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(user)
        }
    }

    override fun getItemCount(): Int = filteredUsers.size

    fun filter(query: String) {
        filteredUsers = if (query.isEmpty()) {
            users
        } else {
            users.filter { user ->
                user.nome.contains(query, true) ||
                        user.perfil.contains(query, true) ||
                        user.usuario.contains(query, true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateUsers(newUsers: List<Usuario>) {
        users = newUsers
        filteredUsers = newUsers
        notifyDataSetChanged()
    }
}