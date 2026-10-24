package com.example.caffeeborcelle.AdminAdapter


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.caffeeborcelle.activity_edit_product
import com.example.caffeeborcelle.activity_manage_product_details
import com.example.caffeeborcelle.databinding.ActivityManagementProductAllBlueprintBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.FirebaseDatabase

class AdapterManagementALL (
    private var products: List<ProductEntity>,
    private val context: Context
) : RecyclerView.Adapter<AdapterManagementALL.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ActivityManagementProductAllBlueprintBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], context)
    }

    override fun getItemCount(): Int = products.size

    fun updateProductList(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ProductViewHolder(
        private val binding: ActivityManagementProductAllBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity, context: Context) {

            // 🔥 Image animation (same)
            binding.HomeItemImage.apply {
                scaleX = 0.10f
                scaleY = 0.10f
                alpha = 0.0f

                animate().cancel()
                animate()
                    .scaleX(0.40f)
                    .scaleY(0.40f)
                    .alpha(0.8f)
                    .setDuration(120)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .alpha(1.0f)
                            .setDuration(180)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    .start()
            }

            Glide.with(binding.HomeItemImage.context)
                .load(product.imageUrl)
                .thumbnail(0.10f)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.HomeItemImage)

            // Coffee details
            binding.ProductName.text = product.name
            binding.ProductRating.rating = product.rating.toFloat()
            binding.ProductReviews.text = "${product.reviews} Reviews"
            binding.DiscountPercent.text = "${product.discount}%"
            binding.ProductPrice.text = "₹${product.price}"
            binding.OriginalPrice.text = "₹${product.originalPrice}"
            binding.OriginalPrice.paintFlags =
                binding.OriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // View coffee details
           // binding.root.setOnClickListener {
              //  val intent = Intent(context, activity_manage_product_details::class.java)
              //  intent.putExtra("coffee_Id", product.id)
              //  context.startActivity(intent)
           // }

            binding.root.setOnClickListener(null)

            // Edit coffee
            binding.EditButton.setOnClickListener {
                val intent = Intent(context, activity_edit_product::class.java)
                intent.putExtra("productId", product.id)
                context.startActivity(intent)
                //Toast.makeText(context, "ID: ${product.id}", Toast.LENGTH_SHORT).show()
            }

            // Delete coffee
            binding.DeleteButton.setOnClickListener {
                FirebaseDatabase.getInstance()
                    .getReference("products")
                    .child(product.id)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "${product.name} deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Delete failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}