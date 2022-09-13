package org.firmanmardiyanto.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.firmanmardiyanto.core.databinding.ItemListStoryBinding
import org.firmanmardiyanto.core.domain.model.Story

class StoriesAdapter : PagingDataAdapter<Story, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var onItemClick: ((Story, ItemListStoryBinding) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class MyViewHolder(private val binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemImage)
                ivItemImage.transitionName = "image_${story.id}"
                tvItemName.text = story.name
                tvItemName.transitionName = "name_${story.id}"
                tvItemDescription.text = story.description
                tvItemDescription.transitionName = "description_${story.id}"
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(story, binding)
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