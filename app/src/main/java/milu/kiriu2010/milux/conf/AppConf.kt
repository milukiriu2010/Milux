package milu.kiriu2010.milux.conf

import android.util.Log
import milu.kiriu2010.milux.entity.Facility
import milu.kiriu2010.milux.entity.FacilityArea

// アプリ設定
class AppConf(
        // 照度値のサンプリング数
        var limit: Int = 101,
        // 施設ビューで表示対象の施設
        // 8:住宅
        var fid: Int = 8,
        // 施設リスト
        val facilityLst: MutableList<Facility> = mutableListOf(),
        // 施設エリアリスト
        val facilityAreaLst: MutableList<FacilityArea> = mutableListOf()
) {
    // デフォルト設定にする
    fun goDefault() {
        val appConfDef = AppConf()
        // 照度値のサンプリング数
        limit = appConfDef.limit
        // 施設ビューで表示対象の施設
        fid = appConfDef.fid
    }

    // 施設リストのテンプレートを構築
    fun createFacLst(): List<Facility> {
        Log.d( javaClass.simpleName, "createFacLst")
        // 施設エリアのリストからfidを抽出する。
        // fidは重複しているため、重複を除く
        val fidLst = facilityAreaLst.map { it -> it.fid }.distinct()

        // 上で取得されたfidのリストから
        // 施設リストを抽出する
        val facLst = facilityLst.filter {
            fidLst.contains(it.fid)
        }

        return facLst
    }

}