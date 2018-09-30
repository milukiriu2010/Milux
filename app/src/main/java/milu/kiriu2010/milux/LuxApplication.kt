package milu.kiriu2010.milux

import android.app.Application
import milu.kiriu2010.milux.conf.AppConf

class LuxApplication: Application() {
    // アプリ設定
    val appConf = AppConf()

    // -------------------------------------
    // アプリケーションの起動時に呼び出される
    // -------------------------------------
    override fun onCreate() {
        super.onCreate()
    }
}