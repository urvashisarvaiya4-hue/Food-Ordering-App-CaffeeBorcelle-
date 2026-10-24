package com.example.caffeeborcelle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityWishlistItemsBlueprintBinding
import com.example.caffeeborcelle.model.WishlistEntity

class AdminUserDetailWishlistAdapter (
    private val items: List<WishlistEntity>
) : RecyclerView.Adapter<AdminUserDetailWishlistAdapter.VH>() {

    inner class VH(
        private val binding: ActivityWishlistItemsBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WishlistEntity) {

            // ☕ TEXT DATA (Coffee Product)
            binding.WishlistProductName.text = item.name
            binding.WishlistProductPrice.text = "₹${item.price}"
            binding.WishlistOriginalPrice.text = "₹${item.originalPrice}"
            binding.WishlistItemDiscount.text = "${item.discount}% OFF"
            binding.WishlistProductRating.rating = item.rating.toFloat()
            binding.WishlistProductReviews.text = "(${item.reviewCount} Reviews)"

            // 🔥 IMAGE (Cloudinary / Firebase URL)
            Glide.with(binding.WishlistItemImage.context)
                .load(item.imageUrl)
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
}