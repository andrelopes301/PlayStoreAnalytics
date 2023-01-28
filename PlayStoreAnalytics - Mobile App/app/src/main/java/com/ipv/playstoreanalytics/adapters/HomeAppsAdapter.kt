package com.ipv.playstoreanalytics.adapters

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.adapters.HomeAppsAdapter.HomeAppsViewHolder
import com.ipv.playstoreanalytics.databinding.CardAppItemBinding

class HomeAppsAdapter(
        private var data: MutableList<PlayStoreAppModel>,
        private val appClicked: ((PlayStoreAppModel) -> Unit)
    ) : RecyclerView.Adapter<HomeAppsViewHolder>() {
        inner class HomeAppsViewHolder(private val binding: CardAppItemBinding) : RecyclerView.ViewHolder(binding.root)  {

        fun bindPlaystoreApp(app: PlayStoreAppModel) = with(itemView) {
            binding.appName.text = app.appName
            Glide.with(itemView)
                .load(app.imageUrl)
                .into(binding.image)


            binding.card.setOnClickListener {
                appClicked(app)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAppsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = CardAppItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return HomeAppsViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: HomeAppsViewHolder, position: Int) {
        val app = data[position]
        holder.bindPlaystoreApp(app)
    }



    override fun getItemCount(): Int {
        return data.size
    }

}



