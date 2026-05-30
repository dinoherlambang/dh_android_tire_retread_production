package com.odoo.dh_android_tire_retread_production.ui.queue

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
import com.odoo.dh_android_tire_retread_production.databinding.FragmentQueueBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QueueFragment : Fragment() {

    private var _binding: FragmentQueueBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QueueViewModel by viewModels()
    private lateinit var adapter: QueueAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.queue_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_change_station -> {
                    viewModel.changeStation()
                    true
                }
                R.id.action_logout -> {
                    viewModel.logout()
                    true
                }
                R.id.action_exit -> {
                    requireActivity().finishAndRemoveTask()
                    true
                }
                else -> false
            }
        }

        adapter = QueueAdapter { item ->
            val bundle = Bundle().apply {
                putInt("workorderId", item.workorder_id)
            }
            findNavController().navigate(R.id.action_queueFragment_to_detailFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Trigger immediate fetch when entering or returning to the screen
        viewModel.refresh()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is QueueUiState.Loading -> {
                        // binding.progressBar.visibility = View.VISIBLE
                    }
                    is QueueUiState.Success -> {
                        // binding.progressBar.visibility = View.GONE
                        binding.toolbar.subtitle = "${state.data.station.name} - ${state.data.total} items"
                        adapter.submitList(state.data.items)
                        binding.emptyView.visibility = if (state.data.total == 0) View.VISIBLE else View.GONE
                    }
                    is QueueUiState.Error -> {
                        // binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is QueueUiState.LoggedOut -> {
                        findNavController().navigate(R.id.action_queueFragment_to_loginFragment)
                    }
                    is QueueUiState.StationClosed -> {
                        findNavController().navigate(R.id.action_queueFragment_to_stationSelectFragment)
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
