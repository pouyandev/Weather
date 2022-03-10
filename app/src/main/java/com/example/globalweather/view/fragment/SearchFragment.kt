package com.example.globalweather.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.globalweather.R
import com.example.globalweather.adapter.CityAdapter
import com.example.globalweather.databinding.FragmentSearchBinding
import com.example.globalweather.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    private val cityAdapter by lazy { CityAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

 /*       // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()*/
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        init()
    }


    @ExperimentalCoroutinesApi
    private fun init() {
        showLoading()
        cityAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("pCityName", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_weatherFragment, bundle)



        }
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
        lifecycleScope.launchWhenCreated {
            hideLoading()
            viewModel.getAllCity().observe(viewLifecycleOwner) {
                cityAdapter.differ.submitList(it)
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
