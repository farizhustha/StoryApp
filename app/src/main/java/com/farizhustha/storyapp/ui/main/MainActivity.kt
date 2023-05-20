package com.farizhustha.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.ui.auth.AuthenticationActivity
import com.farizhustha.storyapp.ui.story.StoryActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        setupViewModel()
        setupViewModelObserver()
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(pref)
        viewModel = ViewModelProvider(
            this, factory
        )[MainViewModel::class.java]
    }

    private fun setupViewModelObserver() {
        val authIntent = Intent(this, AuthenticationActivity::class.java)
        val homeIntent = Intent(this, StoryActivity::class.java)

        viewModel.getUserToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                startActivity(homeIntent)
                finish()

            } else {
                startActivity(authIntent)
                finish()
            }
        }
    }
}