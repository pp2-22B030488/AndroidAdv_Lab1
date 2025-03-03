package com.example.android_advanced_lab1.instagram_story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.android_advanced_lab1.databinding.FragmentIntentsBinding

class IntentsFragment : Fragment() {
    private var _binding: FragmentIntentsBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.imagePreview.setImageURI(uri)
            binding.btnShareInstagram.isEnabled = true
        } else {
            Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnShareInstagram.setOnClickListener {
            imageUri?.let { uri ->
                shareImageToInstagramStories(uri)
            } ?: Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareImageToInstagramStories(imageUri: Uri) {
        val backgroundAssetUri = imageUri.toString()
        val intent = Intent("com.instagram.share.ADD_TO_STORY")
        intent.setDataAndType(Uri.parse(backgroundAssetUri), "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Добавляем Facebook App ID
        intent.putExtra("source_application", "604739712336887") 

        activity?.grantUriPermission("com.instagram.android", imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (requireActivity().packageManager.resolveActivity(intent, 0) != null) {
            activity?.startActivityForResult(intent, 0)
        } else {
            Toast.makeText(requireContext(), "Instagram не установлен или не поддерживает публикацию в Stories", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
