package com.farizhustha.storyapp.ui.story.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.farizhustha.storyapp.databinding.FragmentHomeStoryBinding
import com.farizhustha.storyapp.service.remote.response.Story
import com.farizhustha.storyapp.ui.story.StoryViewModel

class HomeStoryFragment : Fragment() {

    private var _binding: FragmentHomeStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModelObserver()
    }

    private fun setupViewModelObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.listStory.observe(viewLifecycleOwner) { listStory ->
            setListStoryData(listStory)
            if (listStory.isEmpty()){
                Toast.makeText(activity, "Tidak ada story", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvHomeStory.layoutManager = layoutManager
    }

    private fun setListStoryData(listStory: List<Story>) {
        val adapter = ListStoryAdapter(listStory) { story ->
            val toDetailFragment =
                HomeStoryFragmentDirections.actionHomeStoryFragmentToDetailStoryFragment()
            if (story.id != null) toDetailFragment.id = story.id
            findNavController().navigate(toDetailFragment)
        }
        binding.rvHomeStory.adapter = adapter
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

    companion object
}