package com.ipv.playstoreanalytics.adapters

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import com.ipv.playstoreanalytics.databinding.PlaystoreappItemBinding
import androidx.databinding.ViewDataBinding

class PlayStoreAppsAdapter(
    private var data: MutableList<PlayStoreAppModel>,
    private val appClicked: ((PlayStoreAppModel) -> Unit)
) : RecyclerView.Adapter<PlayStoreAppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayStoreAppViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = PlaystoreappItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return PlayStoreAppViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: PlayStoreAppViewHolder, position: Int) {
        val binding = holder.binding as PlaystoreappItemBinding
        val app = data[position]
        binding.app = app
        binding.executePendingBindings()
        binding.card.setOnClickListener{ appClicked(app) }
    }


    override fun getItemCount(): Int {
        return data.size
    }
}


class PlayStoreAppViewHolder(
    val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root)
