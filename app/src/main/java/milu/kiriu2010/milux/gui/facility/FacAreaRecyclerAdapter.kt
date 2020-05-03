package milu.kiriu2010.milux.gui.facility

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import milu.kiriu2010.gui.decorate.Deco03ConstraintLayout
import milu.kiriu2010.milux.R
import milu.kiriu2010.milux.entity.FacilityArea

class FacAreaRecyclerAdapter(
        context: Context,
        // 施設エリアのリスト
        val facAreaLst: MutableList<FacilityArea> = mutableListOf(),
        // 施設エリア名をアニメーションするための照度MIN値
        var luxMin: Float = 100000f,
        // 施設エリア名をアニメーションするための照度MAX値
        var luxMax: Float = 100000f
    ): androidx.recyclerview.widget.RecyclerView.Adapter<FacAreaRecyclerAdapter.FacAreaViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    // ハイライトされる位置
    var highLightPos: Int = -1

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): FacAreaViewHolder {
        val view = inflater.inflate(R.layout.list_row_facarea, parent, false )
        val viewHolder = FacAreaViewHolder(view)
        return viewHolder
    }

    override fun getItemCount() = facAreaLst.size

    override fun onBindViewHolder(holder: FacAreaViewHolder, pos: Int) {
        // 施設エリア
        val facArea = facAreaLst[pos]
        //Log.d(javaClass.simpleName, "onBindViewHolder:pos[$pos]lux[${facArea.lux}]min[$luxMin]max[$luxMax]")

        // 照度
        holder.dataLux.text = "%1d lx".format(facArea.lux)
        // 施設内のエリア名
        holder.dataArea.text = facArea.anameLst.joinToString("\n")
        /*
        // 照度がMIN-MAXの範囲にある場合、
        // 施設内のエリア名をアニメーションする
        holder.dataArea.animated = if ( (facArea.lux >= luxMax) or (facArea.lux < luxMin) ) {
            false
        }
        else {
            //Log.d( javaClass.simpleName, "animate[$pos]")
            // ハイライト位置を設定
            highLightPos = pos
            // アニメーションを開始
            holder.dataArea.kickRunnable()
            true
        }
        */
        // 照度がMIN-MAXの範囲にある場合、
        // 親レイアウトをアニメーションする
        holder.decoLayout.animated = if ( (facArea.lux >= luxMax) or (facArea.lux < luxMin) ) {
            false
        }
        else {
            //Log.d( javaClass.simpleName, "animate[$pos]")
            // ハイライト位置を設定
            highLightPos = pos
            // アニメーションを開始
            holder.decoLayout.kickRunnable()
            true
        }
    }

    class FacAreaViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // 親レイアウト
        val decoLayout = view.findViewById<Deco03ConstraintLayout>(R.id.decoLayout)
        // 照度
        val dataLux = view.findViewById<TextView>(R.id.dataLux)
        // 施設内のエリア名
        //val dataArea = view.findViewById<DecorateTextView>(R.id.dataArea)
        val dataArea = view.findViewById<TextView>(R.id.dataArea)
    }
}