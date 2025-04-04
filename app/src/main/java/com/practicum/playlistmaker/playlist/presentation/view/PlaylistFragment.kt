package com.practicum.playlistmaker.playlist.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.convertMillisToMinutes
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.playlist.presentation.adapter.TrackInPlaylistAdapter
import com.practicum.playlistmaker.playlist.presentation.state.PlaylistScreenState
import com.practicum.playlistmaker.playlist.presentation.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val args: PlaylistFragmentArgs by navArgs()
    private val viewModel: PlaylistViewModel by viewModel()

    private val bottomSheetBehaviorTracks by lazy {
        BottomSheetBehavior.from(binding.bottomSheetTracks)
    }

    private val bottomSheetBehaviorMore by lazy {
        BottomSheetBehavior.from(binding.bottomSheetMore)
    }

    private val adapter = TrackInPlaylistAdapter(
        onTrackClick = { track -> launchPlayer(track) },
        onTrackLongClick = { track -> showDeleteTrackDialog(track) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadPlaylist(args.playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        observeClickEvent()
        setupInitialBottomSheetState()
        setupButtons()
        setupBottomSheetMoreCallback()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is PlaylistScreenState.Loading -> { /* Можно показать ProgressBar */ }
                    is PlaylistScreenState.Content -> { updateUI(state.playlist) }
                    is PlaylistScreenState.Error -> { /* Можно показать Placeholder */ }
                    is PlaylistScreenState.Deleted -> navigateBack()
                }
            }
        }
    }

    private fun observeClickEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickEvent.collect { trackParcel ->
                    val action =
                        PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(
                            trackParcel,
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun updateUI(playlist: Playlist) {
        with(binding) {
            tvPlaylistNameTitle.text = playlist.name
            tvPlaylistDescriptionTitle.text = playlist.description
            tvPlaylistDescriptionTitle.isVisible = playlist.description.isNotBlank()
            tvPlaylistYearTitle.text = playlist.creationDate

            // Форматируем длительность в минуты
            val minutes = convertMillisToMinutes(playlist.totalDurationMillis)
            tvTracksTimeTitle.text = resources.getQuantityString(
                R.plurals.minutes_plurals, minutes, minutes,
            )

            // Форматируем количество треков
            val trackCount = resources.getQuantityString(
                R.plurals.tracks_plurals,
                playlist.trackCount,
                playlist.trackCount,
            )
            tvTracksCountTitle.text = getString(R.string.bullet_prefix, trackCount)

            // Устанавливаем сведения о плейлисте для BottomSheetMore
            tvPlaylistName.text = playlist.name
            tvPlaylistCount.text = trackCount

            playlist.coverFilePath?.let { coverPath ->
                loadImageToViews(coverPath, ivCoverFrame, ivPlaylistCover)
            }

            showTracksList(playlist.trackList)
        }
    }

    private fun loadImageToViews(
        imagePath: String,
        vararg imageViews: ShapeableImageView,
    ) {
        val file = File(imagePath)
        if (!file.exists()) {
            imageViews.forEach { it.setImageResource(R.drawable.ic_track_placeholder) }
            return
        }

        val requestBuilder = Glide.with(requireContext())
            .load(file)
            .placeholder(R.drawable.ic_track_placeholder)
            .error(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()

        imageViews.forEach { imageView ->
            requestBuilder.into(imageView)
        }
    }

    private fun setupRecyclerView() {
        binding.rvTrackList.adapter = adapter
    }

    private fun showTracksList(tracks: List<Track>) {
        Log.d("PlaylistTracks", "Tracks: ${tracks.joinToString { it.trackName.toString() }}")
        adapter.submitList(tracks)
    }

    private fun launchPlayer(track: Track) = viewModel.clickDebounce(track)

    private fun navigateBack() = findNavController().popBackStack()

    private fun sharePlaylist() = viewModel.sharePlaylist()

    private fun setupInitialBottomSheetState() {
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener { navigateBack() }
            btnSharePlaylist.setOnClickListener { sharePlaylist() }
            btnMoreActions.setOnClickListener { toggleBottomSheetMoreState() }
            tvShareItem.setOnClickListener { sharePlaylist() }
            tvEditInfoItem.setOnClickListener { }
            tvDeleteListItem.setOnClickListener { showDeletePlaylistDialog() }

            overlay.setOnClickListener {
                bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_delete_playlist_title)
            .setMessage(getString(R.string.playlist_delete_playlist_message))
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deletePlaylist() }
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_delete_track_title)
            .setMessage(getString(R.string.playlist_delete_track_message, track.trackName))
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.removeFromPlaylist(track.trackId) }
            .show()
    }

    private fun toggleBottomSheetMoreState() {
        if (bottomSheetBehaviorMore.state == BottomSheetBehavior.STATE_HIDDEN) {
            binding.bottomSheetMore.bringToFront()
            bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupBottomSheetMoreCallback() {
        bottomSheetBehaviorMore.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleBottomSheetMoreStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })
    }

    private fun handleBottomSheetMoreStateChange(newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                hideBottomSheetTracks()
                showOverlay()
            }

            BottomSheetBehavior.STATE_HIDDEN -> {
                showBottomSheetTracks()
                hideOverlay()
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

    private fun hideBottomSheetTracks() {
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showBottomSheetTracks() {
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showOverlay() {
        binding.overlay.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        binding.overlay.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}
