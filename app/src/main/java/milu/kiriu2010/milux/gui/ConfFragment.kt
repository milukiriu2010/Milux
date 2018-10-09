package milu.kiriu2010.milux.gui


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R

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

    // デフォルトボタン
    private lateinit var btnDefault: Button

    // OKボタン
    private lateinit var btnOK: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
        val samplingNumLst = (10..200 step 10).toMutableList()
        val adapterSamplingNum = ArrayAdapter<Int>(ctx, android.R.layout.simple_list_item_1, samplingNumLst )
        spinSamplingNum.adapter = adapterSamplingNum
        spinSamplingNum.setSelection(samplingNumLst.indexOf(appConf.limit-1))

        // 施設を選択するスピン
        spinFacility = view.findViewById(R.id.spinFacility)

        // デフォルトボタン
        btnDefault = view.findViewById(R.id.btnDefault)

        // OKボタン
        btnOK = view.findViewById(R.id.btnOK)
        btnOK.setOnClickListener {
            dismiss()
        }



        return view
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
