package com.dwarfkit.storilia.pkg.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dwarfkit.storilia.R
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.databinding.ActivityDetailBinding
import com.dwarfkit.storilia.pkg.main.MainActivity

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_STORY_DETAIL ="EXTRA_STORY_DETAIL"
    }

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val data = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY_DETAIL)
        loadData(data)
        binding.ivArrowBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun loadData(data: StoryEntity?) {
        if (data != null){
            binding.apply {
                val stringData = getString(R.string.text_capture_by,data.name)
                tvUserStory.text = stringData
                tvDesc.text = data.description

                Glide.with(this@DetailActivity)
                    .load(data.photoUrl)
                    .into(ivImageStory)
            }
        }
    }
}