package com.farizhustha.storyapp.ui.story.add

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.farizhustha.storyapp.databinding.FragmentAddStoryBinding
import com.farizhustha.storyapp.ui.story.add.camera.CameraActivity
import com.farizhustha.storyapp.ui.story.StoryViewModel
import com.farizhustha.storyapp.utils.Utils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoryViewModel by activityViewModels()

    private var token: String = ""

    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModelObserver()
    }

    private fun setupView() {
        binding.apply {
            btnAddCamera.setOnClickListener { startCameraX() }
            btnAddGallery.setOnClickListener { startGallery() }
            btnAddUpload.setOnClickListener { upload() }

            edtAddDescription.doAfterTextChanged {
                if (it.isNullOrEmpty()) {
                    edtAddDescription.error = "Deskripsi tidak boleh kosong"
                } else {
                    edtAddDescription.error = null
                }
                updateButtonStatus()
            }
        }
    }

    private fun setupViewModelObserver() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            this.token = token
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                if (message == "Story created successfully") {
                    findNavController().navigateUp()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.file.observe(viewLifecycleOwner) { file ->
            getFile = file
            binding.ivAddPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
        }
    }

    private fun upload() {
        val text = binding.edtAddDescription.text

        if (getFile != null) {
            val file = Utils.reduceFileImage(getFile as File)

            val description = text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, requestImageFile
            )
            viewModel.addStory(token, imageMultipart, description)

        } else {
            Toast.makeText(
                activity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(activity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION") it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                Utils.rotateFile(file, isBackCamera)
                viewModel.setFile(file)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = Utils.uriToFile(uri, requireActivity())
                viewModel.setFile(myFile)
            }
        }
    }

    private fun updateButtonStatus() {
        binding.apply {
            btnAddUpload.isEnabled = !(edtAddDescription.text.isNullOrEmpty())
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