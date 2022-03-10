package com.example.globalweather.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.globalweather.adapter.FavoriteAdapter
import com.example.globalweather.databinding.FragmentFavoriteBinding
import com.example.globalweather.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    private val favoriteAdapter by lazy { FavoriteAdapter() }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        init()

    }

    private fun init() {
        showLoading()
        initRecyclerView()
        lifecycleScope.launchWhenCreated {
            viewModel.getAllFavoriteCity().observe(viewLifecycleOwner) {
                hideLoading()
                favoriteAdapter.differ.submitList(it)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rclFavorite.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = favoriteAdapter
            hasFixedSize()
        }
    }

    private fun showLoading() {
        binding.prgFavorite.visibility = VISIBLE
    }

    private fun hideLoading() {
        binding.prgFavorite.visibility = INVISIBLE
    }


}