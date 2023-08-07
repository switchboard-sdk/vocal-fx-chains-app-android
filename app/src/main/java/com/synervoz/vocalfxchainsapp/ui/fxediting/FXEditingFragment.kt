package com.synervoz.vocalfxchainsapp.ui.fxediting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.synervoz.vocalfxchainsapp.Config
import com.synervoz.vocalfxchainsapp.R
import com.synervoz.vocalfxchainsapp.databinding.FragmentFXEditingBinding
import com.synervoz.vocalfxchainsapp.ui.common.BaseFragment
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState
import com.synervoz.vocalfxchainsapp.ui.recording.RecordingSideEffect
import com.synervoz.vocalfxchainsapp.ui.recording.RecordingViewState
import java.io.File

class FXEditingFragment : BaseFragment<FragmentFXEditingBinding, FXEditingViewState, FXEditingViewModel>() {

    companion object {
        const val TAG = "FXEditingFragment"
    }

    override val viewModel: FXEditingViewModel by viewModels { FXEditingViewModel.Factory }

    override var loading: Boolean = false
        set(value) {
            binding.fxLoadingIndicator.root.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setView(FragmentFXEditingBinding.inflate(inflater, container, false))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.exportButton.setOnClickListener {
            viewModel.startExport()
        }

        binding.fxSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.enableFXChain(isChecked)
        }

        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                viewModel.selectFXChain(if (checkedId == R.id.harmonizer_button) 0 else 1)
                viewModel.selectFXChainPreset(if (binding.presetButton.checkedButtonId == R.id.low_button) 0 else 1)
            }
        }

        binding.presetButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                viewModel.selectFXChainPreset(if (checkedId == R.id.low_button) 0 else 1)
            }
        }

        binding.vocalSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.setVocalVolume(value)
        }

        binding.beatSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.setBeatVolume(value)
        }

        viewModel.startAudioSystem(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAudioSystem()
    }

    override fun renderViewState(viewState: ViewState) {
        when (viewState) {
            is FXEditingViewState.Loading -> renderLoadingState(viewState)
            is FXEditingViewState.Ready -> renderReadyState(viewState)
        }
    }

    override fun handleSideEffect(sideEffect: SideEffect) {
        when (sideEffect) {
            is FXEditingSideEffect.StartExport -> startExport()
            is FXEditingSideEffect.FinishExport -> finishExport()
        }
    }

    private fun renderLoadingState(viewState: FXEditingViewState.Loading) {
        loading = true
    }

    private fun renderReadyState(viewState: FXEditingViewState.Ready) {
        loading = false
        binding.startButton.text = viewState.playbackButtonTitle
        binding.fxSwitch.isChecked = viewState.fxSwitchChecked
        binding.toggleButton.check(if (viewState.fxSelectedIdx == 0) R.id.harmonizer_button else R.id.radio_button)
        binding.presetButton.check(if (viewState.presetSelectedIdx == 0) R.id.low_button else R.id.high_button)
        binding.vocalSlider.value = viewState.vocalVolume
        binding.beatSlider.value = viewState.beatVolume
    }

    private fun startExport() {
        loading = true
    }

    private fun finishExport() {
        loading = false

        val context = requireContext()
        val file = File(Config.finalMixRecordingFile)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )

        val intent = ShareCompat.IntentBuilder(context)
            .setType("*/*")
            .setStream(uri)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    }
}
