package com.example.riarafoodapp.mainflow.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.riarafoodapp.data.Food
import com.example.riarafoodapp.databinding.LayoutFoodBinding

class FoodAdapter(
    val onItemClick: ((Food) -> Unit)?
) :
    ListAdapter<Food, FoodAdapter.SharedAssetViewHolder>(ShareItemDiffCallBack()), Filterable {

    var photosList: MutableList<Food> = mutableListOf()
    var photosListFiltered: MutableList<Food> = mutableListOf()

    inner class SharedAssetViewHolder(private val binding: LayoutFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sharedItem: Food) {

            binding.foodTitle.text = sharedItem.desc

            //binding.description.text = "You shared this asset to ${sharedItem.user?.fname}"
            // Glide.with(binding.root).load(ApiConstants.IMAGE_URL +sharedItem.item.imageurl1).into(binding.assetImg)

            itemView.setOnClickListener {
                onItemClick?.invoke(photosListFiltered[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedAssetViewHolder {
        val view = LayoutFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return SharedAssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SharedAssetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter():Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) photosListFiltered = photosList else {
                    val filteredList = ArrayList<Food>()
                    photosList
                        .filter {
                            (it.id.contains(constraint!!)) or
                                    (it.desc.contains(constraint))

                        }
                        .forEach { filteredList.add(it) }
                    photosListFiltered = filteredList

                }
                return FilterResults().apply { values = photosListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                photosListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Food>
                submitList(photosListFiltered)
                notifyDataSetChanged()
            }
        }
    }
}

class ShareItemDiffCallBack : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
        oldItem.id == newItem.id
}