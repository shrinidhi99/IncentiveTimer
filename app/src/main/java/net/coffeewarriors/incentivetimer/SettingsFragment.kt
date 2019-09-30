package net.coffeewarriors.incentivetimer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.sun.jndi.toolkit.url.Uri
import com.sun.tools.javac.util.JCDiagnostic.Fragment
import net.coffeewarriors.incentivetimer.MainActivity.SHARED_PREFERENCES
import net.coffeewarriors.incentivetimer.TimerFragment.LONG_BREAK_LENGTH_FILE
import net.coffeewarriors.incentivetimer.TimerFragment.LONG_LENGTH_CHANGED
import net.coffeewarriors.incentivetimer.TimerFragment.POMODOROS_LENGTH_CHANGED
import net.coffeewarriors.incentivetimer.TimerFragment.POMODORO_LENGTH_FILE
import net.coffeewarriors.incentivetimer.TimerFragment.SHORT_BREAK_LENGTH_FILE
import net.coffeewarriors.incentivetimer.TimerFragment.SHORT_LENGTH_CHANGED
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var nbPomodoro: NumberPicker? = null
    private var nbShort: NumberPicker? = null
    private var nbLong: NumberPicker? = null
    private var mPomodoroLength = 0
    private var mShortBreakLength = 0
    private var mLongBreakLength = 0
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1)
            mParam2 = getArguments().getString(ARG_PARAM2)
        }
        loadValues()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inf: View = inflater.inflate(R.layout.fragment_settings, container, false)
        nbPomodoro = inf.findViewById(R.id.nb_pomodoro_length)
        nbShort = inf.findViewById(R.id.nb_short_break_length)
        nbLong = inf.findViewById(R.id.nb_long_break_length)
        nbPomodoro.setMinValue(1)
        nbPomodoro.setMaxValue(120)
        nbPomodoro.setValue(mPomodoroLength / 60000)
        nbPomodoro.setOnScrollListener(object : OnScrollListener() {
            fun onScrollStateChange(numberPicker: NumberPicker?, i: Int) {
                mPomodoroLength = nbPomodoro.getValue() * 60000
                savePomodoroLength()
            }
        })
        nbShort.setMinValue(1)
        nbShort.setMaxValue(120)
        nbShort.setValue(mShortBreakLength / 60000)
        nbShort.setOnScrollListener(object : OnScrollListener() {
            fun onScrollStateChange(numberPicker: NumberPicker?, i: Int) {
                mShortBreakLength = nbShort.getValue() * 60000
                saveShortBreakLength()
            }
        })
        nbLong.setMinValue(1)
        nbLong.setMaxValue(120)
        nbLong.setValue(mLongBreakLength / 60000)
        nbLong.setOnScrollListener(object : OnScrollListener() {
            fun onScrollStateChange(numberPicker: NumberPicker?, i: Int) {
                mLongBreakLength = nbLong.getValue() * 60000
                saveLongBreakLength()
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

    fun onResume() {
        super.onResume()
        mListener!!.pauseTimerFragmentChange()
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

        fun pauseTimerFragmentChange()
    }

    fun loadValues() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000)
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 300000)
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000)
    }

    fun savePomodoroLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putInt(POMODORO_LENGTH_FILE, mPomodoroLength)
        editor.putBoolean(POMODOROS_LENGTH_CHANGED, true)
        editor.apply()
    }

    fun saveShortBreakLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putInt(SHORT_BREAK_LENGTH_FILE, mShortBreakLength)
        editor.putBoolean(SHORT_LENGTH_CHANGED, true)
        editor.apply()
    }

    fun saveLongBreakLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putInt(LONG_BREAK_LENGTH_FILE, mLongBreakLength)
        editor.putBoolean(LONG_LENGTH_CHANGED, true)
        editor.apply()
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
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }
}