package com.shen.mediaplayer.feature.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shen.mediaplayer.core.ui.base.BaseFragment
import com.shen.mediaplayer.feature.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm = getViewModel<SettingsViewModel>()

        initBiometrics()
        setupPlaybackSettings(vm)
        setupThemeSettings(vm)
        setupPrivacySettings(vm)
        setupAboutSettings()
        setupObservers(vm)
    }

    private fun initBiometrics() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // 验证成功，处理后续逻辑
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.private_folder_protection))
            .setSubtitle(getString(R.string.enter_password))
            .setNegativeButtonText(getString(android.R.string.cancel))
            .build()
    }

    private fun setupPlaybackSettings(vm: SettingsViewModel) {
        binding.sliderPlaybackSpeed.addOnChangeListener { _, value, _ ->
            binding.tvPlaybackSpeedValue.text = String.format("%.1fx", value)
            vm.updatePlaybackSpeed(value)
        }

        binding.switchEnableAutoPlay.setOnCheckedChangeListener { _, checked ->
            vm.updateAutoPlayNext(checked)
        }

        binding.switchResumeOnStartup.setOnCheckedChangeListener { _, checked ->
            vm.updateResumeOnStartup(checked)
        }

        binding.containerSleepTimer.setOnClickListener {
            showSleepTimerDialog(vm)
        }

        binding.btnSetSleepTimer.setOnClickListener {
            showSleepTimerDialog(vm)
        }
    }

    private fun showSleepTimerDialog(vm: SettingsViewModel) {
        val currentMinutes = vm.sleepTimerMinutes.value
        val options = arrayOf("15分钟", "30分钟", "45分钟", "60分钟", "90分钟", "关闭")
        val values = intArrayOf(15, 30, 45, 60, 90, 0)
        val selectedIndex = values.indexOf(currentMinutes).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_sleep_timer)
            .setSingleChoiceItems(options, selectedIndex) { dialog, which ->
                val minutes = values[which]
                vm.updateSleepTimer(minutes)
                dialog.dismiss()

                if (minutes > 0) {
                    startSleepTimer(minutes)
                } else {
                    cancelSleepTimer()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun startSleepTimer(minutes: Int) {
        // 这里应该通知播放模块启动定时关闭
        // 可以通过共享Preferences或广播通知
        val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putLong("sleep_timer_end_time", System.currentTimeMillis() + minutes * 60 * 1000).apply()
    }

    private fun cancelSleepTimer() {
        val sharedPref = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().remove("sleep_timer_end_time").apply()
    }

    private fun setupThemeSettings(vm: SettingsViewModel) {
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radioThemeSystem -> SettingsViewModel.ThemeMode.System
                R.id.radioThemeLight -> SettingsViewModel.ThemeMode.Light
                R.id.radioThemeDark -> SettingsViewModel.ThemeMode.Dark
                else -> SettingsViewModel.ThemeMode.System
            }
            vm.updateThemeMode(mode)
            // 重启Activity应用主题
            requireActivity().recreate()
        }

        binding.switchDynamicColors.setOnCheckedChangeListener { _, checked ->
            vm.updateDynamicColorsEnabled(checked)
            requireActivity().recreate()
        }
    }

    private fun setupPrivacySettings(vm: SettingsViewModel) {
        val biometricAvailable = BiometricManager.from(requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

        binding.switchEnableBiometrics.isEnabled = vm.privateFolderPasswordEnabled.value && biometricAvailable

        binding.containerPrivateFolderPassword.setOnClickListener {
            if (vm.privateFolderPasswordEnabled.value) {
                showPasswordActionDialog(vm)
            } else {
                showSetPasswordDialog(vm)
            }
        }

        binding.btnSetupPrivateFolder.setOnClickListener {
            if (vm.privateFolderPasswordEnabled.value) {
                showPasswordActionDialog(vm)
            } else {
                showSetPasswordDialog(vm)
            }
        }

        binding.switchEnableBiometrics.setOnCheckedChangeListener { _, checked ->
            vm.updateBiometricsEnabled(checked)
        }

        binding.switchEnableOnlineFeatures.setOnCheckedChangeListener { _, checked ->
            vm.updateOnlineFeaturesEnabled(checked)
        }
    }

    private fun showSetPasswordDialog(vm: SettingsViewModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.set_password)
            .setView(dialogView)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .show()
            .also { dialog ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val password = etPassword.text.toString()
                    val confirm = etConfirmPassword.text.toString()
                    if (password.isEmpty()) {
                        etPassword.error = getString(R.string.password_required)
                        return@setOnClickListener
                    }
                    if (password != confirm) {
                        etConfirmPassword.error = getString(R.string.password_not_match)
                        return@setOnClickListener
                    }
                    vm.updatePrivateFolderPassword(password)
                    dialog.dismiss()
                }
            }
    }

    private fun showPasswordActionDialog(vm: SettingsViewModel) {
        val options = arrayOf(getString(R.string.clear_password), getString(R.string.use_biometrics))
        MaterialAlertDialogBuilder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // 清除密码需要先验证
                        showVerifyPasswordDialog(vm) { success ->
                            if (success) {
                                vm.updatePrivateFolderPassword(null)
                                vm.updateBiometricsEnabled(false)
                            }
                        }
                    }
                }
            }
            .show()
    }

    private fun showVerifyPasswordDialog(vm: SettingsViewModel, onResult: (Boolean) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_verify_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.enter_password)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                val input = etPassword.text.toString()
                val verified = vm.verifyPassword(input)
                onResult(verified)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupAboutSettings() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            binding.tvVersion.text = packageInfo.versionName
        } catch (_: Exception) {
        }

        binding.containerCheckUpdate.setOnClickListener {
            // 占位，在线功能开启后实现
        }

        binding.btnCheckUpdate.setOnClickListener {
            // 占位，在线功能开启后实现
        }
    }

    private fun setupObservers(vm: SettingsViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.playbackSpeed.collect { speed ->
                        binding.sliderPlaybackSpeed.value = speed
                        binding.tvPlaybackSpeedValue.text = String.format("%.1fx", speed)
                    }
                }
                launch {
                    vm.autoPlayNext.collect { enabled ->
                        binding.switchEnableAutoPlay.isChecked = enabled
                    }
                }
                launch {
                    vm.resumeOnStartup.collect { enabled ->
                        binding.switchResumeOnStartup.isChecked = enabled
                    }
                }
                launch {
                    vm.sleepTimerMinutes.collect { minutes ->
                        if (minutes == 0) {
                            binding.tvSleepTimerStatus.text = getString(R.string.off)
                        } else {
                            binding.tvSleepTimerStatus.text = getString(R.string.timer_running).format(minutes)
                        }
                    }
                }
                launch {
                    vm.themeMode.collect { mode ->
                        when (mode) {
                            SettingsViewModel.ThemeMode.System -> binding.radioThemeSystem.isChecked = true
                            SettingsViewModel.ThemeMode.Light -> binding.radioThemeLight.isChecked = true
                            SettingsViewModel.ThemeMode.Dark -> binding.radioThemeDark.isChecked = true
                        }
                    }
                }
                launch {
                    vm.dynamicColorsEnabled.collect { enabled ->
                        binding.switchDynamicColors.isChecked = enabled
                    }
                }
                launch {
                    vm.privateFolderPasswordEnabled.collect { enabled ->
                        binding.tvPrivateFolderStatus.text = if (enabled) getString(R.string.enabled) else getString(R.string.disabled)
                        binding.btnSetupPrivateFolder.text = if (enabled) getString(R.string.clear_password) else getString(R.string.setup)
                        binding.switchEnableBiometrics.isEnabled = enabled
                    }
                }
                launch {
                    vm.biometricsEnabled.collect { enabled ->
                        binding.switchEnableBiometrics.isChecked = enabled
                    }
                }
                launch {
                    vm.onlineFeaturesEnabled.collect { enabled ->
                        binding.switchEnableOnlineFeatures.isChecked = enabled
                    }
                }
            }
        }
    }
}
