package com.example.globalweather.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.globalweather.utils.WeatherState
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
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
         handleDeleteAndInsert(view)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        initRecyclerView()
        searchFavoriteCity()
        getAllFavoriteCity()


    }

    private fun searchFavoriteCity() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (charSequence!!.isNotEmpty()) {
                    viewModel.searchFavoriteQuery.value = charSequence.toString()
                    lifecycleScope.launchWhenCreated {
                        viewModel.searchFavoriteCities().collectLatest {
                            hideLoading()
                            favoriteAdapter.differ.submitList(it)
                        }
                    }
                } else {
                    getAllFavoriteCity()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun getAllFavoriteCity() {
        lifecycleScope.launchWhenCreated {
            viewModel.favoriteCities()
                viewModel.getAllFavoriteCity().collectLatest {
                    when (it) {
                        is WeatherState.Loading -> showLoading()
                        is WeatherState.Error -> {
                            hideLoading()
                            Log.e("TAG", "favoriteCity: ${it.error}")
                        }
                        is WeatherState.SearchFavoriteCity -> {
                            hideLoading()
                            favoriteAdapter.differ.submitList(it.response)
                        }
                        else -> {}
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}