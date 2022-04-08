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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.globalweather.R
import com.example.globalweather.adapter.CityAdapter
import com.example.globalweather.databinding.FragmentSearchBinding
import com.example.globalweather.di.application.HiltApplication
import com.example.globalweather.utils.WeatherState
import com.example.globalweather.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private val viewModel by lazy { viewModels<WeatherViewModel>() }
    private val cityAdapter by lazy { CityAdapter() }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onStart() {
        super.onStart()
        init()


    }


    @ExperimentalCoroutinesApi
    private fun init() {
        cityItemClick()
        searchCity()
    }

    private fun cityItemClick() {
        cityAdapter.setOnItemClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated{
                HiltApplication.cityDetails.storeDetails(it.name!!)
                findNavController().navigate(R.id.action_searchFragment_to_weatherFragment)

            }
        }
    }

    private fun searchCity() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.value.searchCity(charSequence.toString().trim())
                        viewModel.value.searchList.collectLatest {
                            when (it) {
                                is WeatherState.LOADING -> showLoading()
                                is WeatherState.ERROR -> {
                                    hideLoading()
                                    Log.e("TAG", "SearchList: ${it.error}")
                                }
                                is WeatherState.SUCCESS -> {
                                    hideLoading()
                                    initRecyclerView()
                                    if(binding.edtSearch.text!!.toString().isEmpty()){
                                        showLoading()
                                    }
                                      cityAdapter.differ.submitList(it.data)
                                }
                            }
                        }
                    }
                }
            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private fun initRecyclerView() {
        binding.rclCities.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = cityAdapter
            hasFixedSize()
        }
    }

    private fun showLoading() {
        binding.prgSearch.visibility = VISIBLE
        binding.rclCities.visibility = INVISIBLE
    }

    private fun hideLoading() {
        binding.prgSearch.visibility = INVISIBLE
        binding.rclCities.visibility = VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
