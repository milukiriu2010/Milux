package milu.kiriu2010.milux.gui.lux05

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.Facility

class FacSpinAdapter(
        context: Context,
        // 施設リスト
        val facLst: List<Facility> = listOf())
    : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    private class ViewHolder( view: View ) {
        // 施設名を表示するビュー
        val dataFname = view.findViewById<TextView>(R.id.dataFname)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        var viewHolder: ViewHolder?

        if ( convertView != null ) {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        else {
            view = inflater.inflate(R.layout.spin_fac, null )
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }

        // 表示する施設
        val fac = facLst[position]

        // 施設名を設定
        viewHolder.dataFname.text = fac.fname

        return view
    }

    override fun getItem(position: Int) = facLst[position]

    override fun getItemId(position: Int): Long = 0

    override fun getCount() = facLst.size

}