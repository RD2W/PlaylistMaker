package com.practicum.playlistmaker.listmaker.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.practicum.playlistmaker.listmaker.presentation.viewmodel.AddPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlaylistFragment : Fragment(R.layout.fragment_add_playlist) {

    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding: FragmentAddPlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private val viewModel: AddPlaylistViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPlaylistBinding.bind(view)

        // Используем doOnTextChanged для обработки изменений текста в etvPlaylistName
        binding.etvPlaylistName.doOnTextChanged { inputText, _, _, _ ->
            val isNotEmpty = !inputText.isNullOrBlank()
            with(binding) {
                btnCreatePlaylist.isEnabled = isNotEmpty
                if (isNotEmpty) {
                    btnCreatePlaylist.setBackgroundColor(
                        resources.getColor(R.color.light_blue,null)
                    )
                } else {
                    btnCreatePlaylist.setBackgroundColor(
                        resources.getColor(R.color.button_gray,null)
                    )
                }
            }
        }

        binding.btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_addPlaylistFragment_to_playlistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddPlaylistFragment()
    }
}