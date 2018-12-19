package milu.kiriu2010.milux.gui.sun


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.*
import milu.kiriu2010.util.LimitedArrayList

class Lux07SunViewFragment : Fragment()
    , NewVal01Listener
    , NewVal02Listener
    , OrientationListener
    , ResetListener
    , SelectedListener {

    // 照度最大
    private var luxMax: Float = 0f

    // 照度現在
    private var luxNow: Float = 0f

    // 方位角(照度最大)
    private var azimuthMax: Float = 0f

    // 方位角(照度現在)
    private var azimuthNow: Float = 0f

    // 傾斜角(照度最大)
    private var pitchMax: Float = 0f

    // 傾斜角(照度現在)
    private var pitchNow: Float = 0f

    // 回転角(照度最大)
    private var rollMax: Float = 0f

    // 回転角(照度現在)
    private var rollNow: Float = 0f

    // このフラグメントが選択されたかどうか
    private var selected = true

    // 方位角(照度最大)を表示するビュー
    private lateinit var dataAzimuthMax: TextView

    // 方位角(照度現在)を表示するビュー
    private lateinit var dataAzimuthNow: TextView

    // 傾斜角(照度最大)を表示するビュー
    private lateinit var dataPitchMax: TextView

    // 傾斜角(照度現在)を表示するビュー
    private lateinit var dataPitchNow: TextView

    // 回転角(照度最大)を表示するビュー
    private lateinit var dataRollMax: TextView

    // 回転角(照度現在)を表示するビュー
    private lateinit var dataRollNow: TextView

    // 照度最大を表示するビュー
    private lateinit var dataLuxMax: TextView

    // 照度現在を表示するビュー
    private lateinit var dataLuxNow: TextView

    // 方位を示すビュー
    private lateinit var imageViewFlight: ImageView

    // 現在の方向
    private var currentDegree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux07_sun_view, container, false)

        // 方位角(照度最大)を表示するビュー
        dataAzimuthMax = view.findViewById(R.id.dataAzimuthMax)

        // 方位角(照度現在)を表示するビュー
        dataAzimuthNow = view.findViewById(R.id.dataAzimuthNow)

        // 傾斜角(照度最大)を表示するビュー
        dataPitchMax = view.findViewById(R.id.dataPitchMax)

        // 傾斜角(照度現在)を表示するビュー
        dataPitchNow = view.findViewById(R.id.dataPitchNow)

        // 回転角(照度最大)を表示するビュー
        dataRollMax = view.findViewById(R.id.dataRollMax)

        // 回転角(照度現在)を表示するビュー
        dataRollNow = view.findViewById(R.id.dataRollNow)

        // 照度最大を表示するビュー
        dataLuxMax = view.findViewById(R.id.dataLuxMax)
        dataLuxMax.text = luxMax.toString()

        // 照度現在を表示するビュー
        dataLuxNow = view.findViewById(R.id.dataLuxNow)
        dataLuxNow.text = luxNow.toString()

        // 方位を示すビュー
        imageViewFlight = view.findViewById(R.id.imageViewFlight)

        return view
    }

    // NewVal01Listener
    // 新しい照度を設定
    override fun onUpdate(lux: Float) {
    }

    // ------------------------------------
    // NewVal01Listener
    // 照度値の配列を渡す
    // ------------------------------------
    override fun onUpdate( luxLst: LimitedArrayList<LuxData>) {
    }

    // ------------------------------------
    // NewVal02Listener
    // 新しい"照度値"
    // "端末の姿勢値"
    // "方位"
    // を渡す
    // ------------------------------------
    override fun onUpdate(luxData: LuxData, attitude: FloatArray, orientation: FloatArray) {
        // 照度現在
        luxNow = luxData.lux

        // 方位角(照度現在)：ラジアン⇒度
        azimuthNow = Math.toDegrees(attitude[0].toDouble()).toFloat()

        // 傾斜角(照度現在)：ラジアン⇒度
        pitchNow = Math.toDegrees(attitude[1].toDouble()).toFloat()

        // 回転角(照度現在)：ラジアン⇒度
        rollNow = Math.toDegrees(attitude[2].toDouble()).toFloat()

        // 方位
        //val nextDegree = (Math.toDegrees(orientation[0].toDouble())+360.0).toFloat()%360
        val nextDegree = Math.toDegrees(orientation[0].toDouble()).toFloat()

        if ( luxNow > luxMax ) {
            // 照度最大
            luxMax = luxNow
            // 方位角(照度最大)
            azimuthMax = azimuthNow
            // 傾斜角(照度最大)
            pitchMax = pitchNow
            // 回転角(照度最大)
            rollMax = rollNow
        }

        // 照度最大を表示
        if (this::dataLuxMax.isInitialized) {
            dataLuxMax.text = luxMax.toString()
        }
        // 照度現在を表示
        if (this::dataLuxNow.isInitialized) {
            dataLuxNow.text = luxNow.toString()
        }
        // 方位角(照度最大)を表示
        if (this::dataAzimuthMax.isInitialized) {
            dataAzimuthMax.text = "%3.1f".format(azimuthMax)
        }
        // 方位角(照度現在)を表示
        if (this::dataAzimuthNow.isInitialized) {
            dataAzimuthNow.text = "%3.1f".format(azimuthNow)
        }
        // 傾斜角(照度最大)を表示
        if (this::dataPitchMax.isInitialized) {
            dataPitchMax.text = "%3.1f".format(pitchMax)
        }
        // 傾斜角(照度現在)を表示
        if (this::dataPitchNow.isInitialized) {
            dataPitchNow.text = "%3.1f".format(pitchNow)
        }
        // 回転角(照度最大)を表示
        if (this::dataRollMax.isInitialized) {
            dataRollMax.text = "%3.1f".format(rollMax)
        }
        // 回転角(照度現在)を表示
        if (this::dataRollNow.isInitialized) {
            dataRollNow.text = "%3.1f".format(rollNow)
        }

        // 方位を表示
        if (this::imageViewFlight.isInitialized) {
            val animation = RotateAnimation(currentDegree,
                    -nextDegree,
                    // 自分のサイズの割合、0.5fが画像の中心
                    Animation.RELATIVE_TO_SELF,
                    // 回転軸のX座標
            0.5f,
                    Animation.RELATIVE_TO_SELF,
                    // 回転軸のY座標
            0.5f
                    )

            animation.duration = 250
            // アニメーション終了時にviewをそのまま残す
            animation.fillAfter = true

            // アニメーション開始
            imageViewFlight.startAnimation(animation)
            // 現在の方位を設定
            currentDegree = -nextDegree
        }
    }

    // OrientationListener
    override fun onActivityOrientation(): Int {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // ResetListener
    // メンバ変数を初期化する
    override fun OnReset() {
    }

    // SelectedListener
    override fun onSelected(selected: Boolean) {
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                Lux07SunViewFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
