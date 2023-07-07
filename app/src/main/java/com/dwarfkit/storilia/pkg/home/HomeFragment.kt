package com.dwarfkit.storilia.pkg.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarfkit.storilia.R
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.databinding.FragmentHomeBinding
import com.dwarfkit.storilia.pkg.StoryViewModelFactory
import com.dwarfkit.storilia.pkg.adapter.LoadingStateAdapter
import com.dwarfkit.storilia.pkg.adapter.StoriesAdapter
import com.dwarfkit.storilia.pkg.login.LoginActivity
import com.google.android.gms.maps.GoogleMap

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val storiesAdapter = StoriesAdapter()

    private val homeViewModel: HomeViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            UserPreferences.getInstance(requireContext().dataStore), requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        getToken()
        setAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        homeViewModel.logout()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(requireContext(), getString(R.string.text_logout_success), Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getToken() {
        homeViewModel.getUser().observe(requireActivity()) { user ->
            if (user.token.isNotEmpty()) getAllStories(user.token)
        }
    }

    private fun getAllStories(token: String) {
        val result = homeViewModel.getAllStories(token)
        result.observe(requireActivity()){
            val storyData = it
            storiesAdapter.submitData(lifecycle,storyData)
        }
    }

    private fun setAdapter() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = storiesAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storiesAdapter.retry()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}