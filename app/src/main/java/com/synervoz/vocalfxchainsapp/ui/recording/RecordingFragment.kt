package com.synervoz.vocalfxchainsapp.ui.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.fragment.app.replace
import com.synervoz.vocalfxchainsapp.MainActivity
import com.synervoz.vocalfxchainsapp.R
import com.synervoz.vocalfxchainsapp.databinding.FragmentRecordingBinding
import com.synervoz.vocalfxchainsapp.ui.common.BaseFragment
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState
import com.synervoz.vocalfxchainsapp.ui.fxediting.FXEditingFragment

class RecordingFragment : BaseFragment<FragmentRecordingBinding, RecordingViewState, RecordingViewModel>() {

    companion object {
        const val TAG = "RecordingFragment"
    }

    override val viewModel: RecordingViewModel by viewModels { RecordingViewModel.createFactory(requireContext()) }

    override var loading: Boolean = false
        set(value) {
            binding.recordingLoadingIndicator.root.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setView(FragmentRecordingBinding.inflate(inflater, container, false))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            viewModel.toggleRecording()
        }

        binding.listenButton.setOnClickListener {
            viewModel.toggleBeat()
        }

        binding.fxSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.enableFXChain(isChecked)
        }

        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                viewModel.selectFXChain(if (checkedId == R.id.harmonizer_button) 0 else 1)
            }
        }

        viewModel.enableLiveMonitoring((requireActivity() as MainActivity).isHeadset)
        viewModel.startAudioSystem(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAudioSystem()
    }

    override fun renderViewState(viewState: ViewState) {
        when (viewState) {
            is RecordingViewState.Loading -> renderLoadingState(viewState)
            is RecordingViewState.Ready -> renderReadyState(viewState)
        }
    }

    override fun handleSideEffect(sideEffect: SideEffect) {
        when (sideEffect) {
            is RecordingSideEffect.Finish -> navigateToFXEditing()
        }
    }

    private fun renderLoadingState(viewState: RecordingViewState.Loading) {
        loading = true
    }

    private fun renderReadyState(viewState: RecordingViewState.Ready) {
        loading = false
        binding.listenButton.isEnabled = viewState.beatButtonEnabled
        binding.listenButton.text = viewState.beatButtonTitle
        binding.startButton.isEnabled = viewState.recordingButtonEnabled
        binding.startButton.text = viewState.recordingButtonTitle
        binding.fxSwitch.isChecked = viewState.fxSwitchChecked
        binding.toggleButton.check(if (viewState.fxSelectedIdx == 0) R.id.harmonizer_button else R.id.radio_button)
    }

    private fun navigateToFXEditing() {
        requireActivity().supportFragmentManager.commit {
            replace<FXEditingFragment>(R.id.container)
            setReorderingAllowed(true)
            addToBackStack(FXEditingFragment.TAG)
        }
    }
}
