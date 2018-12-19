package milu.kiriu2010.milux.gui

import milu.kiriu2010.milux.entity.LuxData

interface NewVal02Listener {
    // 新しい"照度値"
    // "端末の姿勢値"
    // "方位"
    // を渡す
    fun onUpdate( luxData: LuxData, attitude: FloatArray, orientation: FloatArray )
}