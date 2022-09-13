package org.firmanmardiyanto.storyapp.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<Story>(EXTRA_DATA)
        showDetailStory(detailStory)
    }

    private fun showDetailStory(detailStory: Story?) {
        detailStory?.let {
            with(binding) {
                content.tvDetailDescription.text = it.description
                toolbar.title = it.name
                setSupportActionBar(toolbar)
                Glide.with(this@DetailStoryActivity)
                    .load(detailStory.photoUrl)
                    .into(ivDetailImage)
            }
        }
    }
}