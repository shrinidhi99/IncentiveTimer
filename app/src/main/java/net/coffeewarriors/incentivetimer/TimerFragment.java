package net.coffeewarriors.incentivetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.R.attr.duration;
import static android.content.ContentValues.TAG;
import static net.coffeewarriors.incentivetimer.MainActivity.INCENTIVE_LIST;
import static net.coffeewarriors.incentivetimer.MainActivity.SHARED_PREFERENCES;


public class TimerFragment extends Fragment {

    public static final String POMODORO_LENGTH_FILE = "POMODORO_LENGTH_FILE";
    public static final String SHORT_BREAK_LENGTH_FILE = "SHORT_BREAK_LENGTH_FILE";
    public static final String LONG_BREAK_LENGTH_FILE = "LONG_BREAK_LENGTH_FILE";
    private final String MILLIS_LEFT_FILE = "MILLIS_LEFT_FILE";
    private final String TIMER_MAX_FILE = "TIMER_MAX_FILE";
    private final String CURRENTLY_BREAKING_FILE = "CURRENTLY_BREAKING_FILE";
    private final String CURRENTLY_SHORT_BREAK = "CURRENTLY_SHORT_BREAK";
    private final String CURRENTLY_LONG_BREAK = "CURRENTLY_LONG_BREAK";
    private static final String MAX_POMODOROS_FILE = "MAX_POMODOROS_FILE";
    private final String POMODOROS_DONE_FILE = "POMODOROS_DONE_FILE";
    public static final String POMODOROS_LENGTH_CHANGED = "POMODOROS_LENGTH_CHANGED";
    public static final String SHORT_LENGTH_CHANGED = "SHORT_LENGTH_CHANGED";
    public static final String LONG_LENGTH_CHANGED = "LONG_LENGTH_CHANGED";

    private OnFragmentInteractionListener mListener;

    private int mPomodoroLength;
    private int mShortBreakLength;
    private int mLongBreakLength;

    private DonutProgress mTimerCircle;
    private TextView mPomodorosFinishedText;
    private CountDownTimer pomodoroTimer;
    private Button startStop;
    private Button resetTimerButton;
    private Button resetPomodorosButton;
    private Button skipBreakButton;
    private ImageView breakImage;
    private TextView breakText;
    private int mMillisLeft;
    private int mTimerMax;
    private int mMaxPomodoros;
    private int mPomodorosDone;
    private boolean mCurrentlyRunning;
    private boolean mCurrentlyBreaking;
    private boolean mCurrentlyShortBreak;
    private boolean mCurrentlyLongBreak;
    private boolean mPomLengthChanged;
    private boolean mShortLengthChanged;
    private boolean mLongLengthChanged;
    private boolean mFragmentInactive;
    private boolean mBreakSkipped = false;

    private ArrayList<IncentiveItem> mIncentiveList;

    private MediaPlayer mediaPlayer;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTimerStats();
        loadTimerStats2();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.fragment_timer, container, false);

        loadTimerStats();

        mTimerCircle = inf.findViewById(R.id.donut_progress_timer);
        mTimerCircle.setMax(mPomodoroLength);

        mPomodorosFinishedText = inf.findViewById(R.id.finished_pomodoros_text);

        resetTimerButton = inf.findViewById(R.id.reset_timer_button);
        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        resetPomodorosButton = inf.findViewById(R.id.reset_pomodoros_button);
        resetPomodorosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPomodoros();
            }
        });
        skipBreakButton = inf.findViewById(R.id.skip_break_button);
        skipBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipBreak();
            }
        });


        breakImage = inf.findViewById(R.id.breakImage);
        breakText = inf.findViewById(R.id.break_text);

        updateResetButtons();
        updatePomodorosFinished();
        updateClock();


        startStop = inf.findViewById(R.id.start_stop_button);
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStopButton();
            }
        });

        updateStartStopText();

        return inf;
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
    public void onPause() {
        super.onPause();
        saveTimerStats();
        //   pauseTimerFragmentChange();
        mFragmentInactive = true;
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
    }

    public void startStopButton() {
        if (!mCurrentlyRunning) {
            startTimer();
        } else pauseTimer();
    }

    public void startTimer() {
        loadTimerStats();
        updateTimerMax();
        setTimerMax(mTimerMax);


        mPomLengthChanged = false;
        mShortLengthChanged = false;
        mLongLengthChanged = false;
        saveTimerStats();

        if (!mFragmentInactive || mCurrentlyBreaking) {
            mCurrentlyRunning = true;

            pomodoroTimer = new CountDownTimer(mMillisLeft, 1000) {

                @Override
                public void onTick(long l) {
                    mMillisLeft = (int) l;
                    updateClock();
                }

                @Override
                public void onFinish() {
                    if (!mCurrentlyBreaking) finishPomodoro();
                    else finishBreak();
                }
            }.start();
        } else mCurrentlyRunning = false;
        updateStartStopText();
        updateResetButtons();

    }

    public void finishPomodoro() {
        mPomodorosDone++;
        savePomodorosFinished();
        updatePomodorosFinished();
        loot();
        startBreak();
    }

    public void loot() {
        int unlockCount = 0;
        boolean listContaintsActives = false;
        loadIncentiveList();
        if (!mIncentiveList.isEmpty()) {

            for (int i = 0; i < mIncentiveList.size(); i++) {
                if (!mIncentiveList.get(i).isUnlocked()) {
                    listContaintsActives = true;
                }
            }

            for (int i = 0; i < mIncentiveList.size(); i++) {
                if (!mIncentiveList.get(i).isUnlocked()) {
                    int rnd = (int) (Math.random() * 100);
                    int chance = mIncentiveList.get(i).getChance();
                    if (rnd <= chance) {
                        mIncentiveList.get(i).unlock();
                        unlockCount++;
                    }
                }
            }

        }

        playPomodoroFinishedSound();

        if (!mIncentiveList.isEmpty() && listContaintsActives) {
            if (unlockCount > 0) {
                Toast toast = Toast.makeText(getActivity(), "" + unlockCount + " Incentives unlocked!", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "No luck this time! Keep it up!", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getActivity(), "Pomodoro finished! No active Incentives.", Toast.LENGTH_LONG);
            toast.show();
        }
        saveIncentiveList();
    }

    public void startBreak() {
        mCurrentlyBreaking = true;
        updateBreakColor();
        if (mPomodorosDone >= mMaxPomodoros) startLongBreak();
        else startShortBreak();
    }

    public void startShortBreak() {
        loadShortBreakLength();
        mCurrentlyShortBreak = true;
        setTimerMax(mShortBreakLength);
        mMillisLeft = mShortBreakLength;

        startTimer();
    }

    public void startLongBreak() {
        loadLongBreakLength();
        mCurrentlyLongBreak = true;
        setTimerMax(mLongBreakLength);
        mMillisLeft = mLongBreakLength;

        startTimer();
    }

    public void finishBreak() {
        loadPomodoroLength();
        mCurrentlyBreaking = false;
        mCurrentlyShortBreak = false;
        mCurrentlyLongBreak = false;
        updateBreakColor();
        if (mPomodorosDone >= mMaxPomodoros) mPomodorosDone = 0;
        updatePomodorosFinished();
        savePomodorosFinished();
        mTimerMax = mPomodoroLength;
        mMillisLeft = mPomodoroLength;
        mTimerCircle.setProgress(0);
        setTimerMax(mPomodoroLength);
        startTimer();

        if (!mBreakSkipped) {
            playBreakOverSound();

            Toast toast = Toast.makeText(getActivity(), "Break is over. Time to get to work!", Toast.LENGTH_LONG);
            toast.show();
        }

        mBreakSkipped = false;
    }

    public void pauseTimer() {
        if (mCurrentlyRunning) {
            mCurrentlyRunning = false;
            startStop.setText(R.string.button_start);
            pomodoroTimer.cancel();
            updateResetButtons();
        }
    }

    public void pauseTimerFragmentChange() {
        if (mCurrentlyRunning && !mCurrentlyBreaking) {
            mCurrentlyRunning = false;
            startStop.setText(R.string.button_start);
            pomodoroTimer.cancel();
            updateResetButtons();
        }
    }

    public boolean getTimerRunning() {
        return  mCurrentlyRunning;
    }

    public boolean getCurrentlyBreaking() {
        return mCurrentlyBreaking;
    }

    public void updateClock() {
        int minutes = mMillisLeft / 60000;
        int seconds = (mMillisLeft % 60000) / 1000;
        String clockText = "";

        if (minutes < 10) clockText += "0";
        clockText += minutes;
        clockText += ":";
        if (seconds < 10) clockText += "0";
        clockText += seconds;

        mTimerCircle.setProgress(mTimerMax - mMillisLeft);
        mTimerCircle.setText(clockText);
    }

    public void setTimerMax(int max) {
        mTimerCircle.setMax(max);
    }

    public void updateStartStopText() {
        if (mCurrentlyRunning)
            startStop.setText(R.string.button_pause);
        else startStop.setText(R.string.button_start);
    }

    public void updateResetButtons() {


        if (!mCurrentlyRunning) {
            resetTimerButton.setVisibility(View.VISIBLE);

            if (mMillisLeft < mTimerMax) resetTimerButton.setVisibility(View.VISIBLE);
            else resetTimerButton.setVisibility(View.INVISIBLE);

            if (mPomodorosDone > 0) {
                resetPomodorosButton.setVisibility(View.VISIBLE);
            } else resetPomodorosButton.setVisibility(View.INVISIBLE);

            if (mCurrentlyBreaking) {
                skipBreakButton.setVisibility(View.VISIBLE);
            }

        } else {
            resetPomodorosButton.setVisibility(View.INVISIBLE);
            skipBreakButton.setVisibility(View.INVISIBLE);
            resetTimerButton.setVisibility(View.INVISIBLE);
        }
    }

    public void resetTimer() {

        if (!mCurrentlyBreaking) {
            mMillisLeft = mPomodoroLength;
            mTimerMax = mPomodoroLength;
            setTimerMax(mTimerMax);
        } else if (mCurrentlyShortBreak) {
            mMillisLeft = mShortBreakLength;
            mTimerMax = mShortBreakLength;
            setTimerMax(mTimerMax);
        } else if (mCurrentlyLongBreak) {
            mMillisLeft = mLongBreakLength;
            mTimerMax = mLongBreakLength;
            setTimerMax(mTimerMax);
        }

        mPomLengthChanged = false;
        mShortLengthChanged = false;
        mLongLengthChanged = false;
        updateClock();
        updateResetButtons();
    }

    public void updateTimerMax() {
        if (!mCurrentlyBreaking) {
            mTimerMax = mPomodoroLength;
            setTimerMax(mTimerMax);
        } else if (mCurrentlyShortBreak) {
            mTimerMax = mShortBreakLength;
            setTimerMax(mTimerMax);
        } else if (mCurrentlyLongBreak) {
            mTimerMax = mLongBreakLength;
            setTimerMax(mTimerMax);
        }
    }

    public void resetPomodoros() {
        mPomodorosDone = 0;
        updatePomodorosFinished();
        savePomodorosFinished();
        updateResetButtons();
    }

    public void skipBreak() {
        mBreakSkipped = true;
        finishBreak();
    }

    public void updatePomodorosFinished() {
        mPomodorosFinishedText.setText("" + mPomodorosDone + "/" + mMaxPomodoros);
    }

    public void playPomodoroFinishedSound() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.pomodorofinished);
        mediaPlayer.start();
    }

    public void playBreakOverSound() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.breakover);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer = null;
            }
        });
        mediaPlayer.start();
    }

    public void saveTimerStats() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(MILLIS_LEFT_FILE, mMillisLeft);
        editor.putInt(TIMER_MAX_FILE, mTimerMax);
        editor.putBoolean(CURRENTLY_BREAKING_FILE, mCurrentlyBreaking);
        editor.putBoolean(CURRENTLY_SHORT_BREAK, mCurrentlyShortBreak);
        editor.putBoolean(CURRENTLY_LONG_BREAK, mCurrentlyLongBreak);
        editor.putBoolean(POMODOROS_LENGTH_CHANGED, mPomLengthChanged);
        editor.putBoolean(SHORT_LENGTH_CHANGED, mShortLengthChanged);
        editor.putBoolean(LONG_LENGTH_CHANGED, mLongLengthChanged);
        editor.apply();
    }

    public void savePomodorosFinished() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(POMODOROS_DONE_FILE, mPomodorosDone);
        editor.apply();
    }

    public void saveCurrentlyBreaking() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(CURRENTLY_BREAKING_FILE, mCurrentlyBreaking);
    }

    public void loadTimerStats() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000);
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 30000);
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000);
        mMaxPomodoros = sharedPrefs.getInt(MAX_POMODOROS_FILE, 4);

        mPomLengthChanged = sharedPrefs.getBoolean(POMODOROS_LENGTH_CHANGED, false);
        mShortLengthChanged = sharedPrefs.getBoolean(SHORT_LENGTH_CHANGED, false);
        mLongLengthChanged = sharedPrefs.getBoolean(LONG_LENGTH_CHANGED, false);

    }

    public void loadTimerStats2() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPomodorosDone = sharedPrefs.getInt(POMODOROS_DONE_FILE, 0);
        mCurrentlyBreaking = sharedPrefs.getBoolean(CURRENTLY_BREAKING_FILE, false);
        mCurrentlyShortBreak = sharedPrefs.getBoolean(CURRENTLY_SHORT_BREAK, false);
        mCurrentlyLongBreak = sharedPrefs.getBoolean(CURRENTLY_LONG_BREAK, false);
        mMillisLeft = sharedPrefs.getInt(MILLIS_LEFT_FILE, mPomodoroLength);
        mTimerMax = sharedPrefs.getInt(TIMER_MAX_FILE, mPomodoroLength);

    }

    public void loadPomodoroLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000);
    }

    public void loadShortBreakLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 300000);
    }

    public void loadLongBreakLength() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000);
    }

    public void saveIncentiveList() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mIncentiveList);
        editor.putString(INCENTIVE_LIST, json);
        editor.apply();
    }

    public void loadIncentiveList() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(INCENTIVE_LIST, null);
        Type type = new TypeToken<ArrayList<IncentiveItem>>() {
        }.getType();
        mIncentiveList = gson.fromJson(json, type);

        if (mIncentiveList == null) mIncentiveList = new ArrayList<IncentiveItem>();

    }

    public void updateBreakColor() {

        if (mCurrentlyBreaking) {
            mTimerCircle.setTextColor(Color.parseColor("#FF12CF28"));
            mTimerCircle.setUnfinishedStrokeColor(Color.parseColor("#FF12CF28"));
            breakImage.setVisibility(View.VISIBLE);
            breakText.setVisibility(View.VISIBLE);
        } else {
            mTimerCircle.setTextColor(fetchPrimaryColor());
            mTimerCircle.setUnfinishedStrokeColor(fetchPrimaryColor());
            breakImage.setVisibility(View.INVISIBLE);
            breakText.setVisibility(View.INVISIBLE);
        }
    }

    private int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentInactive = false;
        loadTimerStats();
        updateBreakColor();
        updateTimerMax();
        updateClock();
        updateStartStopText();
        updateResetButtons();


        if (mPomLengthChanged && !mCurrentlyBreaking || mShortLengthChanged && mCurrentlyShortBreak || mLongLengthChanged && mCurrentlyLongBreak) {
            pauseTimer();
            resetTimer();
        }

    }

}
