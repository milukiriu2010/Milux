package milu.kiriu2010.milux.gui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

//class LuxPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
class LuxPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    private lateinit var lux01OverViewFragment: Lux01OverViewFragment
    private lateinit var lux02OverViewFragment: Lux02OverViewFragment
    private lateinit var lux03OverViewFragment: Lux03OverViewFragment
    private lateinit var lux04OverViewFragment: Lux04OverViewFragment
    private lateinit var lux05FacilityFragment: Lux05FacilityFragment

    // ページ数
    private val pageCnt = 4

    override fun getItem(pos: Int): Fragment {
        //return when (pos%pageCnt) {
        return when (pos) {
            0 -> {
                if ( !this::lux01OverViewFragment.isInitialized ) {
                    lux01OverViewFragment = Lux01OverViewFragment.newInstance()
                }
                return lux01OverViewFragment
            }
            /*
            1 -> {
                if ( !this::lux02OverViewFragment.isInitialized ) {
                    lux02OverViewFragment = Lux02OverViewFragment.newInstance()
                }
                return lux02OverViewFragment
            }
            */
            1 -> {
                if ( !this::lux05FacilityFragment.isInitialized ) {
                    lux05FacilityFragment = Lux05FacilityFragment.newInstance()
                }
                return lux05FacilityFragment
            }
            2 -> {
                if ( !this::lux04OverViewFragment.isInitialized ) {
                    lux04OverViewFragment = Lux04OverViewFragment.newInstance()
                }
                return lux04OverViewFragment
            }
            3 -> {
                if ( !this::lux03OverViewFragment.isInitialized ) {
                    lux03OverViewFragment = Lux03OverViewFragment.newInstance()
                }
                return lux03OverViewFragment
            }
            else -> {
                if ( !this::lux01OverViewFragment.isInitialized ) {
                    lux01OverViewFragment = Lux01OverViewFragment.newInstance()
                }
                return lux01OverViewFragment
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