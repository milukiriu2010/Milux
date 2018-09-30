package milu.kiriu2010.milux.gui

interface OrientationListener {
    // アクティビティの方向を返す
    // ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    // ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    fun onActivityOrientation(): Int
}