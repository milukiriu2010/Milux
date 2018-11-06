package milu.kiriu2010.milux.gui.misc


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.Facility
import milu.kiriu2010.milux.gui.facility.FacSpinAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ConfFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ConfFragment : DialogFragment() {

    // サンプリング数を選択するスピン
    private lateinit var spinSamplingNum: Spinner

    // 施設を選択するスピン
    private lateinit var spinFacility: Spinner

    // スクリーンONスイッチ
    private lateinit var switchScreenOn: Switch

    // デフォルトボタン
    private lateinit var btnDefault: Button

    // OKボタン
    private lateinit var btnOK: Button

    // Cancelボタン
    private lateinit var btnCancel: Button

    // OKボタンを押下したことを通知するために用いるリスナー
    private lateinit var listener: OnUpdateConfListener

    // OKボタンを押下したことを通知するために用いるリスナー
    interface OnUpdateConfListener {
        fun updateConf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnUpdateConfListener) {
            listener = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnUpdateConf")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conf, container, false)

        val ctx = context ?: return view

        // アプリケーションの設定
        val appl = ctx.applicationContext as LuxApplication
        val appConf = appl.appConf

        // サンプリング数を選択するスピン
        spinSamplingNum = view.findViewById(R.id.spinSamplingNum)

        // 施設を選択するスピン
        spinFacility = view.findViewById(R.id.spinFacility)

        // スクリーンONスイッチ
        switchScreenOn = view.findViewById(R.id.switchScreenOn)

        // アプリ設定をビューへ反映
        appConf2View(ctx,appConf)

        // デフォルトボタン
        btnDefault = view.findViewById(R.id.btnDefault)
        btnDefault.setOnClickListener {
            // デフォルト設定にする
            appConf.goDefault()
            // アプリ設定をビューへ反映
            appConf2View(ctx,appConf)
        }

        // OKボタン
        btnOK = view.findViewById(R.id.btnOK)
        btnOK.setOnClickListener {
            // サンプリング数を更新
            appConf.limit = (spinSamplingNum.selectedItem as Int) + 1

            // 表示対象の施設を更新
            appConf.fid = (spinFacility.selectedItem as Facility).fid

            // "スクリーンON"に対応する更新を実施
            appConf.screenOn = switchScreenOn.isChecked
            // ON
            if ( appConf.screenOn ) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            // OFF
            else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }


            // 共有設定へアプリ設定を保存する
            appl.saveSharedPreferences()

            // 設定の更新を通知する
            listener.updateConf()
            dismiss()
        }

        // Cancelボタン
        btnCancel = view.findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dismiss()
        }

        return view
    }

    // アプリ設定の値をビューに反映する
    fun appConf2View(ctx: Context,appConf: AppConf) {
        // サンプリング数を選択するスピン
        val samplingNumLst = (10..200 step 10).toMutableList()
        val adapterSamplingNum = ArrayAdapter<Int>(ctx, android.R.layout.simple_list_item_1, samplingNumLst )
        spinSamplingNum.adapter = adapterSamplingNum
        spinSamplingNum.setSelection(samplingNumLst.indexOf(appConf.limit-1))

        // 施設リストのテンプレートを構築
        val facLst = appConf.createFacLst()

        // 施設を選択するスピンにアダプタを設定する
        val adapterFac = FacSpinAdapter(ctx,facLst)
        spinFacility.adapter = adapterFac

        // 施設を選択するスピンのデフォルト選択を設定する
        val fac = facLst.filter { it.fid == appConf.fid }.first()
        spinFacility.setSelection(facLst.indexOf(fac))

        // スクリーンONスイッチを設定する
        switchScreenOn.isChecked = appConf.screenOn

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // WindowManager.LayoutParams
        val lp = dialog?.window?.attributes ?: return

        // DisplayMetrics
        val metrics = resources.displayMetrics

        // ダイアログの幅だけ端末の幅まで広げる
        lp.width = metrics.widthPixels
        //lp.height = metrics.heightPixels
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ConfFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                ConfFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
