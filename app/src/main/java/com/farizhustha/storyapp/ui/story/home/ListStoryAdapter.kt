package com.farizhustha.storyapp.ui.story.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farizhustha.storyapp.databinding.ItemStoryBinding
import com.farizhustha.storyapp.service.remote.response.Story
import com.farizhustha.storyapp.utils.Utils

class ListStoryAdapter(private val listStory: List<Story>, private val onClick: (Story) -> Unit) :
    RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo: String? = listStory[position].photoUrl
        val name: String? = listStory[position].name
        val created: String? = listStory[position].createdAt

        holder.binding.apply {
            Glide.with(holder.itemView.context).load(photo).transform(RoundedCorners(10))
                .into(ivItemPhoto)

            tvItemName.text = name
            tvItemCreated.text = Utils.formatDate(created.toString())
        }

        holder.itemView.setOnClickListener {
            onClick(listStory[position])
        }
    }

    override fun getItemCount() = listStory.size

    inner class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

}