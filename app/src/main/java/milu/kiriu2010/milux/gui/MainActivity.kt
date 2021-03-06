package milu.kiriu2010.milux.gui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_main.*
import milu.kiriu2010.milux.LuxApplication
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.misc.AboutFragment
import milu.kiriu2010.milux.gui.misc.ConfFragment
import milu.kiriu2010.milux.id.FragmentID
import milu.kiriu2010.util.LimitedArrayList
import java.util.Date

// DrawLayoutにすると横向きにしたとき表示されない
// RecyclerViewをつけると"02スクリーン"の上に"01スクリーン"がなぜか表示される
class MainActivity : AppCompatActivity()
        , SensorEventListener
        , ResetListener
        , ConfFragment.OnUpdateConfListener {

    // アプリ設定
    private lateinit var appConf: AppConf

    // スワイプする表示されるページを格納したアダプタ
    private var luxPagerAdapter: LuxPagerAdapter? = null

    // 現在表示対象としているページ番号
    private var currentPagePos = 0

    // 照度センサの値
    private var luxData = LuxData()

    // 時刻ごとの照度値リスト
    private lateinit var luxLst: LimitedArrayList<LuxData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // アプリ設定
        val appl = application as LuxApplication
        appConf = appl.appConf

        // スクリーン制御
        appConf.screenControl(this)

        // 時刻ごとの照度値リスト
        luxLst = LimitedArrayList(appConf.limit, appConf.limit)
        //luxLst.limit = appConf.limit

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        luxPagerAdapter = LuxPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        // containerは、androidx.viewpager.widget.ViewPagerのID
        container.adapter = luxPagerAdapter

        // スクリーンを常にON
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // アクティブなフラグメントが切り替わったら呼び出される
        container.addOnPageChangeListener( object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // スクロールが完了したら
                // ページが選択されたこと・選択から外れたことを通知する
                if ( state == ViewPager.SCROLL_STATE_IDLE ) {
                    selectOnOff()
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(pos: Int) {
                Log.d(javaClass.simpleName, "onPageSelected[{$pos}]")

                // 現在表示中のページ番号を取得
                currentPagePos = pos

                // ページが選択されたこと・選択から外れたことを通知する
                //selectOnOff()
                /*
                for ( i in 0..luxPagerAdapter!!.count!! ) {
                    val fragment = luxPagerAdapter?.getItem(i) as? SelectedListener ?: continue

                    if ( i == pos ) {
                        fragment.onSelected(true)
                    }
                    else {
                        fragment.onSelected(false)
                    }
                }
                */

                /*
                val fragment = luxPagerAdapter?.getItem(pos) ?: return
                // 画面の方向を表示する内容によって変更する
                if ( fragment is OrientationListener ) {
                    requestedOrientation = fragment.onActivityOrientation()
                }
                */
            }

        })


        // 1秒ごとに照度値をバッファに蓄える
        // なんかうまく動かない
        /*
        timer( period = 1000 ) {
            handler.post {
                Log.d(javaClass.simpleName,"luxLst.size[${luxLst.size}]")
                luxLst.add(0,LuxData(Date(),lux))
            }
        }
        */
    }

    // センサーの監視を開始する
    override fun onResume() {
        super.onResume()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 照度センサ
        val sensorLight: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        // 照度センサあり
        if ( sensorLight != null ) {
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL)
        }
        // 照度センサなし
        else {
            // アラート画面
            supportFragmentManager.beginTransaction()
                    .add(R.id.frameErrMsg, NoSensorFragment.newInstance())
                    .commit()
            container.visibility = View.GONE
        }
    }

    // センサーの監視を終了する
    override fun onPause() {
        super.onPause()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    // 言語が変わったとき呼ばれるかどうか
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d( javaClass.simpleName, "onRestoreInstanceState")
        // アプリ設定をロードする
        val appl = application as LuxApplication
        appl.loadAppConf()
    }

    // 言語が変わったとき呼ばれるかどうか
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d( javaClass.simpleName, "onSaveInstanceState")
    }

    // SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    // SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        if ( event?.sensor?.type != Sensor.TYPE_LIGHT) return
        // 照度センサの値を取得
        val lux = event.values[0]
        // 計測時刻
        val now = Date()

        // １秒ごとに照度値をバッファに格納
        var tick = false
        if ( now.time/1000 != luxData.t.time/1000 ) {
            tick = true
            luxData = LuxData(now,lux)
            luxLst.add(0, luxData)
            //Log.d(javaClass.simpleName,"luxLst.size[${luxLst.size}]")
        }

        // 登録されている表示ビュー全てに新しい値を伝える
        for ( i in 0 until luxPagerAdapter!!.count ) {
            val fragment = luxPagerAdapter?.getItem(i) as? NewVal01Listener
                    ?: continue

            /* 現在選択しているページだけ更新
            if ( i == currentPagePos ) {
                fragment.onUpdate(lux)
            }
            */
            // 新しい値をフラグメントに渡す
            fragment.onUpdate(lux)
            // 照度リストは1秒ごとに新しいリストをフラグメントに渡す
            if ( tick == true ) {
                fragment.onUpdate(luxLst)
            }
        }

    }

    // ResetListener
    // メンバ変数を初期化する
    override fun OnReset() {
        //Log.d( javaClass.simpleName, "OnReset")
        // 照度センサの値
        luxData = LuxData()

        // 時刻ごとの照度値リスト
        // 1分間データを保持
        luxLst.clear()

        // 登録されている表示ビュー全てをリセット
        for ( i in 0 until luxPagerAdapter!!.count ) {
            val fragment = luxPagerAdapter?.getItem(i) as? ResetListener
                    ?: continue

            fragment.OnReset()
        }
    }

    // ConfFragment.OnUpdateConfListener
    // 設定が更新されると呼び出される
    override fun updateConf() {
        val appl = application as? LuxApplication ?: return
        val appConf = appl.appConf

        // 照度値リストのリミットを更新
        luxLst.limit = appConf.limit

        // 登録されている表示ビュー全てをリセット
        for ( i in 0 until luxPagerAdapter!!.count ) {
            val fragment = luxPagerAdapter?.getItem(i) as? ConfFragment.OnUpdateConfListener
                    ?: continue

            fragment.updateConf()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            // リセットボタン
            R.id.action_reset -> OnReset()
            // "設定"フラグメントを表示
            R.id.action_settings -> {
                val dialog = ConfFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentID.ID_SETTINGS.id)
            }
            // "About Me"フラグメントを表示
            R.id.action_about -> {
                val dialog = AboutFragment.newInstance()
                dialog.show(supportFragmentManager, FragmentID.ID_ABOUT.id)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // ページが選択されたこと・選択から外れたことを通知する
    private fun selectOnOff() {
        for ( i in 0 until luxPagerAdapter!!.count ) {
            val fragment = luxPagerAdapter?.getItem(i) as? SelectedListener ?: continue

            Log.d(javaClass.simpleName, "selectOnOff[{$i}{$currentPagePos}]")

            if ( i == currentPagePos ) {
                fragment.onSelected(true)
            }
            else {
                fragment.onSelected(false)
            }
        }
    }
}
