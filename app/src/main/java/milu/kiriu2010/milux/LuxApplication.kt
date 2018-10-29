package milu.kiriu2010.milux

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.Facility
import milu.kiriu2010.milux.entity.FacilityArea
import milu.kiriu2010.util.MyTool
import org.json.JSONObject
import java.util.*

class LuxApplication: Application() {
    // アプリ設定
    val appConf = AppConf()

    // ロケール
    // ja
    lateinit var locale: Locale

    private enum class SpKey(val id: String) {
        NAME_APP_CONF("appConf"),
        KEY_LIMIT("limit"),
        KEY_FID("fid")
    }

    // -------------------------------------
    // アプリケーションの起動時に呼び出される
    // -------------------------------------
    override fun onCreate() {
        super.onCreate()

       //Log.d(javaClass.simpleName, "locale:{${locale.language}}")

        // アプリ設定をロードする
        loadAppConf()
    }

    // アプリ設定をロードする
    fun loadAppConf() {
        // ロケールを取得
        locale = Locale.getDefault()

        // 共有設定をロードする
        loadSharedPreferences()

        // 施設リストをロードする
        loadJSONFacility()

        // 施設エリアリストをロードする
        loadJSONFacilityArea()
    }

    // 共有設定へアプリ設定を保存する
    fun saveSharedPreferences() {
        // 共有設定を取得
        val sp = getSharedPreferences(SpKey.NAME_APP_CONF.id, Context.MODE_PRIVATE) as SharedPreferences

        // 共有設定にアプリの設定を保存する
        sp.edit()
                // 共有設定へ"照度値のサンプリング数"を保存
                .putInt(SpKey.KEY_LIMIT.id,appConf.limit)
                // 共有設定へ"施設ビューで表示対象の施設"を保存
                .putInt(SpKey.KEY_FID.id,appConf.fid)
                .commit()
    }

    // 共有設定からアプリ設定をロードする
    private fun loadSharedPreferences() {
        // 共有設定がない場合のデフォルト設定
        val appConfDef = AppConf()

        // 共有設定を取得
        val sp = getSharedPreferences(SpKey.NAME_APP_CONF.id, Context.MODE_PRIVATE) as SharedPreferences
        // 共有設定から"照度値のサンプリング数"を取得
        appConf.limit = sp.getInt(SpKey.KEY_LIMIT.id,appConfDef.limit)
        // 共有設定から"施設ビューで表示対象の施設"を取得
        appConf.fid = sp.getInt(SpKey.KEY_FID.id,appConfDef.fid)
    }

    // 施設リストをロードする
    private fun loadJSONFacility() {
        // JSONファイルをロードする
        val str = MyTool.loadRawFile(resources,R.raw.facility)

        // JSONの内容を解析し施設リストを作成する
        val objJSON = JSONObject(str)
        // 現在の言語設定でJSONファイルを解析
        var facilities = objJSON.getJSONArray(locale.language)
        // 現在の言語設定でのデータがなければ英語を採用する
        if ( facilities.length() == 0 ) {
            facilities = objJSON.getJSONArray("en")
        }

        // 施設リストに解析したデータを追加する
        appConf.facilityLst.clear()
        for ( i in 0 until facilities.length() ) {
            val facilityJSON = facilities.getJSONObject(i)
            appConf.facilityLst.add(
                    Facility(
                            facilityJSON.getInt("fid"),
                            facilityJSON.getString("fname")
                    )
            )
        }

        // なぜか出力されない
        Log.d(javaClass.simpleName, "facilityLst:[${appConf.facilityLst.size}]")
    }


    // 施設エリアリストをロードする
    private fun loadJSONFacilityArea() {
        // JSONファイルをロードする
        val str = MyTool.loadRawFile(resources,R.raw.facility_area)

        // JSONの内容を解析し施設リストを作成する
        val objJSON = JSONObject(str)
        // 現在の言語設定でJSONファイルを解析
        var facilitieAreas = objJSON.getJSONArray(locale.language)
        // 現在の言語設定でのデータがなければ英語を採用する
        if ( facilitieAreas.length() == 0 ) {
            facilitieAreas = objJSON.getJSONArray("en")
        }

        // 施設エリアリストに解析したデータを追加する
        appConf.facilityAreaLst.clear()
        for ( i in 0 until facilitieAreas.length() ) {
            val facilityAreaJSON = facilitieAreas.getJSONObject(i)
            val jsonArrayAnameLst = facilityAreaJSON.getJSONArray("anameLst")
            val anameLst = mutableListOf<String>()
            for ( j in 0 until jsonArrayAnameLst.length() ) {
                anameLst.add( jsonArrayAnameLst.getString(j) )
            }

            appConf.facilityAreaLst.add(
                    FacilityArea(
                            facilityAreaJSON.getInt("fid"),
                            anameLst,
                            facilityAreaJSON.getInt("lux")
                    )
            )
        }
    }
}