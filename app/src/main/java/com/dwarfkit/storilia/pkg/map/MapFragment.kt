package com.dwarfkit.storilia.pkg.map

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.dwarfkit.storilia.R
import com.dwarfkit.storilia.data.Resource
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.databinding.FragmentMapBinding
import com.dwarfkit.storilia.pkg.StoryViewModelFactory
import com.dwarfkit.storilia.pkg.adapter.CustomInfoWindowForGoogleMap
import com.dwarfkit.storilia.pkg.detail.DetailActivity
import com.dwarfkit.storilia.pkg.detail.DetailActivity.Companion.EXTRA_STORY_DETAIL
import com.dwarfkit.storilia.utils.LoadingDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapViewModel: MapViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            UserPreferences.getInstance(requireContext().dataStore), requireContext()
        )
    }

    companion object {
        private val TAG = MapFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.map_options, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.normal_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        true
                    }
                    R.id.satellite_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        true
                    }
                    R.id.terrain_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        true
                    }
                    R.id.hybrid_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getToken()
        setMapStyle()
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),
                    R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.text_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.text_error_cant_find_style), exception)
        }
    }

    private fun getToken() {
        mapViewModel.getUser().observe(this) { user ->
            if (user.token.isNotEmpty()) loadAllMarker(user.token)
        }
    }

    private fun loadAllMarker(token: String) {
        val result = mapViewModel.getAllStoriesWithLocation(token)
        result.observe(this) {
            when (it) {
                is Resource.Error -> {
                    LoadingDialog.hideLoading()
                    val data = it.error
                    Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    LoadingDialog.hideLoading()
                    val listStory = it.data
                    addMarkers(listStory)
                }
                is Resource.Loading -> LoadingDialog.startLoading(requireContext())
            }
        }
    }

    private fun addMarkers(listStory: List<StoryEntity>) {
        for (story in listStory) {
            val latLng = LatLng(story.lat, story.lon)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
            )
            boundsBuilder.include(latLng)
            marker?.tag = story
            mMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(requireContext()))
            mMap.setOnInfoWindowClickListener {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(EXTRA_STORY_DETAIL, it.tag as StoryEntity)
                startActivity(intent)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.15).toInt()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width,height, padding)
        mMap.moveCamera(cu)
        mMap.animateCamera(cu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}