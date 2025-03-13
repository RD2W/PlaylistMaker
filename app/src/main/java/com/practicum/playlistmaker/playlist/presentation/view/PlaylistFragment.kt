package com.practicum.playlistmaker.playlist.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.playlist.presentation.viewmodel.PlaylistViewModel

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val viewModel: PlaylistViewModel by viewModels()

    private val bottomSheetBehaviorTracks by lazy {
        BottomSheetBehavior.from(binding.bottomSheetTracks)
    }

    private val bottomSheetBehaviorMore by lazy {
        BottomSheetBehavior.from(binding.bottomSheetMore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistBinding.bind(view)
        setMockUI()

        setupInitialBottomSheetState()
        setupButtons()
        setupBottomSheetMoreCallback()
    }

    private fun setupInitialBottomSheetState() {
        bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnSharePlaylist.setOnClickListener {
                //
            }

            btnMoreActions.setOnClickListener {
                toggleBottomSheetMoreState()
            }

            overlay.setOnClickListener {
                bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
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
                // Обработка анимации скольжения
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
                //TODO()
            }

            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                //TODO()
            }

            BottomSheetBehavior.STATE_SETTLING -> {
                //TODO()
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

    private fun setMockUI() {
        with(binding) {
            tvPlaylistNameTitle.text = "Лучшие песни 2025"
            tvPlaylistDescriptionTitle.text = "Сборник всех поколений и народов"
            tvPlaylistYearTitle.text = "2025"
            tvTracksTimeTitle.text = "0 минут"
            tvTracksCountTitle.text = " 0 треков"
            tvPlaylistName.text = "Лучшие песни 2025"
            tvPlaylistCount.text = " 0 треков"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}