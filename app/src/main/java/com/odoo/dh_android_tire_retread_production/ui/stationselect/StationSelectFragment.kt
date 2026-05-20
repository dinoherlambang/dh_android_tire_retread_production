package com.odoo.dh_android_tire_retread_production.ui.stationselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.odoo.dh_android_tire_retread_production.R
import com.odoo.dh_android_tire_retread_production.databinding.FragmentStationSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StationSelectFragment : Fragment() {

    private var _binding: FragmentStationSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StationSelectViewModel by viewModels()
    private lateinit var adapter: StationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StationAdapter { station ->
            // In a real app, you'd get the device name from settings or build info
            viewModel.selectStation(station, "Tablet-01")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is StationSelectUiState.Loading -> {
                        // Show loading
                    }
                    is StationSelectUiState.Success -> {
                        adapter.submitList(state.stations)
                    }
                    is StationSelectUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is StationSelectUiState.SessionOpened -> {
                        findNavController().navigate(R.id.action_stationSelectFragment_to_queueFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
