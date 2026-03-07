package com.shen.mediaplayer.core.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    
    abstract fun inflateBinding(): VB
    
    abstract fun onCreated(savedInstanceState: Bundle?)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)
        onCreated(savedInstanceState)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
