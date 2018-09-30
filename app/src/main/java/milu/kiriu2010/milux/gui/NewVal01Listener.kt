package milu.kiriu2010.milux.gui

import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.util.LimitedArrayList

interface NewVal01Listener {
    // 新しい照度の値を渡す
    fun onUpdate( lux: Float )
    // 照度値の配列を渡す
    fun onUpdate( luxLst: LimitedArrayList<LuxData> )
}
