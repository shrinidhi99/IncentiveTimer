package net.coffeewarriors.incentivetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import static net.coffeewarriors.incentivetimer.MainActivity.SHARED_PREFERENCES;
import static net.coffeewarriors.incentivetimer.TimerFragment.LONG_BREAK_LENGTH_FILE;
import static net.coffeewarriors.incentivetimer.TimerFragment.LONG_LENGTH_CHANGED;
import static net.coffeewarriors.incentivetimer.TimerFragment.POMODOROS_LENGTH_CHANGED;
import static net.coffeewarriors.incentivetimer.TimerFragment.POMODORO_LENGTH_FILE;
import static net.coffeewarriors.incentivetimer.TimerFragment.SHORT_BREAK_LENGTH_FILE;
import static net.coffeewarriors.incentivetimer.TimerFragment.SHORT_LENGTH_CHANGED;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private NumberPicker nbPomodoro;
    private NumberPicker nbShort;
    private NumberPicker nbLong;

    private int mPomodoroLength;
    private int mShortBreakLength;
    private int mLongBreakLength;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        loadValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_settings, container, false);

        nbPomodoro = inf.findViewById(R.id.nb_pomodoro_length);
        nbShort = inf.findViewById(R.id.nb_short_break_length);
        nbLong = inf.findViewById(R.id.nb_long_break_length);

        nbPomodoro.setMinValue(1);
        nbPomodoro.setMaxValue(120);
        nbPomodoro.setValue(mPomodoroLength/60000);
        nbPomodoro.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                mPomodoroLength = nbPomodoro.getValue() * 60000;
                savePomodoroLength();
            }
        });

        nbShort.setMinValue(1);
        nbShort.setMaxValue(120);
        nbShort.setValue(mShortBreakLength/60000);
        nbShort.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                mShortBreakLength = nbShort.getValue() * 60000;
                saveShortBreakLength();
            }
        });

        nbLong.setMinValue(1);
        nbLong.setMaxValue(120);
        nbLong.setValue(mLongBreakLength/60000);
        nbLong.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                mLongBreakLength = nbLong.getValue() * 60000;
                saveLongBreakLength();
            }
        });

        return inf;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.pauseTimerFragmentChange();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void pauseTimerFragmentChange();
    }
    public void loadValues() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000);
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 300000);
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000);
    }

    public void savePomodoroLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(POMODORO_LENGTH_FILE, mPomodoroLength);
        editor.putBoolean(POMODOROS_LENGTH_CHANGED, true);
        editor.apply();
    }

    public void saveShortBreakLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(SHORT_BREAK_LENGTH_FILE, mShortBreakLength);
        editor.putBoolean(SHORT_LENGTH_CHANGED, true);
        editor.apply();
    }

    public void saveLongBreakLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(LONG_BREAK_LENGTH_FILE, mLongBreakLength);
        editor.putBoolean(LONG_LENGTH_CHANGED, true);
        editor.apply();
    }
}
