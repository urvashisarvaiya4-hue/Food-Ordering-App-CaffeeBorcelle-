package com.example.caffeeborcelle.MyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.ActivityProductSearchBinding
import com.example.caffeeborcelle.model.ProductEntity

class SearchAdapter (
    private var items: List<ProductEntity>,
    private val onItemClick: (ProductEntity) -> Unit,
    private val onAddToCartClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ActivityProductSearchBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivityProductSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = items[position]

        holder.binding.productNameTextView.text = product.name
        holder.binding.productPriceTextView.text = "₹${product.price}"

        // 🔥 Load image from Firebase / Cloudinary
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(holder.binding.productImageView)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }

        holder.binding.addToCartButton.setOnClickListener {
            onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}