package com.example.caffeeborcelle.MyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.ActivityWishlistItemsBlueprintBinding
import com.example.caffeeborcelle.model.WishlistEntity

class WishlistAdapter (
    private val list: MutableList<WishlistEntity>,
    private val onItemClick: (WishlistEntity) -> Unit,
    private val onRemoveClick: (WishlistEntity) -> Unit,
    private val onItemLongPress: ((WishlistEntity) -> Unit)? = null
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ActivityWishlistItemsBlueprintBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivityWishlistItemsBlueprintBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.WishlistProductName.text = item.name
        holder.binding.WishlistProductPrice.text = "₹${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.binding.WishlistItemImage)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.binding.WishlistRemoveIcon.setOnClickListener {
            onRemoveClick(item)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongPress?.invoke(item)
            true
        }
    }

    override fun getItemCount(): Int = list.size
}