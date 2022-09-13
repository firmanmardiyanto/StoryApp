package org.firmanmardiyanto.storyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.firmanmardiyanto.core.customview.ValidationEditText
import org.firmanmardiyanto.core.customview.ValidationType
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.storyapp.R
import org.firmanmardiyanto.storyapp.auth.AuthViewModel
import org.firmanmardiyanto.storyapp.databinding.FragmentLoginBinding
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isValidEmail = false
    private var isValidPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this) {
            activity?.moveTaskToBack(true)
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        authViewModel.session().observe(viewLifecycleOwner) { user ->
            if (user !== null) {
                findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                activity?.finish()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLogin.setOnClickListener {
                login()
            }

            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            btnLogin.isEnabled = false

            etPassword.setOnValidatedListener(
                ValidationType.PASSWORD,
                true,
                object :
                    ValidationEditText.OnValidatedListener {
                    override fun onValidate(isValid: Boolean) {
                        isValidPassword = isValid
                        btnLogin.isEnabled = isValidEmail && isValidPassword
                    }
                })

            etEmail.setOnValidatedListener(
                ValidationType.EMAIL,
                true,
                object :
                    ValidationEditText.OnValidatedListener {
                    override fun onValidate(isValid: Boolean) {
                        isValidEmail = isValid
                        btnLogin.isEnabled = isValidEmail && isValidPassword
                    }
                })
        }
    }

    private fun login() {
        binding.apply {
            authViewModel.login(etEmail.text.toString(), etPassword.text.toString())
                .observe(
                    viewLifecycleOwner
                ) {
                    when (it) {
                        is Resource.Loading -> {
                            btnLogin.isEnabled = false
                            btnLogin.text = getString(R.string.loading_with_dot)
                        }
                        is Resource.Success -> {
                            it.data?.let { data ->
                                lifecycleScope.launch {
                                    authViewModel.saveSession(data)
                                }
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.welcome_with_name, data.name),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is Resource.Error -> {
                            btnLogin.isEnabled = true
                            btnLogin.text = getString(R.string.login)
                            it.message?.let { message ->
                                Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}