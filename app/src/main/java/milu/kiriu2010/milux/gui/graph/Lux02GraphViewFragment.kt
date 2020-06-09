package milu.kiriu2010.milux.gui.graph


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import milu.kiriu2010.milux.LuxApplication
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.OrientationListener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.milux.gui.SelectedListener
import milu.kiriu2010.util.LimitedArrayList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

// 照度をグラフ表示する
class Lux02GraphViewFragment : Fragment()
        , NewVal01Listener
        , OrientationListener
        , ResetListener
        , SelectedListener {

    // 現在の照度
    private var lux: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView
    // 照度の強さを表すビュー
    private lateinit var overView: ImageView
    // 照度の強さを描画するdrawable
    private lateinit var drawable: LuxGraphViewDrawable

    // バックグラウンドに使うペイント
    private val paintBackground = Paint().apply {
        color = Color.WHITE
    }
    // 枠に使うペイント
    private val paintFrame = Paint(0).apply {
        color = Color.BLACK
        strokeWidth = 20f
        // これがないと枠でなく、塗りつぶされてしまう
        style = Paint.Style.STROKE
    }
    // 基準に使うペイント
    private val paintLineBase = Paint(0).apply {
        color = Color.BLACK
        strokeWidth = 2f
        style = Paint.Style.STROKE
        // ダッシュ線
        pathEffect = DashPathEffect( floatArrayOf(25f,15f), 0f)
    }
    // 照度の位置を示すペイント
    private  val paintLineLux = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    // 時刻に使うペイント
    private val paintTime = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        textSize = 40f
        style = Paint.Style.FILL_AND_STROKE
    }

    // 照度の強さを表示するビューの幅・高さ
    private var ow = 0f
    private var oh = 0f

    // ビューとグラフの枠とのマージン
    private val mw = 100f
    private val mh = 100f

    // 時刻フォーマット
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        // タイムゾーンを自端末に合わせる
        timeZone = TimeZone.getDefault()
    }

    // タイマーで呼び出されるハンドラー
    private val handler = Handler()
    // 描画時に呼び出されるスレッド
    private lateinit var runnable: Runnable

    // このフラグメントが選択されたかどうか
    private var selected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux02_graph_view, container, false)

        // アプリ設定を取得
        val appl = context?.applicationContext as? LuxApplication
        val appConf = appl?.appConf

        // 照度の強さを表示するビュー
        overView = view.findViewById(R.id.imageViewGraph)
        drawable = LuxGraphViewDrawable(appConf!!)
        overView.setImageDrawable(drawable)

        // 描画
        drawable.invalidateSelf()

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        return view
    }

    // NewVal01Listener
    // 新しい照度を設定
    @SuppressLint("SetTextI18n")
    override fun onUpdate(lux: Float) {
        //Log.d( javaClass.simpleName, "lux[$lux]")
        //if ( lux == this.lux ) return
        this.lux = lux

        // 照度の数値を表示
        if (this::dataLux.isInitialized) {
            //Log.d( javaClass.simpleName, "dataLux already initialized")
            dataLux.text = "%.1f lx".format(this.lux)
        }
    }

    // NewVal01Listener
    // 照度値の配列を渡す
    override fun onUpdate( luxLst: LimitedArrayList<LuxData> ) {
        // 照度の強さ⇔時刻をグラフ化
        if (this::drawable.isInitialized) {
            drawable.onUpdate(luxLst)
        }
    }

    // OrientationListener
    override fun onActivityOrientation(): Int {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    // ResetListener
    // メンバ変数を初期化する
    override fun OnReset() {
        //Log.d( javaClass.simpleName, "OnReset")
        // 現在の照度
        lux = 0f

        // 照度の強さ⇔時刻をグラフを初期化
        if (this::drawable.isInitialized) {
            drawable.OnReset()
        }
    }

    // SelectedListener
    override fun onSelected(selected: Boolean) {
        this.selected = selected
        if (this::overView.isInitialized) {
            overView.visibility = if (selected == true) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
        Log.d(javaClass.simpleName, "onSelected:${selected}")
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                Lux02GraphViewFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
