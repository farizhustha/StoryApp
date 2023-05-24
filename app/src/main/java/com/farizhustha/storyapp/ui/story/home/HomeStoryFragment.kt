package com.farizhustha.storyapp.ui.story.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.farizhustha.storyapp.adapter.ListStoryAdapter
import com.farizhustha.storyapp.adapter.LoadingStateAdapter
import com.farizhustha.storyapp.databinding.FragmentHomeStoryBinding
import com.farizhustha.storyapp.ui.ViewModelFactory

class HomeStoryFragment : Fragment() {

    private var _binding: FragmentHomeStoryBinding? = null
    private val binding get() = _binding

    private val viewModel: HomeStoryViewModel by viewModels {
        ViewModelFactory(context = requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setListStoryData()
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(activity)
        binding?.rvHomeStory?.layoutManager = layoutManager
    }

    private fun setListStoryData() {
        val adapter = ListStoryAdapter { story ->
            val toDetailFragment =
                HomeStoryFragmentDirections.actionHomeStoryFragmentToDetailStoryFragment()
            toDetailFragment.id = story.id
            findNavController().navigate(toDetailFragment)
        }
        binding?.rvHomeStory?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}