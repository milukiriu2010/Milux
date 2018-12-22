package milu.kiriu2010.milux.gui.overview


import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
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
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 * Use the [Lux01OverViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux01OverViewFragment : Fragment()
        , NewVal01Listener
        , OrientationListener
        , ResetListener
        , SelectedListener {

    // 照度
    private var lux: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView
    // 照度の強さを表すビュー
    private lateinit var overView: ImageView
    // 照度の強さを描画するdrawable
    private lateinit var drawable: LuxOverViewDrawable

    // このフラグメントが選択されたかどうか
    private var selected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //Log.d( javaClass.simpleName, "onCreateView" )
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux01_over_view, container, false)

        // アプリ設定を取得
        val appl = context?.applicationContext as? LuxApplication
        val appConf = appl?.appConf

        // 照度の強さを表示するサーフェスビュー
        overView = view.findViewById(R.id.imageViewOver)
        drawable = LuxOverViewDrawable(appConf!!,resources)
        overView.setImageDrawable(drawable)

        // 描画
        drawable.invalidateSelf()

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        return view
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
        if (this::drawable.isInitialized) {
            drawable.onUpdate(lux)
        }
    }

    // NewVal01Listener
    // 照度値の配列を渡す
    override fun onUpdate( luxLst: LimitedArrayList<LuxData>) {
    }

    // OrientationListener
    override fun onActivityOrientation(): Int {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // ResetListener
    // メンバ変数を初期化する
    override fun OnReset() {
        // 描画領域を初期化
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
                Lux01OverViewFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
