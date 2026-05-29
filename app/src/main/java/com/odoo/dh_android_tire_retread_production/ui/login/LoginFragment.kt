package com.odoo.dh_android_tire_retread_production.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.odoo.dh_android_tire_retread_production.R
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check existing session
        viewLifecycleOwner.lifecycleScope.launch {
            val token = sessionManager.accessToken.firstOrNull()
            val session = sessionManager.stationSession.firstOrNull()
            if (token != null) {
                if (session != null) {
                    findNavController().navigate(R.id.action_loginFragment_to_queueFragment)
                } else {
                    findNavController().navigate(R.id.action_loginFragment_to_stationSelectFragment)
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.login(username, password)
        }

        binding.exitButton.setOnClickListener {
            requireActivity().finish()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is LoginUiState.Loading -> {
                        binding.loginButton.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is LoginUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_loginFragment_to_stationSelectFragment)
                    }
                    is LoginUiState.Error -> {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.visibility = View.GONE
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
