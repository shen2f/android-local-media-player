package com.shen.mediaplayer.feature.splash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.shen.mediaplayer.feature.splash.databinding.ItemOnboardingBinding

data class OnBoardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

class OnBoardingAdapter(
    private val activity: SplashActivity
) : PagerAdapter() {
    
    private val pages = listOf(
        OnBoardingPage(
            title = "轻量 无广告",
            description = "专注本地媒体播放，无任何广告推送，隐私完全可控",
            imageRes = R.drawable.ic_onboarding_1
        ),
        OnBoardingPage(
            title = "权限申请",
            description = "需要媒体访问权限来读取您手机上的视频、音频和图片文件，仅用于本地播放，不会上传任何数据",
            imageRes = R.drawable.ic_onboarding_2
        ),
        OnBoardingPage(
            title = "开始使用",
            description = "点击完成开始浏览您的媒体库",
            imageRes = R.drawable.ic_onboarding_3
        )
    )
    
    override fun getCount(): Int = pages.size
    
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        )
        val page = pages[position]
        binding.tvTitle.setText(page.title)
        binding.tvDescription.setText(page.description)
        binding.ivImage.setImageResource(page.imageRes)
        container.addView(binding.root)
        return binding.root
    }
    
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
