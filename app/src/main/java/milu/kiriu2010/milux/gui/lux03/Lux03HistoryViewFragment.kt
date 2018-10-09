package milu.kiriu2010.milux.gui.lux03


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.util.LimitedArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Lux03HistoryViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux03HistoryViewFragment : Fragment()
        , NewVal01Listener
        , ResetListener {

    // 照度値の履歴を表示するビュー
    private lateinit var recyclerViewLux: RecyclerView

    // 照度値を表示するためのアダプタ
    private lateinit var adapter: LuxRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lux03_history_view, container, false)

        if ( context == null ) return view

        // 照度値の履歴を表示するビュー
        recyclerViewLux = view.findViewById(R.id.recyclerViewLux)

        // 照度値の履歴を縦に並べる
        recyclerViewLux.layoutManager = LinearLayoutManager( context, LinearLayoutManager.VERTICAL, false )

        // 照度値を表示するためのアダプタ
        adapter = LuxRecyclerAdapter(context!!)
        recyclerViewLux.adapter = adapter

        return view
    }

    // NewVal01Listener
    override fun onUpdate(lux: Float) {
    }

    // NewVal01Listener
    override fun onUpdate(luxLst: LimitedArrayList<LuxData>) {
        if ( luxLst.size <= 0 ) return
        // 照度値を表示するアダプタにデータを追加し、更新を通知する
        if (this::adapter.isInitialized) {
            // アプリ設定を取得
            val appl = context?.applicationContext as? LuxApplication
            val appConf = appl?.appConf ?: return
            // アダプタに格納されているデータ数
            var cnt = adapter.itemCount

            // アダプタに格納されているデータ数がアプリ設定を超える場合は消去する
            while ( cnt > appConf.limit ) {
                adapter.luxDataLst.removeAt(cnt-1)
                adapter.notifyItemRemoved(cnt-1)
                cnt = adapter.itemCount
            }

            // アダプタにすでに履歴がある場合は、１つのみ先頭に追加する
            if ( cnt > 0 ) {
                adapter.luxDataLst.add(0, luxLst[0])
                adapter.notifyItemInserted(0)
            }
            // アダプタに履歴がない場合は、全データを追加する
            else {
                adapter.luxDataLst.addAll(luxLst)
                adapter.notifyItemRangeInserted(0, luxLst.size)
            }

            // 履歴の先頭を表示するようにスクロールする
            recyclerViewLux.scrollToPosition(0)
        }
    }

    // ResetListener
    override fun OnReset() {
        // 照度値を表示するアダプタをクリアする
        if (this::adapter.isInitialized) {
            val cnt = adapter.itemCount
            adapter.luxDataLst.clear()
            adapter.notifyItemRangeRemoved( 0, cnt )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Lux03HistoryViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                Lux03HistoryViewFragment().apply {
                    arguments = Bundle().apply {
                        /*
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        */
                    }
                }
    }
}
