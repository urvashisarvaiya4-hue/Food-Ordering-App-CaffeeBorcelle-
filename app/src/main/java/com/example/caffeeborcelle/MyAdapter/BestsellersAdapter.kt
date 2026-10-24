package com.example.caffeeborcelle.MyAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.model.ProductEntity
import com.example.caffeeborcelle.ItemDetails
import com.example.caffeeborcelle.databinding.ActivityBestsellerItemsBlueprintBinding


class BestsellersAdapter (private val context: Context) :
    ListAdapter<ProductEntity, BestsellersAdapter.BestsellerViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductEntity>() {

            override fun areItemsTheSame(
                oldItem: ProductEntity,
                newItem: ProductEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProductEntity,
                newItem: ProductEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        val binding = ActivityBestsellerItemsBlueprintBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BestsellerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemDetails::class.java)
            intent.putExtra("productId", product.id)
            context.startActivity(intent)
        }
    }

    class BestsellerViewHolder(
        private val binding: ActivityBestsellerItemsBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {

            // 🔥 Coffee product image
            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.HomeItemImage)

            // 🔥 Coffee details
            binding.ProductName.text = product.name
            binding.ProductRating.rating = product.rating.toFloat()
            binding.ProductReviews.text = "${product.reviews} Reviews"
            binding.DiscountPercent.text = "${product.discount}%"
            binding.ProductPrice.text = "₹${product.price}"
            binding.OriginalPrice.text = "₹${product.originalPrice}"

            // Strike through original price
            binding.OriginalPrice.paintFlags =
                binding.OriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}