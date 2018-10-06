package milu.kiriu2010.milux.conf

import milu.kiriu2010.milux.entity.Facility
import milu.kiriu2010.milux.entity.FacilityArea

// アプリ設定
class AppConf(
        // 照度値をためるバッファのサイズ
        var limit: Int = 101,
        // 施設リスト
        val facilityLst: MutableList<Facility> = mutableListOf(),
        // 施設エリアリスト
        val facilityAreaLst: MutableList<FacilityArea> = mutableListOf()
) {

}