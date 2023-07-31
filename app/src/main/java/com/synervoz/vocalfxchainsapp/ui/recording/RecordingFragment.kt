package com.synervoz.vocalfxchainsapp.ui.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.synervoz.vocalfxchainsapp.databinding.FragmentRecordingBinding
import com.synervoz.vocalfxchainsapp.ui.common.BaseFragment
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState

class RecordingFragment : BaseFragment<FragmentRecordingBinding, RecordingViewState, RecordingViewModel>() {

    companion object {
        const val TAG = "RecordingFragment"
    }

    override val viewModel: RecordingViewModel by viewModels { RecordingViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setView(FragmentRecordingBinding.inflate(inflater, container, false))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.init()
    }

    override fun renderViewState(viewState: ViewState) {
        when (viewState) {
            is RecordingViewState.Loading -> renderLoadingState(viewState)
        }
    }

    override fun handleSideEffect(sideEffect: SideEffect) {

    }

    private fun renderLoadingState(viewState: RecordingViewState.Loading) {
        loading = true
    }
}