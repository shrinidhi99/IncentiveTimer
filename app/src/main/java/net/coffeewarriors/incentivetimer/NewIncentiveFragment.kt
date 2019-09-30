package net.coffeewarriors.incentivetimer

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.sun.jndi.toolkit.url.Uri
import com.sun.tools.javac.util.JCDiagnostic.Fragment
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewIncentiveFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewIncentiveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewIncentiveFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1)
            mParam2 = getArguments().getString(ARG_PARAM2)
        }
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inf: View = inflater.inflate(R.layout.fragment_new_incentive, container, false)
        val addIncentive: Button = inf.findViewById(R.id.add_incentive_button)
        addIncentive.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                addNewIncentiveButton()
            }
        })
        val closeImage: ImageView = inf.findViewById(R.id.close_image_new)
        closeImage.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                closeNewIncentiveFragment()
            }
        })
        val closeText: TextView = inf.findViewById(R.id.close_text_new)
        closeText.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                closeNewIncentiveFragment()
            }
        })
        val chanceNumber: TextView = inf.findViewById(R.id.new_incentive_chance_number)
        val chanceBar: SeekBar = inf.findViewById(R.id.seekBar)
        chanceBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                chanceNumber.setText("$i%")
            }

            fun onStartTrackingTouch(seekBar: SeekBar?) {}
            fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val add: Button = inf.findViewById(R.id.add_incentive_button)
        add.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                val nameInput: EditText = inf.findViewById(R.id.editText)
                mListener!!.addNewIncentiveButton(nameInput.getText().toString(), chanceBar.getProgress())
            }
        })
        return inf
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString()
                    .toString() + " must implement OnFragmentInteractionListener"
            )
        }
    }

    fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)

        fun closeNewIncentiveFragment()
        fun addNewIncentiveButton(name: String?, chance: Int)
    }

    fun addNewIncentiveButton() {}
    fun closeNewIncentiveFragment() {
        mListener!!.closeNewIncentiveFragment()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewIncentiveFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): NewIncentiveFragment {
            val fragment = NewIncentiveFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }
}