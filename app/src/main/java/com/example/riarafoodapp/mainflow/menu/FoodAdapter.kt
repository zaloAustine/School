package com.example.riarafoodapp.mainflow.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
            binding.foodTitle.text = sharedItem.desc
            binding.priceTitle.text = "Kes ${sharedItem.price}"
            Glide.with(binding.root).load(sharedItem.imageUrl).into(binding.imageView2)

            binding.addTOCart.setOnClickListener {
                onItemClick?.invoke(photosList[adapterPosition])
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