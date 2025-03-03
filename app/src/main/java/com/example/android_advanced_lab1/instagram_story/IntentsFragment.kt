package com.example.android_advanced_lab1.instagram_story

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.android_advanced_lab1.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IntentsFragment : Fragment() {

    private lateinit var imagePreview: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var btnShareInstagram: Button
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_intents, container, false)

        imagePreview = view.findViewById(R.id.imagePreview)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        btnShareInstagram = view.findViewById(R.id.btnShareInstagram)

        btnPickImage.setOnClickListener { pickImageFromGallery() }
        btnShareInstagram.setOnClickListener { shareToInstagram() }

        return view
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                imagePreview.setImageURI(it)
                imageUri = saveImageToCache(it)
                btnShareInstagram.isEnabled = true
            }
        }
    }

    private fun saveImageToCache(uri: Uri): Uri? {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        val cachePath = File(requireContext().cacheDir, "images")
        cachePath.mkdirs()

        val file = File(cachePath, "shared_image.png")
        Log.d("FileCheck", "Cache directory: ${cachePath.absolutePath}, Exists: ${file.exists()}")

        return try {
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            Log.d("FileCheck", "File saved at: ${file.absolutePath}")

            FileProvider.getUriForFile(requireContext(), "com.example.android_advanced_lab1.provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }



    private fun shareToInstagram() {
        if (!isInstagramInstalled()) {
            Toast.makeText(requireContext(), "Instagram не установлен!", Toast.LENGTH_SHORT).show()
            return
        }

        imageUri?.let {
            val intent = Intent("com.instagram.share.ADD_TO_STORY")
            intent.setDataAndType(it, "image/png")
            intent.putExtra("source_application", "604739712336887") // Укажи свой Facebook App ID
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requireActivity().grantUriPermission("com.instagram.android", it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (requireActivity().packageManager.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Не удалось найти Instagram!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isInstagramInstalled(): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        return try {
            packageManager.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
