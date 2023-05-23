package com.farizhustha.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farizhustha.storyapp.databinding.ItemStoryBinding
import com.farizhustha.storyapp.service.remote.response.Story
import com.farizhustha.storyapp.utils.Utils

class ListStoryAdapter(private val onClick: (Story) -> Unit) :
    PagingDataAdapter<Story, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                onClick(data)
            }
        }
    }

    inner class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            binding.apply {
                Glide.with(itemView.context).load(data.photoUrl).transform(RoundedCorners(10))
                    .into(ivItemPhoto)

                tvItemName.text = data.name
                tvItemCreated.text = Utils.formatDate(data.createdAt.toString())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}