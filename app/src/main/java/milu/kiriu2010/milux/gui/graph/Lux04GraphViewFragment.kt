package milu.kiriu2010.milux.gui.graph


import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
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

/**
 * A simple [Fragment] subclass.
 * Use the [Lux04GraphViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux04GraphViewFragment : androidx.fragment.app.Fragment()
        , SurfaceHolder.Callback
        , NewVal01Listener
        , OrientationListener
        , ResetListener
        , SelectedListener {

    // 現在の照度
    private var lux: Float = 0f

    // 照度MAX値
    private var luxMax = 10f
    // 照度MIN値
    private var luxMin = 0f
    // 照度差分(MAX-MIN)値
    private var luxDif = 10f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView
    // 照度の強さを表すビュー
    private lateinit var overView: SurfaceView

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
    private val timeFmt = SimpleDateFormat("HH:mm:ss").apply {
        // タイムゾーンを自端末に合わせる
        timeZone = TimeZone.getDefault()
    }

    // 描画時に呼び出されるハンドラー
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
        val view = inflater.inflate(R.layout.fragment_lux04_graph_view, container, false)

        // 照度の強さを表示するサーフェスビュー
        overView = view.findViewById(R.id.overView04)
        overView.holder.setFormat(PixelFormat.TRANSLUCENT)
        overView.setZOrderOnTop(false)
        overView.setZOrderMediaOverlay(false)
        overView.holder.addCallback(this)

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        return view
    }

    // SurfaceHolder.Callback
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        // 1080 x 1457(24sp) emulator
        // 1080 x 1769(24sp) huawei p20 lite

        Log.d( javaClass.simpleName, "surfaceChanged:w[$width]h[$height]")
        ow = width.toFloat()
        oh = height.toFloat()
    }

    // SurfaceHolder.Callback
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    // SurfaceHolder.Callback
    override fun surfaceCreated(holder: SurfaceHolder?) {
    }

    // NewVal01Listener
    // 新しい照度を設定
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
        // 照度最大値は最低でも10とする
        // letは最後の値をreturnする
        luxMax = luxLst.maxBy { it.lux }?.lux?.let {
            when {
                (it < 10f) -> 10f
                else -> it
            }
        } ?: 10f
        // 照度最低値は最低でも0とする
        luxMin = luxLst.minBy { it.lux }?.lux?.let {
            when {
                (it < 0f) -> 0f
                else -> it
            }
        } ?: 0f
        // 照度差分(最大値ー最小値)
        luxDif = luxMax - luxMin
        //Log.d( javaClass.simpleName, "onUpdate:max[$luxMax]min[$luxMin]dif[$luxDif]size[${luxLst.size}]")

        // 照度の強さ⇔時刻をグラフ化
        if (this::overView.isInitialized) {
            //drawCanvas(luxLst)
            // 描画時に呼び出されるスレッド
            runnable = Runnable {
                drawCanvas(luxLst)
            }
            handler.post(runnable)
        }
    }

    private fun drawCanvas( luxLst: LimitedArrayList<LuxData> ) {
        //if ( selected == false ) return

        //Log.d( javaClass.simpleName, "drawCanvas:w[$ow]h[$oh]")
        val canvas = overView.holder.lockCanvas()
        if (canvas == null) {
            //Log.d( javaClass.simpleName, "canvas is null")
            //overView.holder.unlockCanvasAndPost(canvas)
            return
        }

        // アプリ設定を取得
        val appl = context?.applicationContext as? LuxApplication
        val appConf = appl?.appConf

        // 照度値サンプリング数の上限数-1
        // 見つからない場合10としている
        var limit = appConf?.limit?.let {
            it - 1
        } ?: 10

        // 照度DIFF値からグラフ表示に使うDIFF値を計算
        //   luxMin      luxMax     luxDif
        // -------------------------------------
        //      253         254          1
        //      250         260         10
        // -------------------------------------
        //      211         258         47
        //      210         260         50
        // -------------------------------------
        //      209         251         42
        //      200         260         60
        // -------------------------------------
        //       81         111         30
        //       80         120         40
        // -------------------------------------
        //       81        2112        2031
        //       80        2200        2120
        // -------------------------------------
        //      111        2112        2001
        //       80        2200        2120
        // -------------------------------------
        val luxDifLog10 = if ( luxDif == 0f ) {
            0
        }
        else {
            log10(luxDif).toInt()
        }
        var gdif = if ( luxDif == 0f ) {
            0f
        }
        else {
            ((luxDif / 10f.pow(luxDifLog10)).toInt()+2)*10f.pow(luxDifLog10)
        }
        // 最低でも10間をあける
        if ( gdif < 10f ) {
            gdif = 10f
        }
        //Log.d( javaClass.simpleName, "gdif[$gdif]luxDifLog10[$luxDifLog10]luxDif[$luxDif]")

        // ----------------------------------------------------
        // 差分の桁数で割り算・再び掛け算をすることにより、
        // 有効桁数以下を切り落として、MIN値を求める
        // ----------------------------------------------------
        val luxGdifLog10 = log10(gdif).toInt()
        val gmin = if ( luxMin == 0f ) {
            0f
        }
        else {
            ((luxMin / 10f.pow(luxGdifLog10)).toInt())*10f.pow(luxGdifLog10)
        }
        //Log.d( javaClass.simpleName, "gmin[$gmin]luxGdifLog10[$luxGdifLog10]luxMin[$luxMin]")

        // ----------------------------------------------------------------
        // 差分の桁数で "割り算＋1したもの" に再び掛け算をすることにより、
        // 有効桁数以下を切り落として、MIN値を求める
        // ----------------------------------------------------------------
        val gmax = if ( luxMax == 0f ) {
            0f
        }
        else {
            ((luxMax / 10f.pow(luxGdifLog10)).toInt()+1)*10f.pow(luxGdifLog10)
        }
        //Log.d( javaClass.simpleName, "gmax[$gmax]luxGdifLog10[$luxGdifLog10]luxMin[$luxMax]")

        // バックグラウンドを塗りつぶす
        val background = Rect( 0, 0, ow.toInt(), oh.toInt())
        canvas.drawRect(background, paintBackground)

        // 枠を描画
        val frame = Rect( mw.toInt(), mh.toInt(), (ow-mw).toInt(), (oh-mh).toInt())
        canvas.drawRect(frame, paintFrame)

        // 座標移動するため、初期位置を保存する
        canvas.save()

        // ------------------------------------------------------------------------
        // 時刻のダッシュ線を描画
        // ------------------------------------------------------------------------
        val timeLine = Path()
        timeLine.moveTo( 0f, mh )
        timeLine.lineTo( 0f, oh-mh)
        // X軸を右マージン分移動
        canvas.translate( mw, 0f)
        // X軸を5分割し、縦にダッシュ線を描く
        val divX = 5
        val boundsTime = Rect()
        for ( i in divX downTo 0 ) {
            canvas.drawPath(timeLine,paintLineBase)
            // バッファを超えてアクセスするとExceptionが発生するので
            // チェックしている
            if ( (i*limit/divX) < luxLst.size ) {
                val date = luxLst[(i*limit/divX)].t
                val strTime = timeFmt.format(date)
                paintTime.getTextBounds( strTime, 0, strTime.length, boundsTime )
                // 文字を詰めすぎるとみにくいので1つ置きに時刻を表示
                if ( i%2 == 0 ) {
                    canvas.drawText(strTime, -boundsTime.width()/2f, oh - mh/2, paintTime)
                }
                else {
                    canvas.drawText(strTime, -boundsTime.width()/2f, oh, paintTime)
                }
                //Log.d( javaClass.simpleName, "i[$i]luxLst.size[${luxLst.size}]time[$strTime]")
            }
            canvas.translate( (frame.width()/divX).toFloat(), 0f)
        }

        // 座標位置を初期値に戻す
        canvas.restore()

        // 座標移動するため、初期位置を保存する
        canvas.save()

        // ------------------------------------------------------------------------
        // 照度スケールのダッシュ線を描画
        // ------------------------------------------------------------------------
        // 一番上に単位を描画
        val luxGmaxLog10 = log10(gmax).toInt()
        canvas.drawText( "(x ${(10f.pow(luxGmaxLog10)).toInt()})", mw, mh/2, paintTime )
        val luxScaleLine = Path()
        luxScaleLine.moveTo( mw, 0f)
        luxScaleLine.lineTo( ow-mw, 0f)
        // Y軸を上マージン分移動
        canvas.translate( 0f, mh )
        // Y軸を5分割し、横にダッシュ線を描く
        val divY = 5
        for ( i in gmax.toInt() downTo gmin.toInt() step ((gmax-gmin)/5).toInt() ) {
            canvas.drawPath(luxScaleLine, paintLineBase)
            val strLux = i.toFloat()/(10f.pow(luxGmaxLog10))
            //canvas.drawText( strLux.toString(), 10f, 0f, paintTime)]
            // 照度を表す文字を小数点下2桁まで表示
            canvas.drawText( "%1$.2f".format(strLux), 10f, 0f, paintTime)
            if ( i > gmin.toInt() ) {
                canvas.translate(0f, (frame.height() / divY).toFloat())
            }
        }

        // 座標位置を初期値に戻す
        canvas.restore()

        // ---------------------------------
        // 照度値を描画
        // ---------------------------------
        // 幅(ow-2*mw)
        // 高さ(oh-2*mh)
        // の領域に描画する
        // ---------------------------------
        // gmax(上:mh)
        // 0   (下:oh-mh)
        // ---------------------------------
        // 最新(右:ow-mw)
        // 最古(左:mw)
        // ---------------------------------
        val luxPath = Path()
        for ( i in 0 until luxLst.size ) {
            val luxData = luxLst[i]
            if ( i == 0 ) {
                luxPath.moveTo( ow-mw, (oh-mh)-(luxData.lux-gmin)/(gmax-gmin)*(oh-2*mh) )
            }
            else {
                luxPath.lineTo( (ow-mw)-(ow-2*mw)*i/limit, (oh-mh)-(luxData.lux-gmin)/(gmax-gmin)*(oh-2*mh) )
            }
        }
        // closeすると、始点と結ばれるらしい
        //luxPath.close()
        canvas.drawPath(luxPath,paintLineLux)

        overView.holder.unlockCanvasAndPost(canvas)
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

        // 照度MAX値
        luxMax = 10f
        // 照度MIN値
        luxMin = 0f
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Lux02GraphViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                Lux04GraphViewFragment().apply {
                    arguments = Bundle().apply {
                        /*
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        */
                    }
                }
    }
}
