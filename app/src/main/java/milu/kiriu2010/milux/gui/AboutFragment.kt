package milu.kiriu2010.milux.gui


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

import milu.kiriu2010.milux.R

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AboutFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    /*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialg = super.onCreateDialog(savedInstanceState)

        // タイトル
        //dialg.setTitle("xxx")
        // ダイアログ枠外タップで消えないようにする
        //dialg.setCanceledOnTouchOutside(false)

        return dialog
    }
    */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // バージョンを表示
        val textViewVer = view.findViewById<TextView>(R.id.textViewVer)
        val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
        textViewVer.text = "ver %s".format(packageInfo?.versionName)

        // OKボタン
        val btnOK = view.findViewById<Button>(R.id.btnOK)
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
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                AboutFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
