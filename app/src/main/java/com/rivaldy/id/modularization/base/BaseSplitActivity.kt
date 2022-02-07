package com.rivaldy.id.modularization.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat

/**
 * Created by rivaldy on 05/02/22.
 * Find me on my Github -> https://github.com/im-o
 */

abstract class BaseSplitActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }
}