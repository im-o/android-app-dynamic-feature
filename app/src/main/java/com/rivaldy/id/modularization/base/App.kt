package com.rivaldy.id.modularization.base

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

/**
 * Created by rivaldy on 05/02/22.
 * Find me on my Github -> https://github.com/im-o
 */

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }
}