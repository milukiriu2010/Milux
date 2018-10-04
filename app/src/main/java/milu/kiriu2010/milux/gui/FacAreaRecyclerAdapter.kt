package milu.kiriu2010.milux.gui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.FacilityArea

class FacAreaRecyclerAdapter(
        context: Context,
        // 施設エリアのリスト
        val facAreaLst: MutableList<FacilityArea> = mutableListOf()
    ): RecyclerView.Adapter<FacAreaRecyclerAdapter.FacAreaViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): FacAreaViewHolder {
        val view = inflater.inflate(R.layout.list_row_facarea, parent, false )
        val viewHolder = FacAreaViewHolder(view)
        return viewHolder
    }

    override fun getItemCount() = facAreaLst.size

    override fun onBindViewHolder(holder: FacAreaViewHolder, pos: Int) {
        // 施設エリア
        val facArea = facAreaLst[pos]

        // 照度
        holder.dataLux.text = facArea.lux.toString()
        // 施設内のエリア名
        holder.dataArea.text = facArea.aname
    }

    class FacAreaViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // 照度
        val dataLux = view.findViewById<TextView>(R.id.dataLux)
        // 施設内のエリア名
        val dataArea = view.findViewById<TextView>(R.id.dataArea)
    }
}