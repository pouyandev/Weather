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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.globalweather.adapter.FavoriteAdapter
import com.example.globalweather.databinding.FragmentFavoriteBinding
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         handleDeleteAndInsert(view)
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
            hasPendingAdapterUpdates()
        }
    }

    private fun showLoading() {
        binding.prgFavorite.visibility = VISIBLE
    }

    private fun hideLoading() {
        binding.prgFavorite.visibility = INVISIBLE
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
                viewModel.deleteFavoriteCity(favorite)
                Snackbar.make(view,"Successfully Deleted Favorite City", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.addFavoriteCity(favorite)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rclFavorite)
        }
    }


}