package milu.kiriu2010.milux.gui.facility


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.conf.AppConf
import milu.kiriu2010.milux.entity.Facility
import milu.kiriu2010.milux.entity.FacilityArea
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.misc.ConfFragment
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.util.LimitedArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Lux05FacilityFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux05FacilityFragment : Fragment()
        , NewVal01Listener
        , ResetListener
        , ConfFragment.OnUpdateConfListener {

    // 照度
    private var lux: Float = 0f

    // 照度の数値を表示するビュー
    private lateinit var dataLux: TextView

    // 表示する施設を選択するスピン
    private lateinit var spinFacility: Spinner

    // 施設エリアのリストを表示するビュー
    private lateinit var recyclerViewFacArea: RecyclerView

    // 施設エリアを表示するためのアダプタ
    private lateinit var adapterFacArea: FacAreaRecyclerAdapter

    // アプリ設定
    private lateinit var appConf: AppConf

    // 照度リスト
    private var luxArray = arrayOf(1000,900,800,700,600,500,400,300,200,150,100,75,20,0)

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

        val ctx = context ?: return view

        // 照度の数値を表示するビュー
        dataLux = view.findViewById(R.id.dataLux)

        // 施設リスト
        val appl = context?.applicationContext as? LuxApplication
        appConf = appl?.appConf ?: AppConf()

        // 表示する施設を選択するスピン
        spinFacility = view.findViewById<Spinner>(R.id.spinFacility)

        // 施設リストのテンプレートを構築
        //val facLst = createFacLst()
        val facLst = appConf.createFacLst()

        // 施設を選択するスピンにアダプタを設定する
        val adapterFac = FacSpinAdapter(ctx,facLst)
        spinFacility.adapter = adapterFac

        // 施設を選択するスピンのデフォルト選択を設定する
        val fac = facLst.filter { it.fid == appConf.fid }.first()
        spinFacility.setSelection(facLst.indexOf(fac))
        //Log.d(javaClass.simpleName, "fac:index:[${facLst.indexOf(fac)}]")

        // 施設スピンの選択を変更
        spinFacility.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 選択された施設
                val selectedFac = parent?.selectedItem as Facility
                // 施設のエリアを表示するビューを更新
                adapterFacArea.facAreaLst.clear()
                createFacAreaLst(selectedFac.fid).forEach {
                    adapterFacArea.facAreaLst.add(it)
                }
                adapterFacArea.notifyDataSetChanged()
            }
        }

        // 施設エリアのリストを構築
        val facilityAreaLst = createFacAreaLst(appConf.fid)

        // 施設エリアのリストを表示するビュー
        recyclerViewFacArea = view.findViewById(R.id.recyclerViewFacArea)

        // 施設エリアのリストを縦に並べる
        recyclerViewFacArea.layoutManager = LinearLayoutManager( ctx, LinearLayoutManager.VERTICAL, false)

        // 施設エリアを表示するためのアダプタ
        adapterFacArea = FacAreaRecyclerAdapter(ctx, facilityAreaLst)
        recyclerViewFacArea.adapter = adapterFacArea

        return view
    }

    /*
    // 施設リストのテンプレートを構築
    private fun createFacLst(): List<Facility> {
        Log.d( javaClass.simpleName, "createFacLst")
        // 施設エリアのリストからfidを抽出する。
        // fidは重複しているため、重複を除く
        val fidLst = appConf.facilityAreaLst.map { it -> it.fid }.distinct()

        // 上で取得されたfidのリストから
        // 施設リストを抽出する
        val facLst = appConf.facilityLst.filter {
            fidLst.contains(it.fid)
        }

        return facLst
    }
    */

    // 施設エリアリストのテンプレートを構築
    //   fid: 施設ID
    private fun createFacAreaLst( fid: Int ): MutableList<FacilityArea> {
        // 照度リストを更新
        luxArray = when (fid) {
            // 5:commercial facility
            5 ->  resources.getIntArray(R.array.FacilityLuxPattern02).toTypedArray()
            // 2:factory
            2 ->  resources.getIntArray(R.array.FacilityLuxPattern03).toTypedArray()
            // 10:parking lot
            10 ->  resources.getIntArray(R.array.FacilityLuxPattern04).toTypedArray()
            else -> resources.getIntArray(R.array.FacilityLuxPattern01).toTypedArray()
        }

        // JSONに格納されている施設エリアリスト
        val facAreaLst = appConf.facilityAreaLst.filter { it.fid == fid }

        // 施設エリアリストのテンプレートを構築
        val facAreaOrgLst = mutableListOf<FacilityArea>()
        for ( lux in luxArray ) {
            val facArea = facAreaLst.filter { it.lux == lux }.firstOrNull()
                    ?: FacilityArea( fid, mutableListOf(), lux )
            facAreaOrgLst.add(facArea)
        }

        return facAreaOrgLst
    }

    // アダプタのハイライト位置を更新する
    private fun updateAdapterHighlight() {
        if (this::adapterFacArea.isInitialized == false) return

        var luxMin = adapterFacArea.luxMin

        var highLightPos = -1
        for ( i in 0 until luxArray.size ) {
            if ( lux > luxArray[i] ) {
                //Log.d(javaClass.simpleName, "adapter:lux[${lux}]i[$i]luxArray[${luxArray[i]}]")
                adapterFacArea.luxMin = luxArray[i].toFloat()
                adapterFacArea.luxMax = when ( i ) {
                    0 -> adapterFacArea.luxMin+100f
                    else -> luxArray[i-1].toFloat()
                }
                highLightPos = i
                break
            }
        }
        //Log.d(javaClass.simpleName, "adapter:lux[${lux}]min[${adapterFacArea.luxMin}]max[${adapterFacArea.luxMax}]")

        /**/
        // ハイライト位置が変わる場合、
        // アダプタに更新をかける
        if ( luxMin != adapterFacArea.luxMin ) {
            // うまく動かない
            //adapter.notifyDataSetChanged()
            // 前回のハイライトを消去
            if ( adapterFacArea.highLightPos != -1 ) {
                adapterFacArea.notifyItemChanged(adapterFacArea.highLightPos)
            }
            // 今回のハイライトを設定
            if ( highLightPos != -1 ) {
                adapterFacArea.notifyItemChanged(highLightPos)
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
        //Log.d( javaClass.simpleName, "onUpdate[$lux]")
        this.lux = lux

        // 照度の数値を表示
        if (this::dataLux.isInitialized) {
            //Log.d( javaClass.simpleName, "dataLux already initialized")
            dataLux.text = "%.1f lx".format(this.lux)
        }

        // アダプタのハイライト位置を更新する
        updateAdapterHighlight()
    }

    // NewVal01Listener
    override fun onUpdate(luxLst: LimitedArrayList<LuxData>) {

    }

    // ResetListener
    override fun OnReset() {

    }

    // ConfFragment.OnUpdateConfListener
    // 設定が更新されると呼び出される
    override fun updateConf() {
        // 施設リスト
        val appl = context?.applicationContext as? LuxApplication
        appConf = appl?.appConf ?: AppConf()

        // 施設リストのテンプレートを構築
        //val facLst = createFacLst()
        val facLst = appConf.createFacLst()

        if (this::spinFacility.isInitialized == false) return

        // 施設を選択するスピンのデフォルト選択を設定する
        val fac = facLst.filter { it.fid == appConf.fid }.first()
        spinFacility.setSelection(facLst.indexOf(fac))
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
