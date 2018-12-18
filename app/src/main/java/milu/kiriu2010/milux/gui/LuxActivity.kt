package milu.kiriu2010.milux.gui

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import milu.kiriu2010.milux.LuxApplication
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.misc.AboutFragment
import milu.kiriu2010.milux.gui.misc.ConfFragment
import milu.kiriu2010.milux.gui.misc.MenuFragment
import milu.kiriu2010.milux.id.FragmentID
import milu.kiriu2010.util.LimitedArrayList
import java.util.*

// DrawLayoutにすると横向きにしたとき表示されない
// RecyclerViewをつけると"02スクリーン"の上に"01スクリーン"がなぜか表示される
class LuxActivity : AppCompatActivity()
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

    // 回転行列
    private val MATRIX_SIZE = 16
    // ------------------------------------------------------------------------------------------
    // R is the identity matrix when the device is aligned with the world's coordinate system,
    // that is, when the device's X axis points toward East,
    // the Y axis points to the North Pole and
    // the device is facing the sky.
    // ------------------------------------------------------------------------------------------
    private val inR = FloatArray(MATRIX_SIZE)
    private val outR = FloatArray(MATRIX_SIZE)
    // ------------------------------------------------------------------------------------------
    // I is a rotation matrix transforming the geomagnetic vector into the same coordinate space
    // as gravity (the world's coordinate space).
    // I is a simple rotation around the X axis.
    // The inclination angle in radians can be computed with getInclination(float[]).
    // ------------------------------------------------------------------------------------------
    private val ix = FloatArray(MATRIX_SIZE)

    // センサー値
    private val AXIS_NUM = 3
    // 加速度センサの値
    private var accel = FloatArray(AXIS_NUM)
    // 磁気センサの値
    private var magnetic = FloatArray(AXIS_NUM)
    // 照度センサの値
    private var light = FloatArray(1)
    // 端末の姿勢
    private var attitude = FloatArray(AXIS_NUM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lux)

        // アプリ設定
        val appl = application as LuxApplication
        appConf = appl.appConf

        // 時刻ごとの照度値リスト
        luxLst = LimitedArrayList<LuxData>(appConf.limit, appConf.limit)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        luxPagerAdapter = LuxPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
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

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

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
        var sensorLight: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
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
        /*
        // アラート画面
        supportFragmentManager.beginTransaction()
                .add(R.id.frameErrMsg, NoSensorFragment.newInstance())
                .commit()
        container.visibility = View.GONE
        */

        // 加速度センサ
        var sensorAccel: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // 加速度センサあり
        if (sensorAccel != null) {
            sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_UI)
        }

        // 磁気センサ
        var sensorMag: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        // 磁気センサあり
        if (sensorMag != null) {
            sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // センサーの監視を終了する
    override fun onPause() {
        super.onPause()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    // 言語が変わったとき呼ばれるかどうか
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d( javaClass.simpleName, "onRestoreInstanceState")
        // アプリ設定をロードする
        val appl = application as LuxApplication
        appl.loadAppConf()
    }

    // 言語が変わったとき呼ばれるかどうか
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.d( javaClass.simpleName, "onSaveInstanceState")
    }

    // SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    // SensorEventListener
    override fun onSensorChanged(event: SensorEvent) {
        /*
        if ( event?.sensor?.type != Sensor.TYPE_LIGHT) return
        // 照度センサの値を取得
        val lux = event.values[0]
        */

        when (event.sensor.type) {
            // 加速度センサ
            Sensor.TYPE_ACCELEROMETER -> {
                accel = event.values.clone()
                return
            }
            // 磁気センサ
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magnetic = event.values.clone()
                return
            }
            // 照度センサ
            Sensor.TYPE_LIGHT -> light = event.values.clone()
            else -> return
        }
        // -------------------------------------------------
        // "照度センサ値"があれば、
        // 値を各フラグメントに通知する
        // -------------------------------------------------
        if ( light != null ) {
            // -------------------------------------------------
            // "加速度センサ値"・"磁気センサ値"があれば、
            // 方位角・傾斜角・回転角を取得
            // -------------------------------------------------
            if ( (accel != null) and (magnetic != null) ) {
                // 回転行列を計算
                SensorManager.getRotationMatrix(inR,ix,accel,magnetic)
                // 端末の画面設定に合わせる(以下は, 縦表示で画面を上にした場合)
                SensorManager.remapCoordinateSystem(inR,SensorManager.AXIS_X,SensorManager.AXIS_Y,outR)
                // 方位角/傾きを取得
                SensorManager.getOrientation(outR,attitude)
            }

            // 計測時刻
            val now = Date()

            // １秒ごとに照度値をバッファに格納
            var tick = false
            if ( now.time/1000 != luxData.t.time/1000 ) {
                tick = true
                luxData = LuxData(now,light[0])
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
                // ----------------------------------------------------------
                // 現在の照度値は、検知したら各フラグメントへ通知
                // ----------------------------------------------------------
                fragment.onUpdate(light[0])
                // ----------------------------------------------------------
                // "端末の姿勢値"があり
                // フラグメントが、それを受け取り可能な場合、値を通知する
                // ----------------------------------------------------------
                if ( (fragment is NewVal02Listener) and (attitude != null) ) {
                    (fragment as? NewVal02Listener)?.onUpdate(luxData,attitude)
                }
                // ----------------------------------------------------------
                // 照度値のリストは、1秒ごとに各フラグメントへ通知
                // ----------------------------------------------------------
                if ( tick == true ) {
                    fragment.onUpdate(luxLst)
                }
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

    private fun selectOnOff() {
        // ページが選択されたこと・選択から外れたことを通知する
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
