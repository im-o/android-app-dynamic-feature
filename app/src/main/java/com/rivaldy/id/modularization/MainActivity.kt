package com.rivaldy.id.modularization

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.rivaldy.id.modularization.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: SplitInstallManager
    private var packageNameModule = ""
    private var dynamicModuleClassName = ""
    private var isModuleAdminInstalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSplitInstallManager()
        initView()
    }

    private fun initView() {
        binding.normalMB.setOnClickListener {
            startActivity(Intent(this, NormalUserActivity::class.java))
        }
        binding.adminMB.setOnClickListener {
            openDialogInstallFeature()
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun toastLog(message: String) {
        Log.e(TAG, message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun goToDynamicFeature() {
        dynamicModuleClassName = CLASS_ADMIN_FEATURE
        Intent().setClassName(packageNameModule, dynamicModuleClassName).also {
            startActivity(it)
        }
    }

    private fun initSplitInstallManager() {
        packageNameModule = packageName
        manager = SplitInstallManagerFactory.create(this)
        manager.installedModules.toList().forEach {
            if (it.equals(MODULE_ADMIN_FEATURE, true)) {
                binding.adminMB.text = getString(R.string.go_to_admin_feature)
                isModuleAdminInstalled = true
            }
        }
        if (!isModuleAdminInstalled) binding.adminMB.text = getString(R.string.go_to_admin_disable)
    }

    private fun splitInstallStateUpdatedListener() = SplitInstallStateUpdatedListener { state ->
        val name = state.moduleNames().joinToString(" - ")
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                toastLog("DOWNLOADING $name")
            }

            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                toastLog("REQUIRES_USER_CONFIRMATION")
            }

            SplitInstallSessionStatus.INSTALLED -> {
                toastLog("INSTALLED")
                initSplitInstallManager()
                isLoadData(false)
            }

            SplitInstallSessionStatus.INSTALLING -> {
                toastLog("INSTALLING $name")
            }

            SplitInstallSessionStatus.FAILED -> {
                toastLog("FAILED")
                showAlertDialog("Another feature failed installed")
            }

            SplitInstallSessionStatus.CANCELING -> {
                toastLog("CANCELING")
            }

            SplitInstallSessionStatus.CANCELED -> {
                toastLog("CANCELED")
            }

            SplitInstallSessionStatus.DOWNLOADED -> {
                toastLog("DOWNLOADED")
            }

            SplitInstallSessionStatus.PENDING -> {
                toastLog("PENDING")
            }

            SplitInstallSessionStatus.UNKNOWN -> {
                toastLog("UNKNOWN")
            }
        }
    }

    private fun openDialogInstallFeature() {
        if (!isModuleAdminInstalled) {
            val builder = AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                .setTitle(getString(R.string.install_feature))
                .setMessage(getString(R.string.want_to_install_feature))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    isLoadData(true)
                    val request = SplitInstallRequest.newBuilder()
                        .addModule(MODULE_ADMIN_FEATURE)
                        .build()
                    manager.startInstall(request)
                        .addOnCompleteListener {
                            isLoadData(false)
                            toastLog("Success, try to open feature again.")
                        }
                        .addOnSuccessListener {
                            isLoadData(true)
                            toastLog("Loading...")
                        }
                        .addOnFailureListener {
                            isLoadData(false)
                            toastLog("Error Installing new feature...")
                        }
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        } else goToDynamicFeature()
    }

    private fun isLoadData(isLoading: Boolean){
        binding.loadingPB.isVisible = isLoading
    }

    override fun onResume() {
        manager.registerListener(splitInstallStateUpdatedListener())
        super.onResume()
    }

    override fun onPause() {
        manager.unregisterListener(splitInstallStateUpdatedListener())
        super.onPause()
    }

    companion object {
        const val MODULE_ADMIN_FEATURE = "admin_feature"
        const val CLASS_ADMIN_FEATURE = "com.rivaldy.id.admin_feature.AdminActivity"
    }
}