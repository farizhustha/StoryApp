package com.farizhustha.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.ui.auth.AuthenticationActivity
import com.farizhustha.storyapp.ui.main.dataStore
import com.farizhustha.storyapp.ui.story.maps.MapsActivity

class StoryActivity : AppCompatActivity() {
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(pref)
        viewModel = ViewModelProvider(
            this, factory
        )[StoryViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()

                val intent = Intent(this, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            R.id.menu_toAdd -> {
                val navController = findNavController(R.id.fragment_story_container)
                val currentFragment = navController.currentDestination?.id ?: 0

                if (currentFragment != R.id.addStoryFragment) {
                    navController.navigate(R.id.addStoryFragment)
                }
                true
            }

            R.id.menu_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> true
        }
    }
}