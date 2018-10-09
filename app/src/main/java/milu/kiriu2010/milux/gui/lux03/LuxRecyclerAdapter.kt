package milu.kiriu2010.milux.gui.lux03

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.LuxData
import java.text.SimpleDateFormat
import java.util.*

class LuxRecyclerAdapter(
        context: Context,
        // 照度値の履歴
        val luxDataLst: MutableList<LuxData> = mutableListOf()
        )
    : RecyclerView.Adapter<LuxRecyclerAdapter.LuxDataViewHolder>(){

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): LuxDataViewHolder {
        val view = inflater.inflate(R.layout.list_row_luxdata, parent, false )
        val viewHolder = LuxDataViewHolder(view)
        return viewHolder
    }

    override fun getItemCount() = luxDataLst.size

    override fun onBindViewHolder(holder: LuxDataViewHolder, pos: Int) {
        // 照度データ
        val luxData = luxDataLst[pos]

        // 時刻フォーマット
        val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply {
            // タイムゾーンを自端末に合わせる
            timeZone = TimeZone.getDefault()
        }

        // データ取得時刻
        holder.dataTime.text = dateFmt.format(luxData.t)
        // 照度値
        holder.dataLux.text = "%.1f lx".format(luxData.lux)
    }

    class LuxDataViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // データ取得時刻
        val dataTime = view.findViewById<TextView>(R.id.dataTime)
        // 照度値
        val dataLux = view.findViewById<TextView>(R.id.dataLux)
    }
}