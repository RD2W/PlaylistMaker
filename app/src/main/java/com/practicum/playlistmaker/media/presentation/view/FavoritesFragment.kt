package com.practicum.playlistmaker.media.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.media.presentation.adapter.FavoriteTrackAdapter
import com.practicum.playlistmaker.media.presentation.state.FavoriteScreenState
import com.practicum.playlistmaker.media.presentation.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val viewModel: FavoritesViewModel by viewModel()
    private val adapter = FavoriteTrackAdapter { track ->
        launchPlayer(track)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)
        setupRecyclerView()
        observeViewModel()
        observeClickEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeClickEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickEvent.collect { trackParcel ->
                    val action =
                        MediaFragmentDirections.actionMediaFragmentToPlayerFragment(trackParcel)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvFavoritesTracks.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteScreenState.Loading -> showLoading()
                is FavoriteScreenState.Content -> showContent(state.tracks)
                is FavoriteScreenState.Empty -> showPlaceholder()
                is FavoriteScreenState.Error -> showPlaceholder()
            }
        }
    }

    private fun showLoading() {
        // Показать индикатор загрузки, если в дальнейшем будет необходимо
    }

    private fun showContent(tracks: List<Track>) {
        Log.d("FavoriteTracks", "Tracks: ${tracks.joinToString { it.trackName.toString() }}")
        showPlaceholder(false)
        adapter.submitList(tracks)
    }

    private fun showPlaceholder(visibility: Boolean = true) {
        binding.rvFavoritesTracks.isVisible = !visibility
        binding.grEmptyFavoritesPlaceholder.isVisible = visibility
    }

    private fun launchPlayer(track: Track) {
        viewModel.clickDebounce(track)
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}
