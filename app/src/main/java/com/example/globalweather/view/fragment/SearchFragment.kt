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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.globalweather.adapter.CityAdapter
import com.example.globalweather.databinding.FragmentSearchBinding
import com.example.globalweather.model.constant.City
import com.example.globalweather.utils.Constants.JSON_FILE
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import kotlin.text.Charsets.UTF_8

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val cityAdapter by lazy { CityAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        init()
    }


    private fun init() {
        showLoading()
        initRecyclerView()
        getAllCity()
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (charSequence!!.isNotEmpty()) {
                    viewModel.searchQuery.value = charSequence.toString()
                    viewModel.searchCities().observe(viewLifecycleOwner) {
                        cityAdapter.differ.submitList(it)
                    }
                } else {
                    lifecycleScope.launchWhenCreated {
                        viewModel.getAllCity().observe(viewLifecycleOwner) {
                            hideLoading()
                            cityAdapter.differ.submitList(it)
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }


    private fun getAllCity() {
        viewModel.getAllCities()
        lifecycleScope.launchWhenCreated {
            viewModel.getData()
            viewModel.getAllCity().observe(viewLifecycleOwner) {
                hideLoading()
                cityAdapter.differ.submitList(it)
                Log.w("TAG", "getAllCity: ${it!!.size}")
            }
        }


    }

    private fun initRecyclerView() {
        binding.rclCities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cityAdapter
            hasFixedSize()
        }
    }

    private fun showLoading() {
        binding.prgSearch.visibility = VISIBLE
    }

    private fun hideLoading() {
        binding.prgSearch.visibility = INVISIBLE
    }


}

