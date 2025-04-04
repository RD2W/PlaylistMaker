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
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.presentation.adapter.playlists.PlaylistAdapter
import com.practicum.playlistmaker.media.presentation.state.PlaylistsScreenState
import com.practicum.playlistmaker.media.presentation.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val viewModel: PlaylistsViewModel by viewModel()
    private val adapter = PlaylistAdapter { playlist ->
        openPlaylist(playlist.playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistsBinding.bind(view)

        setupUI()
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
                viewModel.clickEvent.collect { playlistId ->
                    val action =
                        MediaFragmentDirections.actionMediaFragmentToPlaylistFragment(playlistId)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsScreenState.Loading -> showLoading()
                is PlaylistsScreenState.Content -> showContent(state.playlists)
                is PlaylistsScreenState.Empty -> showPlaceholder()
                is PlaylistsScreenState.Error -> showPlaceholder()
            }
        }
    }

    private fun setupUI() {
        setupButton()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvPlaylists.adapter = adapter
    }

    private fun setupButton() {
        binding.btnAddPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_addPlaylistFragment)
        }
    }

    private fun showLoading() {
        /** Показать индикатор загрузки, если в дальнейшем будет необходимо */
    }

    private fun showContent(playlists: List<Playlist>) {
        Log.d(
            "Playlists",
            "Playlists:\n${playlists.joinToString("\n") {
                "ID: ${it.playlistId}\nName: ${it.name}\nDescription: ${it.description}\nTrack Count: ${it.trackCount}"
            }}",
        )

        showPlaceholder(false)
        adapter.submitList(playlists)
    }

    private fun showPlaceholder(visibility: Boolean = true) {
        binding.rvPlaylists.isVisible = !visibility
        binding.grEmptyPlaylistsPlaceholder.isVisible = visibility
    }

    private fun openPlaylist(playlistId: Long) {
        viewModel.clickDebounce(playlistId)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
