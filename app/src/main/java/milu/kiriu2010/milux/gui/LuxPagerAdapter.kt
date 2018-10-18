package milu.kiriu2010.milux.gui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import milu.kiriu2010.milux.gui.graph.Lux02GraphViewFragment
import milu.kiriu2010.milux.gui.graph.Lux04GraphViewFragment
import milu.kiriu2010.milux.gui.list.Lux03HistoryViewFragment
import milu.kiriu2010.milux.gui.facility.Lux05FacilityFragment
import milu.kiriu2010.milux.gui.overview.Lux01OverViewFragment
import milu.kiriu2010.milux.gui.overview.Lux06OverViewFragment

//class LuxPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
class LuxPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private lateinit var lux01OverViewFragment: Lux01OverViewFragment
    private lateinit var lux06OverViewFragment: Lux06OverViewFragment
    private lateinit var lux05FacilityFragment: Lux05FacilityFragment
    private lateinit var lux02GraphViewFragment: Lux02GraphViewFragment
    private lateinit var lux04GraphViewFragment: Lux04GraphViewFragment
    private lateinit var lux03HistoryViewFragment: Lux03HistoryViewFragment

    // ページ数
    private val pageCnt = 4

    override fun getItem(pos: Int): Fragment {
        //return when (pos%pageCnt) {
        return when (pos) {
            0 -> {
                if ( !this::lux06OverViewFragment.isInitialized ) {
                    lux06OverViewFragment = Lux06OverViewFragment.newInstance()
                }
                return lux06OverViewFragment
            }
            1 -> {
                if ( !this::lux05FacilityFragment.isInitialized ) {
                    lux05FacilityFragment = Lux05FacilityFragment.newInstance()
                }
                return lux05FacilityFragment
            }
            2 -> {
                if ( !this::lux04GraphViewFragment.isInitialized ) {
                    lux04GraphViewFragment = Lux04GraphViewFragment.newInstance()
                }
                return lux04GraphViewFragment
            }
            3 -> {
                if ( !this::lux03HistoryViewFragment.isInitialized ) {
                    lux03HistoryViewFragment = Lux03HistoryViewFragment.newInstance()
                }
                return lux03HistoryViewFragment
            }
            4 -> {
                if ( !this::lux01OverViewFragment.isInitialized ) {
                    lux01OverViewFragment = Lux01OverViewFragment.newInstance()
                }
                return lux01OverViewFragment
            }
            else -> {
                if ( !this::lux06OverViewFragment.isInitialized ) {
                    lux06OverViewFragment = Lux06OverViewFragment.newInstance()
                }
                return lux06OverViewFragment
            }
        }
    }

    override fun getCount(): Int = pageCnt
    //override fun getCount(): Int = Int.MAX_VALUE

    /*
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page" + position
    }
    */
}