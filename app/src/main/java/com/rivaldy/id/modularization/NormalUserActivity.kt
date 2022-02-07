package com.rivaldy.id.modularization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rivaldy.id.modularization.databinding.ActivityNormalUserBinding

class NormalUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}