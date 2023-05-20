package com.farizhustha.storyapp.ui.auth.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.databinding.FragmentSignInBinding
import com.farizhustha.storyapp.model.User
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.ui.main.dataStore
import com.farizhustha.storyapp.ui.story.StoryActivity
import com.farizhustha.storyapp.utils.UtilsContext.dpToPx
import com.farizhustha.storyapp.utils.UtilsContext.getScreenWidth

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupView()
        setupViewModelObserver()
        playAnimation()
    }


    private fun setupView() {

        binding.apply {
            val editTextEmail = edtLayoutSigninEmail.editText
            val editTextPassword = edtLayoutSigninPassword.editText

            editTextEmail?.id = R.id.edt_sign_in_email
            editTextPassword?.id = R.id.edt_sign_in_password

            editTextEmail?.doAfterTextChanged {
                updateButtonStatus()
            }

            editTextPassword?.doAfterTextChanged {
                updateButtonStatus()
            }

            btnSigninLogin.setOnClickListener {
                val email = editTextEmail?.text
                val password = editTextPassword?.text

                val user = User(
                    email = email.toString(), password = password.toString()
                )
                viewModel.login(user)
            }

            btnSigninRegister.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInFragment_to_signUpFragment))
        }
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ViewModelFactory(pref)
        viewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]
    }

    private fun setupViewModelObserver() {
        viewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                val intent = Intent(activity, StoryActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun updateButtonStatus() {

        binding.apply {
            val isEmailError =
                edtLayoutSigninEmail.editText?.text.isNullOrEmpty() || edtLayoutSigninEmail.error != null
            val isPasswordError =
                edtLayoutSigninPassword.editText?.text.isNullOrEmpty() || edtLayoutSigninPassword.error != null
            btnSigninLogin.isEnabled = !(isEmailError || isPasswordError)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun playAnimation() {
        val width = requireActivity().dpToPx(requireActivity().getScreenWidth())

        val titleFade =
            ObjectAnimator.ofFloat(binding.tvSigninTitle, View.ALPHA, 1f).setDuration(2000)
        val titleX =
            ObjectAnimator.ofFloat(binding.tvSigninTitle, View.TRANSLATION_X, -width, 0f).apply {
                    duration = 2000
                }

        val email =
            ObjectAnimator.ofFloat(binding.edtLayoutSigninEmail, View.ALPHA, 1f).setDuration(1000)
        val password = ObjectAnimator.ofFloat(binding.edtLayoutSigninPassword, View.ALPHA, 1f)
            .setDuration(1000)
        val buttonLogin =
            ObjectAnimator.ofFloat(binding.btnSigninLogin, View.ALPHA, 1f).setDuration(1000)

        val textRegisterFade =
            ObjectAnimator.ofFloat(binding.tvSigninRegister, View.ALPHA, 1f).setDuration(0)
        val textRegisterY = ObjectAnimator.ofFloat(
            binding.tvSigninRegister, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f
        ).apply {
                duration = 1000
            }

        val buttonRegisterFade =
            ObjectAnimator.ofFloat(binding.btnSigninRegister, View.ALPHA, 1f).setDuration(0)
        val buttonRegisterY = ObjectAnimator.ofFloat(
            binding.btnSigninRegister, View.TRANSLATION_Y, requireActivity().dpToPx(40f), 0f
        ).apply {
                duration = 1000
            }

        val togetherTitle = AnimatorSet().apply {
            playTogether(titleX, titleFade)
        }
        val togetherRegister = AnimatorSet().apply {
            playTogether(
                email,
                password,
                buttonLogin,
                textRegisterFade,
                textRegisterY,
                buttonRegisterFade,
                buttonRegisterY
            )
        }
        AnimatorSet().apply {
            playSequentially(togetherTitle, togetherRegister)
            start()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}