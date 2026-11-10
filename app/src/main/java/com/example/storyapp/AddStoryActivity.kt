package com.example.storyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.maps.MapsActivity
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.PagingViewModel
import com.example.storyapp.viewmodel.UploadViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory.getInstance(
            LoginPreferences.getInstance(dataStore)
        )
    }

    private val uploadViewModel: UploadViewModel by viewModels {
        UploadViewModel.UploadViewModelFactory.getInstance(
            LoginPreferences.getInstance(dataStore)
        )
    }

    private val pagingViewModel: PagingViewModel by viewModels {
        PagingViewModel.PagingViewModelFactory.getInstance(
            LoginPreferences.getInstance(dataStore)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                val intent = Intent(this, PostImageActivity::class.java)
                startActivity(intent)
            }
        }
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        checkSession()

    }

    private fun checkSession() {
        mainViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {

                val adapter = StoryAdapter()
                binding.rvStory.adapter = adapter
                pagingViewModel.allStory("Bearer $it").observe(this) { story ->
                    adapter.submitData(lifecycle, story)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_needed), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            uploadViewModel.checkToken().observe(this) {

                Log.e(TAG, "toket = " + it)

            }
            mainViewModel.logout()
        } else if (item.itemId == R.id.map) {
            startActivity(Intent(this, MapsActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = AddStoryActivity::class.java.simpleName
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}