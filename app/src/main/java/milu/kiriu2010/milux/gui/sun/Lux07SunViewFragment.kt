package milu.kiriu2010.milux.gui.sun


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.OrientationListener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.milux.gui.SelectedListener
import milu.kiriu2010.util.LimitedArrayList

class Lux07SunViewFragment : Fragment()
    , NewVal01Listener
    , OrientationListener
    , ResetListener
    , SelectedListener {

    // 照度
    private var lux: Float = 0f

    // このフラグメントが選択されたかどうか
    private var selected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux07_sun_view, container, false)





        return view
    }


    // NewVal01Listener
    // 新しい照度を設定
    override fun onUpdate(lux: Float) {
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
