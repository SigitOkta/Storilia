package com.dwarfkit.storilia.pkg.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.databinding.ItemStoryBinding
import com.dwarfkit.storilia.utils.getAddress
import com.dwarfkit.storilia.utils.toTimeAgo
import javax.sql.CommonDataSource

class StoriesAdapter : PagingDataAdapter<StoryEntity, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: StoryEntity?){
            binding.apply {
                Glide.with(itemView.context)
                    .load(stories?.photoUrl)
                    .into(ivItemStory)
                tvUserStory.text = stories?.name
                if (stories != null) {
                    tvLocation.text = getAddress(stories.lat,stories.lon,itemView.context)
                }
                tvTime.text = stories?.createdAt?.toTimeAgo()
                //tvLocation.text = getAddress(stories.lat,stories.lon)
              /*  itemView.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra(EXTRA_STORY, stories)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemStory, "profile"),
                            Pair(tvUserStory, "name"),
                            Pair(vGradien, "customView"),
                        )
                    itemView.context.startActivity(intent,optionsCompat.toBundle())
                }*/
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories)
    }
    companion object{
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>(){
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}