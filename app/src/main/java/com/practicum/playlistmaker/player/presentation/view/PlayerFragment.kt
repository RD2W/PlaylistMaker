package com.practicum.playlistmaker.player.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.NEW_PLAYLIST_ID
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.model.ErrorType
import com.practicum.playlistmaker.player.presentation.adapter.PlaylistLinearAdapter
import com.practicum.playlistmaker.player.presentation.state.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private lateinit var bottomSheetAddToPlaylist: BottomSheetBehavior<LinearLayout>

    private val args: PlayerFragmentArgs by navArgs()
    private val viewModel: PlayerViewModel by viewModel()

    private val adapter = PlaylistLinearAdapter { playlist ->
        val message = getString(R.string.player_track_added_to_playlist_message, playlist.name)
        addTrackToPlaylist(playlist.playlistId)
        showBottomSheetAddPlaylist(false)
        showSnackbar(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getTrack(args.trackParcel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        setupUI()
        observePlayerState()
        setupRecyclerView()
        setupButtons()
    }

    private fun observePlayerState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.track?.let { track ->
                updateUIWithTrack(track)
            }
            Timber.d("ScreenState: $state")
            state.screenState.let { screenState ->
                if (state.showToast && state.screenState is PlayerScreenState.NotReady) {
                    showToast(state.screenState)
                    viewModel.onToastShown()
                }

                when (screenState) {
                    is PlayerScreenState.NotReady -> disablePlayButton()
                    is PlayerScreenState.Ready -> showPlayButton()
                    is PlayerScreenState.Playing -> showPauseButton()
                    is PlayerScreenState.Paused -> showPlayButton()
                    is PlayerScreenState.Stopped -> showPlayButton()
                }
            }
            binding.playerListenedTrackTime.text = state.currentPosition
            updateFavoriteButton(state.isFavorite)
            updatePlaylistButton(state.isInPlaylist)
            showPlaylists(state.availablePlaylists)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        updateButtonIcon(
            view = binding.btnAddToFavorites,
            isActive = isFavorite,
            activeIconRes = R.drawable.ic_player_add_favs_button_active,
            inactiveIconRes = R.drawable.ic_player_add_favs_button,
        )
    }

    private fun updatePlaylistButton(isInPlaylist: Boolean) {
        updateButtonIcon(
            view = binding.btnAddToPlaylist,
            isActive = isInPlaylist,
            activeIconRes = R.drawable.ic_player_add_plist_button_active,
            inactiveIconRes = R.drawable.ic_player_add_plist_button,
        )
    }

    private fun updateButtonIcon(
        view: ImageView,
        isActive: Boolean,
        activeIconRes: Int,
        inactiveIconRes: Int,
    ) {
        val iconRes = if (isActive) activeIconRes else inactiveIconRes
        view.setImageResource(iconRes)
    }

    private fun updateUIWithTrack(track: Track) {
        with(binding) {
            playerTrackName.text = track.trackName
            playerArtistName.text = track.artistName
            playerTrackDuration.text = track.trackTime
            playerTrackYear.text = track.releaseDate
            playerTrackGenre.text = track.primaryGenreName
            playerTrackCountry.text = track.country
            if (track.collectionName.isNullOrBlank()) {
                groupCollectionName.visibility = View.GONE
            } else {
                playerTrackAlbum.text = track.collectionName
            }

            Glide.with(playerTrackCover.context)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.ic_track_placeholder)
                .fitCenter()
                .centerCrop()
                .into(playerTrackCover)
        }
    }

    private fun showPlayButton() {
        with(binding) {
            btnControl.setImageResource(R.drawable.ic_player_play_button)
            btnControl.isEnabled = true
        }
    }

    private fun showPauseButton() {
        with(binding) {
            btnControl.setImageResource(R.drawable.ic_player_pause_button)
            btnControl.isEnabled = true
        }
    }

    private fun disablePlayButton() {
        with(binding) {
            btnControl.setImageResource(R.drawable.ic_player_play_button)
            btnControl.isEnabled = false
        }
    }

    private fun showToast(state: PlayerScreenState.NotReady) {
        val errorMessage = when (state.error) {
            is ErrorType.NoInternet -> getString(R.string.player_no_internet_connection_toast)
            is ErrorType.PlayerException -> getString(R.string.player_error_exception_toast)
            is ErrorType.Unknown -> getString(R.string.player_error_unknown_toast)
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG,
        ).show()
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        Timber.d(
            "Playlists:\n${
                playlists.joinToString("\n") {
                    "ID: ${it.playlistId}\nName: ${it.name}\nTrack Count: ${it.trackCount}"
                }
            }",
        )
        with(binding) {
            val showList = playlists.isNotEmpty()
            rvPlaylistsList.isVisible = showList
            tvNoPlaylist.isVisible = !showList
            if (showList) adapter.submitList(playlists)
        }
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            overlay.setOnClickListener {
                showBottomSheetAddPlaylist(false)
            }

            btnControl.setOnClickListener {
                viewModel.controlPlayer()
            }

            btnAddToFavorites.setOnClickListener {
                viewModel.onFavoriteClicked()
            }

            btnAddToPlaylist.setOnClickListener {
                showBottomSheetAddPlaylist(true)
            }

            btnAddNewPlaylist.setOnClickListener {
                showBottomSheetAddPlaylist(false)
                addNewPlaylist()
            }
        }
    }

    private fun setupUI() {
        setupBottomSheet()
        setupBottomSheetAddToPlaylistCallback()
        showBottomSheetAddPlaylist(false)
    }

    private fun setupRecyclerView() {
        binding.rvPlaylistsList.adapter = adapter
    }

    private fun addTrackToPlaylist(playlistId: Long) {
        viewModel.addToPlaylist(playlistId)
    }

    private fun addNewPlaylist() {
        val action =
            PlayerFragmentDirections.actionPlayerFragmentToAddPlaylistFragment(NEW_PLAYLIST_ID)
        findNavController().navigate(action)
    }

    private fun setupBottomSheet() {
        bottomSheetAddToPlaylist = BottomSheetBehavior.from(binding.bottomSheetAddToPlaylist)
    }

    private fun setupBottomSheetAddToPlaylistCallback() {
        bottomSheetAddToPlaylist.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleBottomSheetAddToPlaylistStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.let { safeBinding ->
                    safeBinding.overlay.alpha = slideOffset + 1f
                }
            }
        })
    }

    private fun handleBottomSheetAddToPlaylistStateChange(newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                showOverlay(true)
            }

            BottomSheetBehavior.STATE_HIDDEN -> {
                showOverlay(false)
            }

            BottomSheetBehavior.STATE_DRAGGING -> {
                // TODO()
            }

            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                // TODO()
            }

            BottomSheetBehavior.STATE_SETTLING -> {
                // TODO()
            }
        }
    }

    private fun showOverlay(visibility: Boolean) {
        binding.overlay.isVisible = visibility
    }

    private fun showBottomSheetAddPlaylist(visible: Boolean) {
        bottomSheetAddToPlaylist.state = if (visible) {
            BottomSheetBehavior.STATE_COLLAPSED
        } else {
            BottomSheetBehavior.STATE_HIDDEN
        }
        showOverlay(visible)
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseTrack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
