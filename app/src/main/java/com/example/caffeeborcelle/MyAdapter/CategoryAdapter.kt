package com.example.caffeeborcelle.MyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.caffeeborcelle.databinding.ActivityItemCategoryBinding
import com.example.caffeeborcelle.model.CategoryModel

class CategoryAdapter (
    private val list: List<CategoryModel>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(
        val binding: ActivityItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val binding = ActivityItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val category = list[position]

        holder.binding.txtCategory.text = category.name
        holder.binding.imgcategory.setImageResource(category.image)

        holder.itemView.setOnClickListener {
            onClick(category.name)
        }
    }


    override fun getItemCount(): Int = list.size
}
