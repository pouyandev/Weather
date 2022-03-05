package com.example.globalweather.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.globalweather.R
import com.example.globalweather.databinding.ItemCityBinding
import com.example.globalweather.model.constant.City

class CityAdapter : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {
    private val differCallback = object : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((City) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {

        return CityViewHolder(
            ItemCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }


    fun setOnItemClickListener(listener: (City) -> Unit) {
        onItemClickListener = listener
    }


    inner class CityViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(city: City) {
            binding.apply {
                txtCityName.text = city.name

            }
            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(city)
                   /*     val bundle = Bundle().apply {
                            putParcelable("pCityName", city)
                        }
                        Navigation.createNavigateOnClickListener(R.id.action_searchFragment_to_weatherFragment, bundle)
*/
                    }
                }
            }

        }
    }
