package milu.kiriu2010.milux.gui.misc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milux.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MenuFragment : androidx.fragment.app.Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                MenuFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
