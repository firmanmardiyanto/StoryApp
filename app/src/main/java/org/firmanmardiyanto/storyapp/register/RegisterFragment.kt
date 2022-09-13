package org.firmanmardiyanto.storyapp.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.firmanmardiyanto.core.customview.ValidationEditText
import org.firmanmardiyanto.core.customview.ValidationType
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.storyapp.R
import org.firmanmardiyanto.storyapp.auth.AuthViewModel
import org.firmanmardiyanto.storyapp.databinding.FragmentRegisterBinding
import org.koin.android.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var isValidEmail = false
    private var isValidPassword = false
    private var isValidName = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnRegister.setOnClickListener {
                register()
            }

            tvLogin.setOnClickListener {
                activity?.onBackPressed()
            }

            btnRegister.isEnabled = false

            etPassword.setOnValidatedListener(
                ValidationType.PASSWORD,
                true,
                object :
                    ValidationEditText.OnValidatedListener {
                    override fun onValidate(isValid: Boolean) {
                        isValidPassword = isValid
                        btnRegister.isEnabled = isValidEmail && isValidPassword && isValidName
                    }
                })

            etEmail.setOnValidatedListener(
                ValidationType.EMAIL,
                true,
                object :
                    ValidationEditText.OnValidatedListener {
                    override fun onValidate(isValid: Boolean) {
                        isValidEmail = isValid
                        btnRegister.isEnabled = isValidEmail && isValidPassword && isValidName
                    }
                })

            etName.setOnValidatedListener(
                ValidationType.TEXT,
                true,
                object :
                    ValidationEditText.OnValidatedListener {
                    override fun onValidate(isValid: Boolean) {
                        isValidName = isValid
                        btnRegister.isEnabled = isValidEmail && isValidPassword && isValidName
                    }
                })
        }
    }

    private fun register() {
        binding.apply {
            authViewModel.register(
                etName.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString()
            )
                .observe(
                    viewLifecycleOwner,
                ) {
                    when (it) {
                        is Resource.Loading -> {
                            btnRegister.isEnabled = false
                            btnRegister.text = getString(R.string.loading_with_dot)
                        }
                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.register_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            activity?.onBackPressed()
                        }
                        is Resource.Error -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = getString(R.string.register)
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
}