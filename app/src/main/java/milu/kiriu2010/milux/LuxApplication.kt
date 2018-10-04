package milu.kiriu2010.milux

import android.app.Application
import android.util.Log
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.Facility
import org.json.JSONObject
import java.util.*

class LuxApplication: Application() {
    // アプリ設定
    val appConf = AppConf()

    // ロケール
    // ja
    val locale = Locale.getDefault()

    // -------------------------------------
    // アプリケーションの起動時に呼び出される
    // -------------------------------------
    override fun onCreate() {
        super.onCreate()

       //Log.d(javaClass.simpleName, "locale:{${locale.language}}")

        // 施設リストをロードする
        loadJSONFacility()
    }

    // 施設リストをロードする
    private fun loadJSONFacility() {
        // JSONファイルをロードする
        val sb = StringBuffer()
        val istream = resources.openRawResource(R.raw.facility)
        val reader = istream.bufferedReader()
        val iterator = reader.lineSequence().iterator()
        while ( iterator.hasNext() ) {
            sb.append( iterator.next() )
        }
        reader.close()
        istream.close()

        // JSONの内容を解析し施設リストを作成する
        val objJSON = JSONObject(sb.toString())
        // 現在の言語設定でJSONファイルを解析
        var facilities = objJSON.getJSONArray(locale.language)
        // 現在の言語設定でのデータがなければ英語を採用する
        if ( facilities.length() == 0 ) {
            facilities = objJSON.getJSONArray("en")
        }

        // 施設リストに解析したデータを追加する
        for ( i in 0 until facilities.length() ) {
            val facilityJSON = facilities.getJSONObject(i)
            appConf.facilityLst.add( Facility( facilityJSON.getInt("fid"), facilityJSON.getString("fname") ) )
        }

        // なぜか出力されない
        Log.d(javaClass.simpleName, "facilityLst:[${appConf.facilityLst.size}]")
    }
}