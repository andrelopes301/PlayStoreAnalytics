package com.ipv.playstoreanalytics.adapters

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.adapters.CategoriesListAppsAdapter.CategoriesListAppsViewHolder
import com.ipv.playstoreanalytics.databinding.PlaystoreappItemBinding
import com.ipv.playstoreanalytics.fragments.PlayStoreAppItem
import com.ipv.playstoreanalytics.models.CategoryModel

class CategoriesListAppsAdapter(
        private var data: MutableList<PlayStoreAppModel>,
        private val appClicked: ((PlayStoreAppModel) -> Unit)
    ) : RecyclerView.Adapter<CategoriesListAppsViewHolder>() {

        inner class CategoriesListAppsViewHolder(private val binding: PlaystoreappItemBinding) : RecyclerView.ViewHolder(binding.root)  {

        fun bindPlaystoreApp(app: PlayStoreAppModel) = with(itemView) {

            binding.appName.text = app.appName
            binding.developerId.text = app.developerId
            binding.numberInstalls.text = app.estimatedInstalls
            binding.rating.text = app.rating.toString()

            Glide.with(this)
                .load(app.imageUrl)
                .into(binding.appImage)

            binding.card.setOnClickListener {
                appClicked(app)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesListAppsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = PlaystoreappItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return CategoriesListAppsViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: CategoriesListAppsViewHolder, position: Int) {
        val app = data[position]
        holder.bindPlaystoreApp(app)
    }



    override fun getItemCount(): Int {
        return data.size
    }

}



