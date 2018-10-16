package milu.kiriu2010.milux.gui.overview


import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.TextView
import milu.kiriu2010.gui.move.AVector
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.OrientationListener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.milux.gui.SelectedListener
import milu.kiriu2010.util.LimitedArrayList
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 * Use the [Lux06OverViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux06OverViewFragment : Fragment()
        , SurfaceHolder.Callback
        , NewVal01Listener
        , OrientationListener
        , ResetListener
        , SelectedListener {

    // 照度
    private var lux: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView
    // 照度の強さを表すビュー
    private lateinit var overView: SurfaceView

    // 照度の強さを表示するビューの幅・高さ
    private var wh = AVector()

    // 照度値をlog10したときのMAX値
    // LIGHT_SUNLIGHT_MAX 120000.0
    private val luxLog10Max = 5f

    // ５つある照度セグメントのカウント数
    private val luxSeg = intArrayOf(0,0,0,0,0)

    // アニメーションする画像リスト
    private lateinit var bmpLst: List<Bitmap>

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
        handler.removeCallbacks(runnable)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux01_over_view, container, false)

        // 照度の強さを表示するサーフェスビュー
        overView = view.findViewById(R.id.overView01)
        overView.holder.setFormat(PixelFormat.TRANSLUCENT)
        overView.setZOrderOnTop(false)
        overView.setZOrderMediaOverlay(false)
        overView.holder.addCallback(this)

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        // アニメーションする画像をロードする
        bmpLst = listOf(
                // 太陽を描画(セグメント４)
                BitmapFactory.decodeResource(resources, R.drawable.a_sun),
                // 日の出を描画(セグメント３)
                BitmapFactory.decodeResource(resources, R.drawable.a_sunrise),
                // 雲を描画(セグメント２)
                BitmapFactory.decodeResource(resources, R.drawable.a_cloudy),
                // 月を描画(セグメント１)
                BitmapFactory.decodeResource(resources, R.drawable.a_moon),
                // 星を描画(セグメント０)
                BitmapFactory.decodeResource(resources, R.drawable.a_star)
        )

        return view
    }

    // SurfaceHolder.Callback
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        // 1080 x 1457(24sp) emulator
        // 1080 x 1769(24sp) huawei p20 lite

        Log.d( javaClass.simpleName, "surfaceChanged:w[$width]h[$height]")
        wh.x = width.toFloat()
        wh.y = height.toFloat()
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

        // 照度の強さを表示
        if (this::overView.isInitialized) {
            //drawCanvas()
            // 描画時に呼び出されるスレッド
            runnable = Runnable {
                drawCanvas()
            }
            handler.post(runnable)
        }

    }

    // NewVal01Listener
    // 照度値の配列を渡す
    override fun onUpdate( luxLst: LimitedArrayList<LuxData>) {
        /*
        if ( luxLst.size <= 0 ) return
        val luxData = luxLst[0]

        // 照度セグメントはlog対数で分かれているため、補正する
        val luxC = when {
            ( luxData.lux < 1f ) -> 0
            ( log(luxData.lux,10f) >= luxLog10Max ) -> (luxLog10Max-1).toInt()
            else -> log(luxData.lux,10f).toInt()
        }

        // 照度セグメントの値を１つ増やす
        luxSeg[luxC]++
        */
    }

    // 照度の強さを描画
    private fun drawCanvas() {
        // ページが選択されていなければ描画しない
        //if ( selected == false ) return

        val canvas = overView.holder.lockCanvas()
        if (canvas == null) {
            //Log.d( javaClass.simpleName, "canvas is null")
            //overView.holder.unlockCanvasAndPost(canvas)
            return
        }

        //Log.d( javaClass.simpleName, "drawCanvas:w[$ow]h[$oh]")

        // ----------------------------------------------------------------------
        // http://seesaawiki.jp/w/moonlight_aska/d/%be%c8%c5%d9%a5%bb%a5%f3%a5%b5%a1%bc%a4%ce%c3%cd%a4%f2%bc%e8%c6%c0%a4%b9%a4%eb
        // ----------------------------------------------------------------------
        // 明るさの目安
        // ----------------------------------------------------------------------
        //   LIGHT_SUNLIGHT_MAX 120000.0       5.079
        //   LIGHT_SUNLLIGHT    110000.0       5.041
        //   LIGHT_SHADE         20000.0       4.301
        //   LIGHT_OVERCAST      10000.0       4
        //   LIGHT_SUNRISE         400.0       2.602
        //   LIGHT_CLOUDY          100.0       2
        //   LIGHT_FULLMOON          0.25     -0.602
        //   LIGHT_NO_MOON           0.0010   -3
        // ----------------------------------------------------------------------
        // ただし、計測しても1未満は表示されることはない
        // ----------------------------------------------------------------------
        // 照度をlog対数表示するため、補正する
        val luxC = when {
            ( lux < 1f ) -> 0f
            ( log(lux,10f) > luxLog10Max ) -> luxLog10Max
            else -> log(lux,10f)
        }

        // 照度セグメントの値を１つ増やす
        if ( luxC < 4) {
            luxSeg[luxC.toInt()] += 5
        }
        else {
            luxSeg[4] += 5
        }

        // 座標移動するため、初期位置を保存する
        canvas.save()

        // バックグラウンドを塗りつぶす
        val frame = RectF( 0f, 0f, wh.x, wh.y)
        canvas.drawRect(frame, paintBackground)
        //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // 枠を描画
        canvas.drawRect(frame, paintFrame)

        // 各フレームの高さ
        val fh = wh.y/luxLog10Max
        // 各フレームを描画
        val baseLine = Path()
        baseLine.moveTo(0f, 0f)
        baseLine.lineTo( wh.x, 0f)
        // 閉じると点線じゃなくなる？
        //baseLine.close()
        for ( i in 1 until luxLog10Max.toInt() ) {
            canvas.translate( 0f, fh.toFloat())
            canvas.drawPath(baseLine,paintLineBase)
        }

        // 座標位置を初期値に戻す
        canvas.restore()

        // 座標移動するため、初期位置を保存する
        canvas.save()

        // 各セグメントの画像を描画する
        bmpLst.forEachIndexed { index, bitmap ->
            drawImage(canvas, bitmap, bmpLst.size-index-1,fh)
        }

        // 座標位置を初期値に戻す
        canvas.restore()

        // 照度の位置を描画
        val luxH = wh.y * (luxLog10Max-luxC)/luxLog10Max
        canvas.drawLine(0f, luxH, wh.y, luxH, paintLineLux )

        overView.holder.unlockCanvasAndPost(canvas)
    }

    // ---------------------------------------------------
    // 照度に対応する画像を描画する
    // ---------------------------------------------------
    //   seg: セグメント
    //        4:太陽
    //        3:日の出
    //        2:雲
    //        1:月
    //        0:星
    //    fh: 各セグメントの幅
    // ---------------------------------------------------
    private fun drawImage( canvas: Canvas, bmp: Bitmap, seg: Int, fh: Float ) {
        val src = Rect(0,0, bmp.width, bmp.height)
        // 描画先の矩形イメージ
        val dst = Rect((luxSeg[seg]%wh.x).toInt(), 0, ((luxSeg[seg]%wh.x) + fh).toInt(), fh.toInt())
        // 座標を下に移動する
        if ( seg != 4 ) {
            canvas.translate(0f, fh)
        }
        canvas.drawBitmap(bmp, src, dst, paintBackground)
        // 右端にきたら、左端から出てくるようにする
        if ( (luxSeg[seg]%wh.x+fh).toInt() > wh.x.toInt() ) {
            val dst2 = Rect(((luxSeg[seg]+fh)%wh.x-fh).toInt(), 0, ((luxSeg[seg]+fh)%wh.x).toInt(), fh.toInt())
            canvas.drawBitmap(bmp, src, dst2, paintBackground)
        }
    }

    // OrientationListener
    override fun onActivityOrientation(): Int {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // ResetListener
    // メンバ変数を初期化する
    override fun OnReset() {
        //Log.d( javaClass.simpleName, "OnReset")
        // 照度
        lux = 0f
        // ５つある照度セグメントのカウント数をクリアする
        //luxSeg.forEach { i -> 0 }
        for ( i in 0 until luxSeg.size ) {
            luxSeg[i] = 0
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Lux01OverViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                Lux06OverViewFragment().apply {
                    arguments = Bundle().apply {
                        /*
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        */
                    }
                }
    }
}
