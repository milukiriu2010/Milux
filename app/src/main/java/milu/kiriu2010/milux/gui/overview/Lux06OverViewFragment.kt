package milu.kiriu2010.milux.gui.overview


import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.TextView
import milu.kiriu2010.gui.move.*
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.OrientationListener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.milux.gui.SelectedListener
import milu.kiriu2010.util.LimitedArrayList
import kotlin.math.log

class Lux06OverViewFragment : androidx.fragment.app.Fragment()
        , SurfaceHolder.Callback
        , NewVal01Listener
        , OrientationListener
        , ResetListener
        , SelectedListener {

    // 照度
    private var lux: Float = 0f
    // 照度をlog対数表示するため、補正した値
    private var luxC: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView
    // 照度の強さを表すビュー
    private lateinit var overView: SurfaceView

    // 照度の強さを表示するビューの幅・高さ
    private var wh = AVector()

    // 照度値をlog10したときのMAX値
    // LIGHT_SUNLIGHT_MAX 120000.0
    private val luxLog10Max = 5f


    // 照明セグメント象徴画像の移動量
    private val mv = 5f
    // 照明セグメント象徴画像の回転角度(速度)
    private val av = 10f
    // ５つある照度セグメントの象徴画像
    private val luxSegMover: MutableList<AMoverAbs> =
            mutableListOf(
                    AMoverRect(iv=AVector(x=mv)),
                    AMoverRect(iv=AVector(x=mv)),
                    AMoverRect(iv=AVector(x=mv)),
                    AMoverRect(iv=AVector(x=mv)),
                    AMoverRect(iv=AVector(x=mv))
            )

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

    init {
        // ５つある照度セグメントの象徴画像
        luxSegMover.forEach {
            // 回転角度(位置)
            it.al = AAngle(x=180f,y=180f)
            // 回転角度(速度)
            it.av = AAngle(y=av)
            // 回転による射影位置を更新
            it.updateByReflect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(javaClass.simpleName,"onDestroy")
        if (this::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(javaClass.simpleName,"onCreateView")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux06_over_view, container, false)

        // 照度の強さを表示するサーフェスビュー
        overView = view.findViewById(R.id.overView01)
        overView.holder.setFormat(PixelFormat.TRANSLUCENT)
        overView.setZOrderOnTop(false)
        overView.setZOrderMediaOverlay(false)
        overView.holder.addCallback(this)

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        // アニメーションする画像をロードする
        // 太陽を描画(セグメント４)
        luxSegMover[4].bmp = BitmapFactory.decodeResource(resources, R.drawable.a_sun)
        // 日の出を描画(セグメント３)
        luxSegMover[3].bmp = BitmapFactory.decodeResource(resources, R.drawable.a_sunrise)
        // 雲を描画(セグメント２)
        luxSegMover[2].bmp = BitmapFactory.decodeResource(resources, R.drawable.a_cloudy)
        // 月を描画(セグメント１)
        luxSegMover[1].bmp = BitmapFactory.decodeResource(resources, R.drawable.a_moon)
        // 星を描画(セグメント０)
        luxSegMover[0].bmp = BitmapFactory.decodeResource(resources, R.drawable.a_star)

        return view
    }

    // SurfaceHolder.Callback
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        // 1080 x 1457(24sp) emulator
        // 1080 x 1769(24sp) huawei p20 lite

        Log.d( javaClass.simpleName, "surfaceChanged:w[$width]h[$height]")
        wh.x = width.toFloat()
        wh.y = height.toFloat()

        // 物体の大きさを再設定する
        luxSegMover.forEach {
            it.wh.x = wh.y/luxLog10Max
            it.wh.y = wh.y/luxLog10Max
            // 回転による射影位置を更新
            it.updateByReflect()
        }
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
        //Log.d( javaClass.simpleName, "onUpdate:lux[$lux]")
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
                move();

                drawCanvas()
            }
            handler.post(runnable)
        }

    }

    // 照度値に対応するセグメントの象徴画像を右へ移動
    private fun move() {

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
        luxC = when {
            ( lux < 1f ) -> 0f
            ( log(lux,10f) > luxLog10Max ) -> luxLog10Max
            else -> log(lux,10f)
        }

        // 照度セグメントの象徴画像を右へ移動
        // セグメント１・２・３
        val seg = if ( luxC < 4) {
            luxC.toInt()
        }
        // セグメント４・５
        else {
            4
        }

        val luxSeg = luxSegMover[seg]

        // 照度値に対応するセグメントを右に移動
        luxSeg.il.x += luxSeg.iv.x
        // 照度値に対応するセグメントを回転
        luxSeg.rotateByCenter()
        // 回転による射影位置を更新
        luxSeg.updateByReflect()


        // 右端に来た場合、左端へ位置補正する
        //val correctPos = ACorrectPosLR01( AVector(), wh)
        val correctPos = ACorrectPosLR02( AVector(), wh)
        correctPos.doo(luxSeg)
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
        ((luxSegMover.size-1) downTo 0).forEach { seg ->
            drawImage( canvas, luxSegMover[seg].bmp, seg, fh )
        }

        // 座標位置を初期値に戻す
        canvas.restore()

        // 照度の位置を描画
        val luxH = wh.y * (luxLog10Max-luxC)/luxLog10Max
        canvas.drawLine(0f, luxH, wh.x, luxH, paintLineLux )

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
        // 座標を下に移動する
        // "4:太陽セグメント"以外は、座標を移動する
        if ( seg != 4 )  canvas.translate(0f, fh)

        val luxSeg = luxSegMover[seg]
        //Log.d(javaClass.simpleName, "seg[$seg]il.x[${luxSeg.il.x}]rl.x[${luxSeg.rl.x}]rs.x[${luxSeg.rs.x}]")

        val matrix = Matrix()
        // 左右反転
        if ( luxSeg.rs.x < 0 ) {
            matrix.postScale(-1f,1f)
        }
        else {
            matrix.postScale(1f,1f)
        }

        // 描画先の矩形イメージ
        val bmpt = Bitmap.createBitmap(bmp,0,0,bmp.width,bmp.height,matrix,false)
        val dst1f = if ( luxSeg.rs.x >= 0 ) {
            RectF(luxSeg.rl.x,0f,luxSeg.rl.x+luxSeg.rs.x,fh)
        }
        else {
            RectF(luxSeg.rl.x+luxSeg.rs.x,0f,luxSeg.rl.x,fh)
        }
        canvas.drawBitmap(bmpt, null, dst1f, paintBackground)
        // 右端にきたら、左端から出てくるようにする
        if ( (luxSeg.il.x+fh) > wh.x ) {
            val dst2f = if ( luxSeg.rs.x >= 0 ) {
                RectF(luxSeg.rl.x - wh.x, 0f,luxSeg.rl.x-wh.x+luxSeg.rs.x, fh)
            }
            else {
                RectF(luxSeg.rl.x - wh.x+luxSeg.rs.x, 0f,luxSeg.rl.x-wh.x, fh)
            }
            canvas.drawBitmap(bmpt, null, dst2f, paintBackground)
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

        // 初期位置に更新
        luxSegMover.forEach {
            // 位置
            it.il = AVector()
            // 回転角度(位置)
            it.al = AAngle(x=180f,y=180f)
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
                Lux06OverViewFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
