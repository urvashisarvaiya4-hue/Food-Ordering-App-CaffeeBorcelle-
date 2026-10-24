package com.example.caffeeborcelle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityWishlistItemsBlueprintBinding
import com.example.caffeeborcelle.model.CartEntity

class AdminUserDetailCartAdapter (
    private val items: MutableList<CartEntity>
) : RecyclerView.Adapter<AdminUserDetailCartAdapter.VH>() {

    inner class VH(
        private val binding: ActivityWishlistItemsBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartEntity) {

            // TEXT
            binding.WishlistProductName.text = item.productName
            binding.WishlistProductPrice.text = "₹${item.price}"
            binding.WishlistOriginalPrice.text = "Qty: ${item.quantity}"

            // Unused views hide
            binding.WishlistItemDiscount.text = ""
            binding.WishlistProductReviews.text = ""
            binding.WishlistProductRating.rating = 0f

            // IMAGE LOAD
            Glide.with(binding.WishlistItemImage.context)
                .load(item.productImage)
                .placeholder(R.drawable.broken_image)
                .error(R.drawable.broken_image)
                .into(binding.WishlistItemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ActivityWishlistItemsBlueprintBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}