package milu.kiriu2010.milux.entity

import java.util.*

// 照度データ
data class LuxData(
        // 時刻
        val t: Date = Date(),
        // 照度
        val lux: Float = 0f
) {
}
