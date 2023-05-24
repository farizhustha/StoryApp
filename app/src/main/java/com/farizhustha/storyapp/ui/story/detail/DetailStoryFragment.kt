package com.farizhustha.storyapp.ui.story.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.farizhustha.storyapp.databinding.FragmentDetailStoryBinding
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.utils.Utils

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding
    private val args: DetailStoryFragmentArgs by navArgs()
    private val viewModel: DetailStoryViewModel by viewModels {
        ViewModelFactory(context = requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelObserver()
    }

    private fun setupViewModelObserver() {
        viewModel.getDetailStory(args.id).observe(viewLifecycleOwner) { story ->
            binding?.apply {
                Glide.with(requireActivity()).load(story.photoUrl).into(ivDetailPhoto)

                tvDetailName.text = story.name
                tvDetailCreated.text = Utils.formatDate(story.createdAt.toString())
                tvDetailDescription.text = story.description
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}