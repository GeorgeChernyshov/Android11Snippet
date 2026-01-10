package com.example.pre30.ui.packagevisibility

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PackagesRecyclerAdapter : RecyclerView.Adapter<PackagesRecyclerAdapter.ViewHolder>() {

    var items: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = PackageItem(parent.context, null)

        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.text = items[position]
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val item: PackageItem) : RecyclerView.ViewHolder(item) {
        var text: String? = null
            set(value) {
                item.setData(value)
                field = value
            }
    }
}