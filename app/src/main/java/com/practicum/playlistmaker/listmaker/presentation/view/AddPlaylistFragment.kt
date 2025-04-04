package com.practicum.playlistmaker.listmaker.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.practicum.playlistmaker.listmaker.presentation.state.AddPlaylistUiState
import com.practicum.playlistmaker.listmaker.presentation.viewmodel.AddPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class AddPlaylistFragment : Fragment(R.layout.fragment_add_playlist) {

    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding: FragmentAddPlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private val viewModel: AddPlaylistViewModel by viewModel()
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPermission: String

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            if (shouldShowRequestPermissionRationale(currentPermission)) {
                showPermissionRationaleDialog(currentPermission)
            } else {
                showPermissionSettingsDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.d("ImagePicker", "Selected URI: $uri")
                        viewModel.setSelectedImageUri(uri)
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPlaylistBinding.bind(view)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is AddPlaylistUiState.Success -> navigateBack()
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coverImage.collect { file ->
                file?.let {
                    Log.d("CoverImage", "File loaded: ${it.path}")
                    loadImageWithGlide(it)
                } ?: run {
                    binding.ivCoverFrame.setImageResource(R.drawable.ic_playlist_cover_frame)
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            etvPlaylistName.doOnTextChanged { text, _, _, _ ->
                val isNotEmpty = !text.isNullOrBlank()
                btnCreatePlaylist.isEnabled = isNotEmpty
                btnCreatePlaylist.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isNotEmpty) R.color.light_blue else R.color.button_gray,
                    ),
                )
            }

            btnCreatePlaylist.setOnClickListener {
                val name = etvPlaylistName.text.toString()
                val description = etvPlaylistDescription.text.toString()
                if (name.isNotEmpty()) viewModel.createPlaylist(name, description)
            }

            ivCoverFrame.setOnClickListener {
                checkAndRequestPermission()
            }
        }
    }

    private fun loadImageWithGlide(file: File) {
        Glide.with(requireContext())
            .load(file)
            .centerCrop()
            .into(binding.ivCoverFrame)
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun checkAndRequestPermission() {
        currentPermission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            else -> Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                currentPermission,
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }

            shouldShowRequestPermissionRationale(currentPermission) -> {
                showPermissionRationaleDialog(currentPermission)
            }

            else -> {
                requestPermissionLauncher.launch(currentPermission)
            }
        }
    }

    private fun showPermissionRationaleDialog(permission: String) {
        buildMaterialDialog(
            titleRes = R.string.permission_rationale_title,
            messageRes = R.string.permission_rationale_message,
            positiveButtonRes = R.string.grant,
            positiveAction = { requestPermissionLauncher.launch(permission) },
        ).show()
    }

    private fun showPermissionSettingsDialog() {
        buildMaterialDialog(
            titleRes = R.string.permission_settings_title,
            messageRes = R.string.permission_settings_message,
            positiveButtonRes = R.string.settings,
            positiveAction = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            },
        ).show()
    }

    private fun buildMaterialDialog(
        context: Context = requireContext(),
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes positiveButtonRes: Int,
        @StringRes negativeButtonRes: Int = R.string.cancel,
        @StyleRes themeResId: Int = R.style.ThemeOverlay_PlaylistMaker_MaterialAlertDialog,
        positiveAction: (() -> Unit),
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context, themeResId)
            .setTitle(getString(titleRes))
            .setMessage(getString(messageRes))
            .setNegativeButton(getString(negativeButtonRes)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(positiveButtonRes)) { _, _ -> positiveAction() }
            .apply {
                setCancelable(false)
            }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddPlaylistFragment()
    }
}
