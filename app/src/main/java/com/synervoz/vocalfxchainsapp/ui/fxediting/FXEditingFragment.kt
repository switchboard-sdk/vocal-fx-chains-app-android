package com.synervoz.vocalfxchainsapp.ui.fxediting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.synervoz.vocalfxchainsapp.databinding.FragmentFXEditingBinding
import com.synervoz.vocalfxchainsapp.ui.common.BaseFragment
import com.synervoz.vocalfxchainsapp.ui.common.SideEffect
import com.synervoz.vocalfxchainsapp.ui.common.ViewState

class FXEditingFragment : BaseFragment<FragmentFXEditingBinding, FXEditingViewState, FXEditingViewModel>() {

    companion object {
        const val TAG = "FXEditingFragment"
    }

    override val viewModel: FXEditingViewModel by viewModels { FXEditingViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setView(FragmentFXEditingBinding.inflate(inflater, container, false))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.init()
    }

    override fun renderViewState(viewState: ViewState) {
        when (viewState) {
            is FXEditingViewState.Loading -> renderLoadingState(viewState)
        }
    }

    override fun handleSideEffect(sideEffect: SideEffect) {

    }

    private fun renderLoadingState(viewState: FXEditingViewState.Loading) {
        loading = true
    }
}
