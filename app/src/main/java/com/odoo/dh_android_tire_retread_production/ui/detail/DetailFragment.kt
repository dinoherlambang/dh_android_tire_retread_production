package com.odoo.dh_android_tire_retread_production.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.odoo.dh_android_tire_retread_production.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.passButton.setOnClickListener { viewModel.markDone("pass") }
        binding.failButton.setOnClickListener { showFailDialog() }
        binding.cancelButton.setOnClickListener { showCancelConfirmation() }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is DetailUiState.Loading -> {
                        // Show loading
                    }
                    is DetailUiState.Success -> {
                        val wo = state.data.workorder
                        binding.woNumber.text = wo.wo_number
                        binding.serialNumber.text = wo.serial_number
                        binding.customerName.text = wo.customer_name
                        binding.serviceTypeChip.text = wo.service_type
                        
                        binding.passButton.visibility = if (wo.can_done) View.VISIBLE else View.GONE
                        binding.failButton.visibility = if (wo.can_done) View.VISIBLE else View.GONE
                        binding.cancelButton.visibility = if (wo.can_done) View.VISIBLE else View.GONE
                    }
                    is DetailUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is DetailUiState.ActionSuccess -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun showFailDialog() {
        // Simple dialog for now, can be replaced with BottomSheet as per requirements
        val input = android.widget.EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Mark as Failed")
            .setMessage("Enter reason (optional):")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                viewModel.markDone("fail", input.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCancelConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Process")
            .setMessage("Are you sure you want to cancel processing for this tire?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.cancel()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
