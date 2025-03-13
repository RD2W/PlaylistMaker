package com.practicum.playlistmaker.search.presentation.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.presentation.adapter.SearchHistoryAdapter
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import com.practicum.playlistmaker.search.presentation.state.SearchScreenState
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var foundTrackAdapter: TrackAdapter

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setupUI()
        observeViewModel()
        observeClickEvent()
    }

    private fun observeViewModel() {
        searchViewModel.searchScreenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Idle -> { /* Ничего не делать, начальное состояние */ }
                is SearchScreenState.Loading -> showProgressBar()
                is SearchScreenState.Content -> showFoundTracks(state.tracks)
                is SearchScreenState.NotFound -> showNotFoundPlaceholder()
                is SearchScreenState.NetworkError -> showNetworkErrorPlaceholder()
                is SearchScreenState.SearchHistory -> showSearchHistory(state.history)
            }
        }
    }

    private fun observeClickEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.clickEvent.collect { trackParcel ->
                    val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(trackParcel)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun showProgressBar() {
        with(binding) {
            pbSearchProgressBar.isVisible = true
            rvSearch.isVisible = false
            searchPlaceholderViewGroup.isVisible = false
        }
    }

    private fun showFoundTracks(foundTracks: List<Track>) {
        with(binding) {
            pbSearchProgressBar.isVisible = false
            searchPlaceholderViewGroup.visibility = View.GONE
            rvSearch.visibility = View.VISIBLE
        }
        foundTrackAdapter.updateTracks(foundTracks)
    }

    private fun showNotFoundPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_not_found_placeholder,
            placeholderText = getString(R.string.search_placeholder_not_found),
            isUpdateButtonVisible = false
        )
    }

    private fun showNetworkErrorPlaceholder() {
        setPlaceholder(
            placeholderImageResId = R.drawable.ic_no_internet_placeholder,
            placeholderText = getString(R.string.search_placeholder_network_error),
            isUpdateButtonVisible = true
        )
    }

    private fun showSearchHistory(history: List<Track>) {
        searchHistoryAdapter.updateTracks(history)
        updateSearchHistoryVisibility(history)
    }

    private fun setPlaceholder(
        placeholderImageResId: Int,
        placeholderText: String,
        isUpdateButtonVisible: Boolean
    ) {
        with(binding) {
            ivSearchPlaceholder.setImageResource(placeholderImageResId)
            tvSearchPlaceholder.text = placeholderText
            pbSearchProgressBar.isVisible = false
            rvSearch.visibility = View.GONE
            searchPlaceholderViewGroup.visibility = View.VISIBLE
            btnSearchUpdate.visibility = if (isUpdateButtonVisible) View.VISIBLE else View.GONE
        }
    }

    private fun launchPlayer(track: Track) {
        searchViewModel.clickDebounce(track)
    }

    private fun setupUI() {
        setupSearchInput()
        setupButtons()
        initializeSearchRecycler()
        initializeHistoryRecycler()
    }

    private fun initializeSearchRecycler() {
        if (!::foundTrackAdapter.isInitialized) {
            foundTrackAdapter = TrackAdapter { track ->
                searchViewModel.addTrackToHistory(track)
                launchPlayer(track)
            }
        }
        with(binding) {
            rvSearch.adapter = foundTrackAdapter
            rvSearch.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initializeHistoryRecycler() {
        if (!::searchHistoryAdapter.isInitialized) {
            searchHistoryAdapter = SearchHistoryAdapter { track ->
                launchPlayer(track)
            }
        }
        with(binding) {
            rvSearchHistory.adapter = searchHistoryAdapter
            rvSearchHistory.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupButtons() {
        with(binding) {
            btnSearchUpdate.setOnClickListener { searchViewModel.researchDebounce() }
            btnClearInput.setOnClickListener { clearSearch() }
            btnClearHistory.setOnClickListener { searchViewModel.clearSearchHistory() }
        }
    }

    private fun setupSearchInput() {
        binding.etvInputSearch.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchViewModel.researchDebounce()
                    true
                } else false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateUIForSearchInput(s)
                    searchViewModel.searchDebounce(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) updateSearchHistoryVisibility(searchHistoryAdapter.getCurrentHistory())
            }
        }
    }

    private fun updateSearchHistoryVisibility(history: List<Track>) {
        with(binding) {
            searchHistoryViewGroup.visibility =
                if (etvInputSearch.text.isNullOrEmpty() && etvInputSearch.hasFocus() && history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun clearSearch() {
        with(binding) {
            etvInputSearch.text.clear()
            rvSearch.visibility = View.GONE
            searchPlaceholderViewGroup.visibility = View.GONE
            foundTrackAdapter.clearTracks()
        }
        hideKeyboard()
    }

    private fun updateUIForSearchInput(s: CharSequence?) {
        with(binding) {
            if (s.isNullOrEmpty()) {
                btnClearInput.visibility = View.GONE
                rvSearch.visibility = View.GONE
                searchPlaceholderViewGroup.visibility = View.GONE
                updateSearchHistoryVisibility(searchHistoryAdapter.getCurrentHistory())
            } else {
                btnClearInput.visibility = View.VISIBLE
                searchHistoryViewGroup.visibility = View.GONE
            }
        }
    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        view?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}