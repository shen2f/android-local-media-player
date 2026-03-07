package com.shen.mediaplayer.feature.splash

import android.content.Intent
import android.os.Bundle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.shen.mediaplayer.core.ui.base.BaseActivity
import com.shen.mediaplayer.feature.splash.databinding.ActivitySplashBinding
import com.shen.mediaplayer.utils.permission.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    
    private val viewModel: SplashViewModel by viewModels()
    
    override fun inflateBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }
    
    override fun onCreated(savedInstanceState: Bundle?) {
        binding.viewPager.adapter = OnBoardingAdapter(this)
        binding.indicator.attachTo(binding.viewPager)
        
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < (binding.viewPager.adapter?.count ?: 1) - 1) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
        
        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
        
        viewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToHome()
            }
        }
    }
    
    @OptIn(ExperimentalPermissionsApi::class)
    private fun finishOnboarding() {
        viewModel.onboardingFinished()
    }
    
    private fun navigateToHome() {
        // TODO: Navigate to main home activity
        finish()
    }
}
