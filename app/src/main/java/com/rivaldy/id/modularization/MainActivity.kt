package com.rivaldy.id.modularization

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.rivaldy.id.modularization.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: SplitInstallManager
    private var packageNameModule = ""
    private var dynamicModuleClassName = ""
    private var isModuleAdminInstalled = false
//    private val progressDialog: ProgressDialog by lazy { ProgressDialog(this) }

    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        val name = state.moduleNames().joinToString(" - ")
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                toastLog("DOWNLOADING $name")
                updateLoadingState(state, "Downloading $name")
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                toastLog("REQUIRES_USER_CONFIRMATION")
            }
            SplitInstallSessionStatus.INSTALLED -> {
                toastLog("INSTALLED")
                initListener()
                binding.loadingPB.isVisible = false
//                if (progressDialog.isShowing) {
//                    showAlertDialog("Another feature successfully installed")
//                    progressDialog.dismiss()
//                    progressDialog.progress = 0
//                    progressDialog.max = 100
//                }
            }
            SplitInstallSessionStatus.INSTALLING -> {
                toastLog("INSTALLING $name")
                updateLoadingState(state, "Installing $name")
            }
            SplitInstallSessionStatus.FAILED -> {
                toastLog("FAILED")
                showAlertDialog("Another feature failed installed")
//                progressDialog.dismiss()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = SplitInstallManagerFactory.create(this)
        packageNameModule = packageName
        initListener()
        initView()
    }

    override fun onResume() {
        manager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        manager.unregisterListener(listener)
        super.onPause()
    }

    private fun initListener() {
        manager.installedModules.toList().forEach {
            if (it.equals(MODULE_ADMIN_FEATURE, true)) {
                binding.adminMB.text = getString(R.string.go_to_admin_feature)
                isModuleAdminInstalled = true
            }
        }
        if (!isModuleAdminInstalled) binding.adminMB.text = getString(R.string.go_to_admin_disable)
    }

    private fun initView() {
        binding.normalMB.setOnClickListener {
            startActivity(Intent(this, NormalUserActivity::class.java))
        }
        binding.adminMB.setOnClickListener {
            if (!isModuleAdminInstalled) {
                val builder = AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                    .setTitle(getString(R.string.install_feature))
                    .setMessage(getString(R.string.want_to_install_feature))
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        binding.loadingPB.isVisible = true
                        val request = SplitInstallRequest.newBuilder()
                            .addModule(MODULE_ADMIN_FEATURE)
                            .build()
                        manager.startInstall(request)
                            .addOnCompleteListener {
                                binding.loadingPB.isVisible = false
                                toastLog("Success Installing...")
                            }
                            .addOnSuccessListener {
                                toastLog("Loading...")
                                binding.loadingPB.isVisible = true
//                                progressDialog.isIndeterminate = false
//                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//                                progressDialog.setCancelable(true)
//                                progressDialog.show()
                            }
                            .addOnFailureListener {
                                binding.loadingPB.isVisible = false
                                toastLog("Error Loading...")
                            }
                    }.setNegativeButton(android.R.string.cancel, null)
                builder.create().show()
            } else goToAdminPage()
        }
    }

    private fun goToAdminPage() {
//        if (progressDialog.isShowing) progressDialog.dismiss()
        dynamicModuleClassName = CLASS_ADMIN_FEATURE
        Intent().setClassName(packageNameModule, dynamicModuleClassName).also {
            startActivity(it)
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

    private fun updateLoadingState(state: SplitInstallSessionState, message: String) {
//        progressDialog.max = state.totalBytesToDownload().toInt()
//        progressDialog.progress = state.bytesDownloaded().toInt()
//        progressDialog.setTitle(message)
    }

    private fun toastLog(message: String) {
        Log.e(TAG, message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        initListener()
    }

    companion object {
        const val MODULE_ADMIN_FEATURE = "admin_feature"
        const val CLASS_ADMIN_FEATURE = "com.rivaldy.id.admin_feature.AdminActivity"
    }
}