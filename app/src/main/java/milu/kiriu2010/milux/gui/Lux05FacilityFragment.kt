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
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.FacilityArea
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

    // アプリ設定
    private lateinit var appConf: AppConf

    // 表示対象の施設
    // "8:house"を選択
    private var fid = 8

    // 照度リスト
    private val luxArray = arrayOf(1000,900,800,700,600,500,400,300,200,150,100,75,20,0)

    // 施設エリアのリストを構築
    //private lateinit var facilityAreaLst: MutableList<FacilityArea>

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
        appConf = appl?.appConf ?: AppConf()



        // 施設エリアのリストを構築
        val facilityAreaLst = createFacAreaLst(fid)






        // 施設エリアのリストを表示するビュー
        recyclerViewFacArea = view.findViewById(R.id.recyclerViewFacArea)

        // 施設エリアのリストを縦に並べる
        recyclerViewFacArea.layoutManager = LinearLayoutManager( context, LinearLayoutManager.VERTICAL, false)

        // 施設エリアを表示するためのアダプタ
        adapter = FacAreaRecyclerAdapter(context!!,facilityAreaLst)
        recyclerViewFacArea.adapter = adapter

        return view
    }

    // 施設エリアリストのテンプレートを構築
    //   fid: 施設ID
    private fun createFacAreaLst( fid: Int ): MutableList<FacilityArea> {
        // JSONに格納されている施設エリアリスト
        val facAreaLst = appConf.facilityAreaLst.filter { it.fid == fid }


        val facAreaOrgLst = mutableListOf<FacilityArea>()
        for ( lux in luxArray ) {
            val facArea = facAreaLst.filter { it.lux == lux }.firstOrNull()
                    ?: FacilityArea( fid, mutableListOf(), lux )
            facAreaOrgLst.add(facArea)
        }

        return facAreaOrgLst
    }

    private fun updateAdapterMinMax() {
        if (this::adapter.isInitialized == false) return

        var luxMin = adapter.luxMin

        var highLightPos = -1
        for ( i in 0 until  luxArray.size ) {
            if ( lux > luxArray[i] ) {
                Log.d(javaClass.simpleName, "adapter:lux[${lux}]i[$i]luxArray[${luxArray[i]}]")
                adapter.luxMin = luxArray[i].toFloat()
                adapter.luxMax = when ( i ) {
                    0 -> adapter.luxMin+100f
                    else -> luxArray[i-1].toFloat()
                }
                highLightPos = i
                break
            }
        }
        Log.d(javaClass.simpleName, "adapter:lux[${lux}]min[${adapter.luxMin}]max[${adapter.luxMax}]")

        /**/
        // ハイライト位置が変わる場合、
        // アダプタに更新をかける
        if ( luxMin != adapter.luxMin ) {
            // うまく動かない
            //adapter.notifyDataSetChanged()
            // 前回のハイライトを消去
            if ( adapter.highLightPos != -1 ) {
                adapter.notifyItemChanged(adapter.highLightPos)
            }
            // 今回のハイライトを設定
            if ( highLightPos != -1 ) {
                adapter.notifyItemChanged(highLightPos)
            }
        }
        /**/
        /*
        if ( luxMin != 800f ) {
            adapter.luxMax = 900f
            adapter.luxMin = 800f
            luxMin = 800f
            adapter.notifyItemChanged(1)
            adapter.notifyItemChanged(2)
            adapter.notifyItemChanged(10)
        }
        */
    }

    // NewVal01Listener
    override fun onUpdate(lux: Float) {
        this.lux = lux

        // 照度の数値を表示
        if (this::dataLux.isInitialized) {
            //Log.d( javaClass.simpleName, "dataLux already initialized")
            dataLux.text = "%.1f lx".format(this.lux)
        }

        updateAdapterMinMax()
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
