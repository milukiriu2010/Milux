package milu.kiriu2010.milux.gui.list


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.*
import milu.kiriu2010.milux.LuxApplication

import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import milu.kiriu2010.milux.gui.NewVal01Listener
import milu.kiriu2010.milux.gui.ResetListener
import milu.kiriu2010.util.LimitedArrayList
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [Lux03HistoryViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Lux03HistoryViewFragment : androidx.fragment.app.Fragment()
        , NewVal01Listener
        , ResetListener {

    // 照度値の履歴を表示するビュー
    private lateinit var recyclerViewLux: androidx.recyclerview.widget.RecyclerView

    // 照度値を表示するためのアダプタ
    private lateinit var adapter: LuxRecyclerAdapter

    // 表示するリストを自動更新するかどうか
    private var autoUpdate = true

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
        recyclerViewLux.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)

        // 照度値を表示するためのアダプタ
        adapter = LuxRecyclerAdapter(context!!)
        recyclerViewLux.adapter = adapter

        // オプションメニューを表示
        setHasOptionsMenu(true)

        return view
    }

    // NewVal01Listener
    override fun onUpdate(lux: Float) {
    }

    // NewVal01Listener
    override fun onUpdate(luxLst: LimitedArrayList<LuxData>) {
        // 自動更新"不可"の場合は、すぐ終了
        if ( autoUpdate == false ) return
        // リストがない場合は、すぐ終了
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

    // このフラグメント用のメニューを作成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list,menu)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // 自動更新の挙動を変更する
            R.id.action_update -> {
                // 自動更新: 可 => 不可
                if ( autoUpdate ) {
                    // 自動更新: 可 => 不可
                    autoUpdate = false
                    // アイコン変更: pause => resume
                    item.icon = resources.getDrawable(R.drawable.svg_resume,null)
                    // メニュータイトル変更: pause => resume
                    item.title = resources.getString(R.string.action_resume)
                }
                // 自動更新: 不可 => 可
                else {
                    // リストを一旦クリアする
                    OnReset()


                    // 自動更新: 不可 => 可
                    autoUpdate = true
                    // アイコン変更: resume => pause
                    item.icon = resources.getDrawable(R.drawable.svg_pause,null)
                    // メニュータイトル変更: resume => pause
                    item.title = resources.getString(R.string.action_pause)
                }
                true
            }
            // データをアップロード
            R.id.action_upload -> {
                val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ssz")
                // タイムゾーンをローカルに変更
                dateFmt.timeZone = TimeZone.getDefault()
                // アダプタに格納されたデータをCSV形式に変換する
                val strCSV = adapter.luxDataLst.joinToString(separator="\n",postfix = "\n") {
                    dateFmt.format(it.t) + "," + it.lux
                }
                Log.d(javaClass.simpleName, "strCSV[$strCSV]")

                val intent = Intent().apply{
                    action = Intent.ACTION_SEND
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TEXT,strCSV)
                }

                // アップロードを実行するアプリを呼び出す
                startActivity(Intent.createChooser(intent, resources.getText(R.string.action_upload)))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
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
