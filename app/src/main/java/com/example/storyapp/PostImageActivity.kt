package com.example.storyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.api.Result
import com.example.storyapp.databinding.ActivityPostImageBinding
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.viewmodel.UploadViewModel
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class PostImageActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPostImageBinding
    private val uploadViewModel: UploadViewModel by viewModels {
        UploadViewModel.UploadViewModelFactory.getInstance(
            LoginPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAdd.setOnClickListener {
            uploadViewModel.checkToken().observe(this) {
                if (it == null) {
                    val intent = Intent(this@PostImageActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "toket = " + it)
                    uploadImage("Bearer $it")
                }
            }

        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostImageActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.ivImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostImageActivity)
                getFile = myFile
                binding.ivImage.setImageURI(uri)
            }
        }
    }

    private fun uploadImage(token: String) {
        val description = binding.etDesc.text.toString()
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            binding.progressBar.visibility = View.VISIBLE
            val result = uploadViewModel.uploadStory(token, file, description)
            result.observe(this@PostImageActivity) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val error = it.error
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this, getString(R.string.succes_upload), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, AddStoryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            if(description.isNullOrEmpty()) binding.etDesc.error = getString(R.string.null_desc)
            Toast.makeText(
                this@PostImageActivity,
                getString(R.string.image_null),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = PostImageActivity::class.java.simpleName
        const val CAMERA_X_RESULT = 200
        private var getFile: File? = null
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}