package com.ipv.playstoreanalytics.adapters

import android.graphics.drawable.Drawable
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.databinding.ListItemBinding
import com.ipv.playstoreanalytics.models.CategoryModel
import com.ipv.playstoreanalytics.utils.getLayoutInflater


class CategoriesAdapter(
    private var data: MutableList<CategoryModel>,
    private val categoryClicked: ((CategoryModel) -> Unit)
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bindCategory(category: CategoryModel) {
            binding.categoryText.text = category.name

           val drawable = when(category.name){
                "Business" ->  R.drawable.business
                "Communication" ->  R.drawable.communication
                "Education" ->  R.drawable.education
                "Entertainment" ->  R.drawable.entertainment
                "Food & Drink" ->  R.drawable.food
                "Games" ->  R.drawable.games
                "Lifestyle" ->  R.drawable.lifestyle
                "Medical" ->  R.drawable.medical
                "Photography" ->  R.drawable.photography
                "Social" ->  R.drawable.social
                "Sports" ->  R.drawable.sports
                "Tools" ->  R.drawable.tools
                else ->  R.drawable.ic_apps
            }

            binding.cardImage.setImageResource(drawable)

            binding.listItem.setOnClickListener {
                categoryClicked(category)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ListItemBinding.inflate(
                parent.getLayoutInflater(),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category: CategoryModel = data[position]
        holder.bindCategory(category)
    }


    override fun getItemCount(): Int {
        return data.size
    }
}

