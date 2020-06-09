package milu.kiriu2010.milux.gui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milux.R

// 照度センサがなかった場合に、表示されるフラグメント
class NoSensorFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_no_sensor, container, false)

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                NoSensorFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
