package org.firmanmardiyanto.storyapp.poststory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.storyapp.R
import org.firmanmardiyanto.storyapp.databinding.ActivityPostStoryBinding
import org.firmanmardiyanto.storyapp.utils.createCustomTempFile
import org.firmanmardiyanto.storyapp.utils.reduceFileImage
import org.firmanmardiyanto.storyapp.utils.uriToFile
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private val postStoryViewModel: PostStoryViewModel by viewModel()
    private var getFile: File? = null
    private var location: Location? = null

    companion object {
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
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
                    getString(R.string.not_granted_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        setSupportActionBar(binding.toolbar)
        location = getCurrentLocation()
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnPostStory.setOnClickListener { uploadImage() }

    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostStoryActivity,
                "org.firmanmardiyanto.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = binding.etDescription.text.toString()
            if (descriptionText.isEmpty()) {
                Toast.makeText(this, getString(R.string.required_description), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val description =
                    descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                var latitude: RequestBody? = null;
                var longitude: RequestBody? = null;
                if (location != null) {
                    latitude =
                        location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    longitude =
                        location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }
                try {
                    postStoryViewModel.postStory(imageMultipart, description, latitude, longitude)
                        .observe(
                            this,
                        ) {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.btnPostStory.isEnabled = false
                                    binding.btnPostStory.text = getString(R.string.posting_with_dot)
                                }
                                is Resource.Success -> {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.story_has_posted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setResult(RESULT_OK, Intent())
                                    onBackPressed()
                                    finish()
                                }
                                is Resource.Error -> {
                                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                                    binding.btnPostStory.text = getString(R.string.post)
                                    binding.btnPostStory.isEnabled = true
                                }
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(
                this@PostStoryActivity,
                getString(R.string.please_select_a_picture),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostStoryActivity)
            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private fun getCurrentLocation(): Location? {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)
        val location = locationManager.getLastKnownLocation(provider.toString())
        if (location != null) {
            binding.tvCurrentLocation.text =
                getString(
                    R.string.current_location,
                    "${location.latitude}, ${location.longitude}"
                )
            return location
        }
        binding.tvCurrentLocation.text = getString(R.string.current_location, "-")
        return location
    }

}
