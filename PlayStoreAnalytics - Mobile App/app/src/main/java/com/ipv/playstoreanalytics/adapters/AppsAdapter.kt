package com.ipv.playstoreanalytics.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ipv.playstoreanalytics.databinding.PlaystoreappItemBinding
import com.ipv.playstoreanalytics.models.PlayStoreAppModel
import androidx.databinding.ViewDataBinding


class AppsAdapter(
    private val appClicked: ((PlayStoreAppModel) -> Unit))
    : PagingDataAdapter<PlayStoreAppModel, PlayStoreAppsViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<PlayStoreAppModel>() {
        override fun areItemsTheSame(oldItem: PlayStoreAppModel, newItem: PlayStoreAppModel): Boolean {
            return oldItem.appName == newItem.appName
        }

        override fun areContentsTheSame(oldItem: PlayStoreAppModel, newItem: PlayStoreAppModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: PlayStoreAppsViewHolder, position: Int) {
        val binding = holder.binding as PlaystoreappItemBinding
        binding.app = getItem(position) ?: return
        binding.executePendingBindings()

        binding.card.setOnClickListener{
            getItem(position)?.let { app -> appClicked(app) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayStoreAppsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PlaystoreappItemBinding.inflate(layoutInflater, parent, false)
        return PlayStoreAppsViewHolder(binding)
    }

}

class PlayStoreAppsViewHolder(
    val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root)
