package com.example.caffeeborcelle.MyAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.caffeeborcelle.R
import androidx.recyclerview.widget.RecyclerView

class QuickPickAdapter (private val list: List<String>) :
    RecyclerView.Adapter<QuickPickAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text = view.findViewById<TextView>(R.id.txtName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_pick, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = list[position]
    }

    override fun getItemCount(): Int = list.size
}