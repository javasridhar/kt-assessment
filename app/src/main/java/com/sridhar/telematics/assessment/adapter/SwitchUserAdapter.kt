package com.sridhar.telematics.assessment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sridhar.telematics.assessment.databinding.UserListItemBinding
import com.sridhar.telematics.assessment.model.entity.RealmUser

class SwitchUserAdapter(private val onUserSelected: (RealmUser) -> Unit) : RecyclerView.Adapter<SwitchUserAdapter.ViewHolder>() {

    private var list: MutableList<RealmUser> = mutableListOf()
    private lateinit var binding: UserListItemBinding

    inner class ViewHolder(private val binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: RealmUser) {
            binding.root.setOnClickListener { onUserSelected(user) }
            binding.user = user
        }
    }

    fun add(user: RealmUser) {
        list.add(user)
        notifyItemInserted(list.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.bind(user)
    }
}