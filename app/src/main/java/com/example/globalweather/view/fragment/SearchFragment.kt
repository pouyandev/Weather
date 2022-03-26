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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by viewModels()
    private val cityAdapter by lazy { CityAdapter() }
    var job: Job? = null


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
        showLoading()
        cityItemClick()
        getAllCity()
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
                if (charSequence!!.isNotEmpty()) {
                    viewModel.searchQuery.value = charSequence.toString().trim()
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.searchCities().collectLatest {
                            cityAdapter.differ.submitList(it)
                        }
                    }
                } else {
                    getAllCity()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }


    private fun getAllCity() {
            viewModel.handleAllCity()
            viewModel.allCities.observe(viewLifecycleOwner) {
                when (it) {
                    is WeatherState.Loading -> showLoading()
                    is WeatherState.Error -> {
                        hideLoading()
                        Log.e("TAG", "getAllCity: ${it.error}")
                    }
                    is WeatherState.SearchCity -> {
                        hideLoading()
                        initRecyclerView()
                        if (it.response.isNullOrEmpty()) {
                            viewModel.convertJsonAndUpsert()
                        }
                        cityAdapter.differ.submitList(it.response)
                    }
                    else -> {}
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
