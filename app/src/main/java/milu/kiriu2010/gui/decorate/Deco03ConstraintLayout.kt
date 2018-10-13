package milu.kiriu2010.gui.decorate

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log

// 2018.10.13 clear markRectLst in kickRunnable
class Deco03ConstraintLayout
    @JvmOverloads
    constructor(
            context: Context?,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    )
    : ConstraintLayout(context, attrs, defStyleAttr){

    // ---------------------------------------------
    // マーカ(共通変数)
    // ---------------------------------------------
    // マーカがアニメーション中かどうか
    var animated = true
    // マーカの長さ
    var markLen = 100f
    // マーカの移動スピード
    var markVelocity = 10f


    // ---------------------------------------------
    // マーカ(枠)の変数
    // ---------------------------------------------
    // マーカ描画開始点
    val markPoint = PointF()
    // マーカ描画用パス
    val markPath = Path()
    // マーカの移動方向
    //   0:左⇒右
    //   1:上⇒下
    //   2:右⇒左
    //   3:下⇒上
    var markDir = 0

    // ---------------------------------------------
    // マーカ(長方形)の変数
    // ---------------------------------------------
    // マーカ(長方形)のリスト
    val markRectLst = arrayListOf<RectF>()
    // マーカ(長方形)を作成するために描画領域を分割する数
    val markRectSplitCnt = 5
    // マーカ(長方形)が移動可能な距離
    // 長方形の幅×２
    var markRectMoveLen = 0f
    // マーカ(長方形)の座標位置(0 - markRectMoveLenの範囲で移動可能)
    var markRectPos = 0f

    /*
    // テキスト描画に使うペイント
    val paintText = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        textSize = 40f
        style = Paint.Style.FILL_AND_STROKE
    }
    */
    // 枠描画に使うペイント
    val paintFrame = Paint().apply {
        color = Color.GRAY
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }
    // マーカ描画に使うペイント
    val paintMark = Paint().apply {
        color = Color.RED
        strokeWidth = 20f
        // 角にきても、線を描く
        //style = Paint.Style.STROKE
        // 角にくると、角を塗りつぶすように描く
        style = Paint.Style.FILL_AND_STROKE
    }
    // マーカ描画に使うペイント(塗りつぶし用バックグラウンド)
    val paintMarkBackground = Paint().apply {
        //color = Color.argb(64,255,0,0)
        color = Color.WHITE

        //strokeWidth = 5f
        // 角にきても、線を描く
        //style = Paint.Style.STROKE
        // 角にくると、角を塗りつぶすように描く
        style = Paint.Style.FILL
    }
    // マーカ描画に使うペイント(塗りつぶし用その１)
    val paintMarkFill1 = Paint().apply {
        color = Color.argb(64,255,0,0)
        //color = Color.RED

        //strokeWidth = 5f
        // 角にきても、線を描く
        //style = Paint.Style.STROKE
        // 角にくると、角を塗りつぶすように描く
        style = Paint.Style.FILL
    }
    // マーカ描画に使うペイント(塗りつぶし用その２)
    val paintMarkFill2 = Paint().apply {
        color = Color.argb(16,255,0,0)
        //color = Color.WHITE
        //strokeWidth = 5f
        // 角にきても、線を描く
        //style = Paint.Style.STROKE
        // 角にくると、角を塗りつぶすように描く
        style = Paint.Style.FILL
    }

    // マーカを移動するためのハンドラ
    private val markHandler = Handler()
    // マーカを移動するためのスレッド
    private lateinit var markRunnable: Runnable

    init {
        // レイアウトでonDrawを呼び出すために
        // このメソッドをキックすることが必要
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d( javaClass.simpleName, "onSizeChanged")
        kickRunnable(animated)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        markHandler.removeCallbacks(markRunnable)
    }

    fun kickRunnable( animated: Boolean = true ) {
        this.animated = animated

        val w = width
        val h = height
        // マーカ(長方形)の幅を求める
        val rectw = (w/markRectSplitCnt).toFloat()
        // マーカ(長方形)の移動可能な範囲を求める
        markRectMoveLen = rectw * 2f

        // レイアウトのサイズが変わったとき呼び出されるので
        // マーカ(長方形)リストをクリアする
        markRectLst.clear()
        // 濃い色で塗りつぶす
        markRectLst.add(RectF(-rectw*2f, 0f, -rectw, h.toFloat()))
        // 薄い色で塗りつぶす
        markRectLst.add(RectF(-rectw, 0f, 0f, h.toFloat()))

        markRunnable = Runnable {
            //Log.d( javaClass.simpleName, "runnable")
            // ---------------------------------------------
            // マーカ(枠)の移動－枠をなぞる
            // ---------------------------------------------
            moveFrame(w,h)

            // ---------------------------------------------
            // マーカ(長方形)の移動－ネオン効果
            // ---------------------------------------------
            moveNeon()

            // ---------------------------------------------
            // 描画する
            // ---------------------------------------------
            //invalidate()
            postInvalidate()

            // ---------------------------------------------
            // アニメーション中であれば、
            // 描画スレッドをキックする
            // ---------------------------------------------
            if ( animated ) {
                markHandler.postDelayed(markRunnable, 50)
            }
        }
        markHandler.post(markRunnable)
    }

    // ---------------------------------------------
    // マーカ(枠)の移動－枠をなぞる
    // ---------------------------------------------
    private fun moveFrame( w: Int, h: Int ) {
        // リセットしないと前回描いた線と今回描く線がつながってしまう。
        markPath.reset()
        // 左⇒右
        if ( markDir == 0 ) {
            markPoint.x += markVelocity
            markPath.moveTo(markPoint.x,markPoint.y)
            // マーカの右端がビューの右端に達していない場合
            // "マーカの左端"～"マーカの右端"を描画
            if (markPoint.x+markLen < w.toFloat()) {
                markPath.lineTo(markPoint.x+markLen, markPoint.y)
            }
            // マーカの右端がビューの右端に達している場合
            // (1) "マーカの左端"～"ビューの右端"を描画
            // (2) "ビューの右上角"～"ビューの右端残り"を描画
            else {
                markPath.lineTo(w.toFloat(), markPoint.y)
                markPath.lineTo(w.toFloat(),markLen-(w.toFloat()-markPoint.x))
            }

            // マーカの始点が右上角に到達したらマーカの移動方向を"上⇒下"に変更する
            // マーカの始点は、ビューの右上角に合わせる
            if (markPoint.x >= w.toFloat()) {
                markDir = 1
                markPoint.x = w.toFloat()
                markPoint.y = 0f
            }
        }
        // 上⇒下
        else if ( markDir == 1 ) {
            markPoint.y += markVelocity
            markPath.moveTo(markPoint.x,markPoint.y)
            // マーカの下端がビューの下端に達していない場合
            // "マーカの上端"～"マーカの下端"を描画
            if (markPoint.y+markLen < h.toFloat()) {
                markPath.lineTo(markPoint.x, markPoint.y+markLen)
            }
            // マーカの下端がビューの下端に達している場合
            // (1) "マーカの下端"～"ビューの下端"を描画
            // (2) "ビューの左下角"～"ビューの下端残り"を描画
            else {
                markPath.lineTo(markPoint.x, h.toFloat())
                markPath.lineTo(w.toFloat()-(markLen-(h.toFloat()-markPoint.y)), h.toFloat())
            }

            // マーカの始点が右下角に到達したらマーカの移動方向を"右⇒左"に変更する
            // マーカの始点は、ビューの右下角に合わせる
            if (markPoint.y >= h.toFloat()) {
                markDir = 2
                markPoint.x = w.toFloat()
                markPoint.y = h.toFloat()
            }
        }
        // 右⇒左
        else if ( markDir == 2 ) {
            markPoint.x -= markVelocity
            markPath.moveTo(markPoint.x,markPoint.y)
            // マーカの左端がビューの左端に達していない場合
            // "マーカの右端"～"マーカの左端"を描画
            if (markPoint.x-markLen > 0) {
                markPath.lineTo(markPoint.x-markLen, markPoint.y)
            }
            // マーカの左端がビューの左端に達している場合
            // (1) "マーカの左端"～"ビューの左端"を描画
            // (2) "ビューの左下角"～"ビューの左端残り"を描画
            else {
                markPath.lineTo(0f, h.toFloat())
                markPath.lineTo(0f,h.toFloat()-(markLen-markPoint.x))
            }

            // マーカの始点が右上角に到達したらマーカの移動方向を"上⇒下"に変更する
            // マーカの始点は、ビューの右上角に合わせる
            if (markPoint.x <= 0) {
                markDir = 3
                markPoint.x = 0f
                markPoint.y = h.toFloat()
            }
        }
        // 下⇒上
        else if ( markDir == 3 ) {
            markPoint.y -= markVelocity
            markPath.moveTo(markPoint.x,markPoint.y)
            // マーカの上端がビューの上端に達していない場合
            // "マーカの下端"～"マーカの上端"を描画
            if (markPoint.y-markLen > 0) {
                markPath.lineTo(markPoint.x, markPoint.y-markLen)
            }
            // マーカの上端がビューの上端に達している場合
            // (1) "マーカの下端"～"ビューの上端"を描画
            // (2) "ビューの右上角"～"ビューの右端残り"を描画
            else {
                markPath.lineTo(markPoint.x, 0f)
                markPath.lineTo(markLen-markPoint.y,0f)
            }

            // マーカの始点が右上角に到達したらマーカの移動方向を"左⇒右"に変更する
            // マーカの始点は、ビューの右上角に合わせる
            if (markPoint.y <= 0) {
                markDir = 0
                markPoint.x = 0f
                markPoint.y = 0f
            }
        }
    }

    // ---------------------------------------------
    // マーカ(長方形)の移動－ネオン効果
    // ---------------------------------------------
    private fun moveNeon() {
        markRectPos += markVelocity
        // マーカ(長方形)の座標位置が
        // 移動可能範囲を超えたら初期値に戻す
        if ( markRectPos >= markRectMoveLen ) {
            markRectPos = 0f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if ( canvas == null ) return
        //Log.d( javaClass.simpleName, "onDraw")

        // 枠の大きさを取得
        val frameBounds = Rect(0,0,width,height)

        // 枠を描画
        canvas.drawRect(frameBounds,paintFrame)

        // マーカを描画
        if ( animated ) {
            // ---------------------------------------------
            // マーカ(長方形)を描画
            // ---------------------------------------------
            drawNeon(canvas)

            // ---------------------------------------------
            // マーカ(枠)を描画
            // ---------------------------------------------
            // Pathを後に描かないと
            // なぜかバックグラウンドが塗りつぶされてしまう。
            drawFrame(canvas)
        }
    }

    // ---------------------------------------------
    // マーカ(枠)を描画
    // ---------------------------------------------
    private fun drawFrame(canvas: Canvas) {
        canvas.drawPath(markPath,paintMark)
    }

    // ---------------------------------------------
    // マーカ(長方形)を描画
    // ---------------------------------------------
    private fun drawNeon(canvas: Canvas) {
        // 座標位置を保存
        canvas.save()

        // 描画したマーカ(長方形)の数
        var drawCnt = 0
        // 座標位置を移動する
        canvas.translate(markRectPos,0f)
        do {
            for ( i in 0 until markRectLst.size ) {
                val paintFill = when (i%2) {
                    // 濃い
                    0 -> paintMarkFill1
                    // 薄い
                    1 -> paintMarkFill2
                    else -> paintMarkFill1
                }
                canvas.drawRect( markRectLst[i], paintFill )
            }
            // "描画したマーカ(長方形)の数"をインクリメントする
            drawCnt += markRectLst.size
            // 次の座標位置に移動する
            canvas.translate(markRectMoveLen/2*markRectLst.size,0f)
            //canvas.translate(markRectMoveLen*markRectLst.size,0f)
        //} while ( drawCnt < markRectSplitCnt )
        } while ( drawCnt < (markRectSplitCnt+2) )

        // 座標位置を戻す
        canvas.restore()
    }
}
