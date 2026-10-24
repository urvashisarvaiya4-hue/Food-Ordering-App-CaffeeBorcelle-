package com.example.caffeeborcelle.MyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.ActivityCartItemsBlueprintBinding
import com.example.caffeeborcelle.model.CartEntity


class CartAdapter (
    private var list: MutableList<CartEntity>,
    private val listener: OnCartItemActionListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnCartItemActionListener {
        fun onCartItemDeleted(cartItem: CartEntity, position: Int)
        fun onQuantityChanged(cartItem: CartEntity, position: Int, newQuantity: Long)
    }

    inner class CartViewHolder(val binding: ActivityCartItemsBlueprintBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ActivityCartItemsBlueprintBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        val item = list[position]
        val qty = item.quantity ?: 1L

        // ⭐ TEXT
        holder.binding.ProductName.text = item.productName ?: ""
        holder.binding.ProductPrice.text = "₹${item.price ?: 0L}"
        holder.binding.cartItemQuantity.text = qty.toString()

        // ⭐ IMAGE
        Glide.with(holder.itemView.context)
            .load(item.productImage)
            .into(holder.binding.HomeItemImage)

        // ⭐ DELETE
        holder.binding.CartItemDelete.setOnClickListener {
            listener.onCartItemDeleted(item, position)
        }

        // ⭐ PLUS
        holder.binding.plusebutton.setOnClickListener {
            listener.onQuantityChanged(item, position, qty + 1)
        }

        // ⭐ MINUS
        holder.binding.minusbutton.setOnClickListener {
            if (qty > 1) {
                listener.onQuantityChanged(item, position, qty - 1)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun setData(newList: MutableList<CartEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}