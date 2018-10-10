package milu.kiriu2010.milux.gui

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import milu.kiriu2010.milux.LuxApplication
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.id.FragmentID
import milu.kiriu2010.util.LimitedArrayList
import java.util.*

class LuxActivity : AppCompatActivity()
        , SensorEventListener
        , ResetListener {

    // アプリ設定
    private lateinit var appConf: AppConf

    // ナビゲーションドロワーの状態操作用オブジェクト
    private var drawerToggle: ActionBarDrawerToggle? = null

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var luxPagerAdapter: LuxPagerAdapter? = null

    // 照度センサの値
    private var luxData = LuxData()

    // 時刻ごとの照度値リスト
    // 1分間データを保持
    //private val luxLst = LimitedArrayList<LuxData>(appConf.limit, appConf.limit)
    private lateinit var luxLst: LimitedArrayList<LuxData>

    // タイマーで呼び出されるハンドラー
    //private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lux)

        /*
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameLux, fragment)
                .commit()
                */

        // アプリ設定
        val appl = application as LuxApplication
        appConf = appl.appConf

        // 時刻ごとの照度値リスト
        luxLst = LimitedArrayList<LuxData>(appConf.limit, appConf.limit)
        //luxLst.limit = appConf.limit

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        luxPagerAdapter = LuxPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = luxPagerAdapter

        // アクティブなフラグメントが切り替わったら呼び出される
        /*
        container.addOnPageChangeListener( object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                val fragment = luxPagerAdapter?.getItem(pos) ?: return
                // 画面の方向を表示する内容によって変更する
                if ( fragment is OrientationListener ) {
                    requestedOrientation = fragment.onActivityOrientation()
                }
            }

        })
        */


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

        if ( savedInstanceState == null ) {
            // メニューを表示するフラグメントを追加
            if ( supportFragmentManager.findFragmentByTag(FragmentID.ID_MENU.id) == null ) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frameMenu, MenuFragment.newInstance(), FragmentID.ID_MENU.id)
                        .commit()
            }
        }

        // レイアウトからドロワーを探す
        //   Portrait  => ドロワーあり
        //   Landscape => ドロワーなし
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        // レイアウト中にドロワーがある場合設定を行う
        if ( drawerLayout != null ) {
            setupDrawer(drawerLayout)
        }
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
        }
    }

    // センサーの監視を終了する
    override fun onPause() {
        super.onPause()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
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

            fragment.onUpdate(lux)
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

    // アクティビティの生成が終わった後に呼ばれる
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // ドロワーのトグルの状態を同期する
        // これを実施しないと、
        // メニューが表示されてなくても"←"マークが左上に表示されつづける
        drawerToggle?.syncState()
    }

    // 画面が回転するなど、状態が変化したときに呼ばれる
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // 状態の変化をドロワーに伝える
        drawerToggle?.onConfigurationChanged(newConfig)
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
            R.id.action_reset -> {
                OnReset()
                return true
            }
        }

        // ドロワーに伝える
        if ( drawerToggle?.onOptionsItemSelected(item) == true ) {
            return true
        }
        else {
            return super.onOptionsItemSelected(item)
        }
    }

    // ナビゲーションドロワーを開閉するためのアイコンをアクションバーに配置する
    private fun setupDrawer( drawer: DrawerLayout ) {
        val toggle = ActionBarDrawerToggle( this, drawer, R.string.app_name, R.string.app_name )
        // ドロワーのトグルを有効にする
        toggle.isDrawerIndicatorEnabled = true
        // 開いたり閉じたりのコールバックを設定する
        drawer.addDrawerListener(toggle)

        drawerToggle = toggle

        // アクションバーの設定を行う
        supportActionBar?.apply {
            // ドロワー用のアイコンを表示
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }
}
