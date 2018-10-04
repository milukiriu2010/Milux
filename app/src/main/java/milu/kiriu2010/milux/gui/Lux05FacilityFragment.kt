package milu.kiriu2010.milux.gui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.util.LimitedArrayList
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [Lux05FacilityFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux05FacilityFragment : Fragment()
        , NewVal01Listener
        , ResetListener {

    // 照度
    private var lux: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView

    // 施設エリアのリストを表示するビュー
    private lateinit var recyclerViewFacArea: RecyclerView

    // 施設エリアを表示するためのアダプタ
    private lateinit var adapter: FacAreaRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux05_facility, container, false)

        if ( context == null ) return view

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        // 施設リスト
        val appl = context?.applicationContext as? LuxApplication
        if ( appl != null ) {
            Log.d(javaClass.simpleName, "facilityLst:[${appl.appConf.facilityLst.size}]")
        }

        // 施設エリアのリストを表示するビュー
        recyclerViewFacArea = view.findViewById(R.id.recyclerViewFacArea)

        // 施設エリアのリストを縦に並べる
        recyclerViewFacArea.layoutManager = LinearLayoutManager( context, LinearLayoutManager.VERTICAL, false)

        // 施設エリアを表示するためのアダプタ
        adapter = FacAreaRecyclerAdapter(context!!)
        recyclerViewFacArea.adapter = adapter

        return view
    }

    // NewVal01Listener
    override fun onUpdate(lux: Float) {
        this.lux = lux

        // 照度の数値を表示
        if (this::dataLux.isInitialized) {
            //Log.d( javaClass.simpleName, "dataLux already initialized")
            dataLux.text = "%.1f lx".format(this.lux)
        }


    }

    // NewVal01Listener
    override fun onUpdate(luxLst: LimitedArrayList<LuxData>) {

    }

    // ResetListener
    override fun OnReset() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Lux05FacilityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                Lux05FacilityFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
