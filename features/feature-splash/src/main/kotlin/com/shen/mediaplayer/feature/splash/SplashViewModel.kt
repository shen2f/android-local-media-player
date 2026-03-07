package com.shen.mediaplayer.feature.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.Constants
import com.shen.mediaplayer.core.database.dao.AppConfigDao
import com.shen.mediaplayer.core.database.entity.AppConfigEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appConfigDao: AppConfigDao
) : ViewModel() {
    
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome
    
    fun onboardingFinished() {
        viewModelScope.launch {
            saveFirstLaunchCompleted()
            _navigateToHome.postValue(true)
        }
    }
    
    private suspend fun saveFirstLaunchCompleted() {
        appConfigDao.insert(
            AppConfigEntity(
                key = Constants.PREF_FIRST_LAUNCH,
                value = "false"
            )
        )
    }
}
