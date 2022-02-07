package com.rivaldy.id.admin_feature

import android.os.Bundle
import com.rivaldy.id.admin_feature.databinding.ActivityAdminBinding
import com.rivaldy.id.modularization.base.BaseSplitActivity

class AdminActivity : BaseSplitActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}