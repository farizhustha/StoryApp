package com.farizhustha.storyapp.ui.auth.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.data.Result
import com.farizhustha.storyapp.databinding.FragmentSignUpBinding
import com.farizhustha.storyapp.model.User
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.utils.UtilsContext.dpToPx
import com.farizhustha.storyapp.utils.UtilsContext.getScreenWidth

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels {
        ViewModelFactory(context = requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        playAnimation()
    }

    private fun setupView() {
        binding.apply {
            val editTextEmail = edtLayoutSignupEmail.editText
            val editTextPassword = edtLayoutSignupPassword.editText
            val editTextConfirm = edtLayoutSignupConfirmPassword.editText

            editTextEmail?.id = R.id.edt_sign_up_email
            editTextPassword?.id = R.id.edt_sign_up_password
            editTextConfirm?.id = R.id.edt_sign_up_confirm_password

            edtSignupName.doAfterTextChanged {
                updateButtonStatus()
            }
            editTextEmail?.doAfterTextChanged {
                updateButtonStatus()
            }
            editTextPassword?.doAfterTextChanged {
                edtLayoutSignupConfirmPassword.setPassword(it.toString())
                if (it.toString() == editTextConfirm?.text.toString()) {
                    edtLayoutSignupConfirmPassword.error = null
                }
                updateButtonStatus()
            }
            editTextConfirm?.doAfterTextChanged {
                updateButtonStatus()
            }

            btnSignupRegister.setOnClickListener {
                val name: String = edtSignupName.text.toString()
                val email: String = editTextEmail?.text.toString()
                val password: String = editTextPassword?.text.toString()

                val user = User(name, email, password)
                viewModel.register(user).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(activity, result.data, Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(activity, result.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            btnSignupLogin.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun updateButtonStatus() {

        binding.apply {
            val isNameError = edtSignupName.text.isNullOrEmpty()
            val isEmailError =
                edtLayoutSignupEmail.editText?.text.isNullOrEmpty() || edtLayoutSignupEmail.error != null
            val isPasswordError =
                edtLayoutSignupPassword.editText?.text.isNullOrEmpty() || edtLayoutSignupPassword.error != null
            val isConfirmError =
                edtLayoutSignupConfirmPassword.editText?.text.isNullOrEmpty() || edtLayoutSignupConfirmPassword.error != null

            btnSignupRegister.isEnabled =
                !(isNameError || isEmailError || isPasswordError || isConfirmError)
        }
    }

    private fun playAnimation() {
        val width = requireActivity().dpToPx(requireActivity().getScreenWidth())

        val title = ObjectAnimator.ofFloat(binding.tvSignupTitle, View.ALPHA, 1f).setDuration(2000)
        val subTitle =
            ObjectAnimator.ofFloat(binding.tvSignupTitle2, View.ALPHA, 1f).setDuration(2000)
        val togetherTitle = AnimatorSet().apply {
            playTogether(title, subTitle)
        }

        val nameFade = ObjectAnimator.ofFloat(binding.edtLayoutSignupName, View.ALPHA, 1f)
        val nameX =
            ObjectAnimator.ofFloat(binding.edtLayoutSignupName, View.TRANSLATION_X, -width, 0f)
                .apply {
                    duration = 500
                }
        val togetherName = AnimatorSet().apply {
            playTogether(nameFade, nameX)
        }

        val emailFade = ObjectAnimator.ofFloat(binding.edtLayoutSignupEmail, View.ALPHA, 1f)
        val emailX =
            ObjectAnimator.ofFloat(binding.edtLayoutSignupEmail, View.TRANSLATION_X, -width, 0f)
                .apply {
                    duration = 500
                }
        val togetherEmail = AnimatorSet().apply {
            playTogether(emailX, emailFade)
        }

        val passwordFade = ObjectAnimator.ofFloat(binding.edtLayoutSignupPassword, View.ALPHA, 1f)
        val passwordX =
            ObjectAnimator.ofFloat(binding.edtLayoutSignupPassword, View.TRANSLATION_X, -width, 0f)
                .apply {
                    duration = 500
                }
        val togetherPassword = AnimatorSet().apply {
            playTogether(passwordX, passwordFade)
        }

        val confirmFade =
            ObjectAnimator.ofFloat(binding.edtLayoutSignupConfirmPassword, View.ALPHA, 1f)
        val confirmX = ObjectAnimator.ofFloat(
            binding.edtLayoutSignupConfirmPassword, View.TRANSLATION_X, -width, 0f
        ).apply {
            duration = 500
        }
        val togetherConfirm = AnimatorSet().apply {
            playTogether(confirmX, confirmFade)
        }

        val buttonRegister = ObjectAnimator.ofFloat(binding.btnSignupRegister, View.ALPHA, 1f)

        val textLoginFade =
            ObjectAnimator.ofFloat(binding.tvSignupLogin, View.ALPHA, 1f).setDuration(0)
        val textLoginY = ObjectAnimator.ofFloat(
            binding.tvSignupLogin, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f
        ).apply {
            duration = 500
        }
        val buttonLoginFade =
            ObjectAnimator.ofFloat(binding.btnSignupLogin, View.ALPHA, 1f).setDuration(0)
        val buttonLoginY = ObjectAnimator.ofFloat(
            binding.btnSignupLogin, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f
        ).apply {
            duration = 500
        }
        val togetherLogin = AnimatorSet().apply {
            playTogether(textLoginFade, textLoginY, buttonLoginFade, buttonLoginY)
        }

        val groupInput = AnimatorSet().apply {
            playSequentially(
                togetherName,
                togetherEmail,
                togetherPassword,
                togetherConfirm,
                buttonRegister,
                togetherLogin
            )
        }

        AnimatorSet().apply {
            playTogether(togetherTitle, groupInput)
            start()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}