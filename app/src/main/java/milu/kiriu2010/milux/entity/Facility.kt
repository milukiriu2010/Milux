package milu.kiriu2010.milux.entity

// 施設
// ------------------------------------
// 1:事務所
data class Facility(
        // 施設ID
        val fid: Int,
        // 施設名
        val fname: String
)

// 施設内のエリア
// ------------------------------------
// 事務所
//   事務室
//   役員室
data class FacilityArea(
    // 施設ID
    val fid: Int,
    // エリア名
    val anameLst: MutableList<String>,
    // エリアの適正照度
    val lux: Int
)