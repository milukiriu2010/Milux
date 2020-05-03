package milu.kiriu2010.gui.decorate

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

class DecorateTextView: TextView {
    // マーカの長さ
    var markLen = 100f
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
    // マーカの移動スピード
    var markVelocity = 10f
    // マーカがアニメーション中かどうか
    var animated = true

    // テキスト描画に使うペイント
    val paintText = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        textSize = 40f
        style = Paint.Style.FILL_AND_STROKE
    }
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
        style = Paint.Style.STROKE
    }

    // マーカを移動するためのハンドラ
    private val markHandler = Handler()
    // マーカを移動するためのスレッド
    private lateinit var markRunnable: Runnable

    // デコレーションのモード
    var mode = 0

    constructor(context: Context?): super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) : super(context, attrs, defStyle, defStyleRes) {
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
        //if ( this::markRunnable.isInitialized == false ) {
        val w = width
        val h = height

        markRunnable = Runnable {
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

            // 描画する
            invalidate()

            // アニメーション中であれば、
            // 描画スレッドをキックする
            if ( animated ) {
                markHandler.postDelayed(markRunnable, 50)
            }
        }
        markHandler.post(markRunnable)
        //}
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if ( canvas == null ) return

        when (mode) {
            0 -> drawMode0(canvas)
        }
    }

    // モード0出描画する
    private fun drawMode0(canvas: Canvas) {
        // 枠の大きさを取得
        val frameBounds = Rect(0,0,width,height)

        // 枠を描画
        canvas.drawRect(frameBounds,paintFrame)

        // マーカを描画
        if ( animated ) canvas.drawPath(markPath,paintMark)
    }
}