package com.farizhustha.storyapp.ui.story.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.farizhustha.storyapp.databinding.FragmentDetailStoryBinding
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.ui.main.dataStore
import com.farizhustha.storyapp.utils.Utils

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private val args: DetailStoryFragmentArgs by navArgs()
    private lateinit var viewModel: DetailStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupViewModelObserver()
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val factory = ViewModelFactory(pref)
        viewModel = ViewModelProvider(
            this, factory
        )[DetailStoryViewModel::class.java]
    }

    private fun setupViewModelObserver() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (!viewModel.story.isInitialized) {
                viewModel.getDetailStory(token, args.id)
            }

        }

        viewModel.story.observe(viewLifecycleOwner) { story ->
            binding.apply {
                Glide.with(requireActivity()).load(story.photoUrl).into(ivDetailPhoto)

                tvDetailName.text = story.name
                tvDetailCreated.text = Utils.formatDate(story.createdAt.toString())
                tvDetailDescription.text = story.description
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}