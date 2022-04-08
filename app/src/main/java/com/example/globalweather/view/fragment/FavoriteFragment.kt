package com.example.globalweather.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.globalweather.R
import com.example.globalweather.adapter.FavoriteAdapter
import com.example.globalweather.databinding.FragmentFavoriteBinding
import com.example.globalweather.di.application.HiltApplication
import com.example.globalweather.utils.WeatherState
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!
    private val viewModel by lazy { viewModels<WeatherViewModel>() }
    private val favoriteAdapter by lazy { FavoriteAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }


    private fun init(view: View) {
        getAllFavoriteCity()
        handleDeleteAndInsert(view)
        favoriteAdapter.setOnItemClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated{
                HiltApplication.cityDetails.storeDetails(it.cityName!!.toString())
                findNavController().navigate(R.id.action_favoriteFragment_to_weatherFragment)
            }

        }
        binding.imgSearchCity.setOnClickListener {
            findNavController().navigate(R.id.action_favoriteFragment_to_searchFragment)
        }
        binding.imgBackFavorite.setOnClickListener {
            findNavController().navigate(R.id.action_favoriteFragment_to_weatherFragment)
        }
    }


    private fun getAllFavoriteCity() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.value.handleAllFavoriteCity()
            viewModel.value.favoriteCities.collectLatest {
                when (it) {
                    is WeatherState.LOADING -> showLoading()
                    is WeatherState.ERROR -> {
                        hideLoading()
                        Log.e("TAG", "favoriteCity: ${it.error}")
                    }
                    is WeatherState.SUCCESS -> {
                        hideLoading()
                        initRecyclerView()
                        if(it.data!!.isEmpty()){
                            showLoading()
                        }
                        favoriteAdapter.differ.submitList(it.data)
                    }
                }
            }
        }


    }

    private fun initRecyclerView() {
        binding.rclFavorite.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteAdapter
            hasFixedSize()
        }
    }

    private fun handleDeleteAndInsert(view: View) {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favorite = favoriteAdapter.differ.currentList[position]
                viewModel.value.deleteFavoriteCity(favorite)
                Snackbar.make(view,"Successfully Deleted Favorite City", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.value.addFavoriteCity(favorite)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rclFavorite)
        }
    }

    private fun showLoading() {
        binding.prgFavorite.visibility = VISIBLE
        binding.rclFavorite.visibility = INVISIBLE
    }

    private fun hideLoading() {
        binding.prgFavorite.visibility = INVISIBLE
        binding.rclFavorite.visibility = VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}