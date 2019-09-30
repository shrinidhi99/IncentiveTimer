package net.coffeewarriors.incentivetimer

import android.R.attr.duration
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.lzyzsd.circleprogress.DonutProgress
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sun.jndi.toolkit.url.Uri
import com.sun.tools.javac.util.JCDiagnostic.Fragment
import net.coffeewarriors.incentivetimer.MainActivity.INCENTIVE_LIST
import net.coffeewarriors.incentivetimer.MainActivity.SHARED_PREFERENCES
import java.lang.RuntimeException
import java.lang.reflect.Type
import java.util.*

class TimerFragment :
    Fragment() {
    private val MILLIS_LEFT_FILE = "MILLIS_LEFT_FILE"
    private val TIMER_MAX_FILE = "TIMER_MAX_FILE"
    private val CURRENTLY_BREAKING_FILE = "CURRENTLY_BREAKING_FILE"
    private val CURRENTLY_SHORT_BREAK = "CURRENTLY_SHORT_BREAK"
    private val CURRENTLY_LONG_BREAK = "CURRENTLY_LONG_BREAK"
    private val POMODOROS_DONE_FILE = "POMODOROS_DONE_FILE"
    private var mListener: OnFragmentInteractionListener? = null
    private var mPomodoroLength = 0
    private var mShortBreakLength = 0
    private var mLongBreakLength = 0
    private var mTimerCircle: DonutProgress? = null
    private var mPomodorosFinishedText: TextView? = null
    private var pomodoroTimer: CountDownTimer? = null
    private var startStop: Button? = null
    private var resetTimerButton: Button? = null
    private var resetPomodorosButton: Button? = null
    private var skipBreakButton: Button? = null
    private var breakImage: ImageView? = null
    private var breakText: TextView? = null
    private var mMillisLeft = 0
    private var mTimerMax = 0
    private var mMaxPomodoros = 0
    private var mPomodorosDone = 0
    var timerRunning = false
        private set
    var currentlyBreaking = false
        private set
    private var mCurrentlyShortBreak = false
    private var mCurrentlyLongBreak = false
    private var mPomLengthChanged = false
    private var mShortLengthChanged = false
    private var mLongLengthChanged = false
    private var mFragmentInactive = false
    private var mBreakSkipped = false
    private var mIncentiveList: ArrayList<IncentiveItem>? = null
    private var mediaPlayer: MediaPlayer? = null
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTimerStats()
        loadTimerStats2()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inf: View = inflater.inflate(R.layout.fragment_timer, container, false)
        loadTimerStats()
        mTimerCircle = inf.findViewById(R.id.donut_progress_timer)
        mTimerCircle.setMax(mPomodoroLength)
        mPomodorosFinishedText = inf.findViewById(R.id.finished_pomodoros_text)
        resetTimerButton = inf.findViewById(R.id.reset_timer_button)
        resetTimerButton.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                resetTimer()
            }
        })
        resetPomodorosButton = inf.findViewById(R.id.reset_pomodoros_button)
        resetPomodorosButton.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                resetPomodoros()
            }
        })
        skipBreakButton = inf.findViewById(R.id.skip_break_button)
        skipBreakButton.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                skipBreak()
            }
        })
        breakImage = inf.findViewById(R.id.breakImage)
        breakText = inf.findViewById(R.id.break_text)
        updateResetButtons()
        updatePomodorosFinished()
        updateClock()
        startStop = inf.findViewById(R.id.start_stop_button)
        startStop.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                startStopButton()
            }
        })
        updateStartStopText()
        return inf
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

    fun onPause() {
        super.onPause()
        saveTimerStats()
        //   pauseTimerFragmentChange();

        mFragmentInactive = true
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
        fun onFragmentInteraction(uri: Uri?)
    }

    fun startStopButton() {
        if (!timerRunning) {
            startTimer()
        } else pauseTimer()
    }

    fun startTimer() {
        loadTimerStats()
        updateTimerMax()
        setTimerMax(mTimerMax)
        mPomLengthChanged = false
        mShortLengthChanged = false
        mLongLengthChanged = false
        saveTimerStats()
        if (!mFragmentInactive || currentlyBreaking) {
            timerRunning = true
            pomodoroTimer = object : CountDownTimer(mMillisLeft, 1000) {
                fun onTick(l: Long) {
                    mMillisLeft = l.toInt()
                    updateClock()
                }

                fun onFinish() {
                    if (!currentlyBreaking) finishPomodoro() else finishBreak()
                }
            }.start()
        } else timerRunning = false
        updateStartStopText()
        updateResetButtons()
    }

    fun finishPomodoro() {
        mPomodorosDone++
        savePomodorosFinished()
        updatePomodorosFinished()
        loot()
        startBreak()
    }

    fun loot() {
        var unlockCount = 0
        var listContaintsActives = false
        loadIncentiveList()
        if (!mIncentiveList!!.isEmpty()) {
            for (i in mIncentiveList!!.indices) {
                if (!mIncentiveList!![i].isUnlocked()) {
                    listContaintsActives = true
                }
            }
            for (i in mIncentiveList!!.indices) {
                if (!mIncentiveList!![i].isUnlocked()) {
                    val rnd = (Math.random() * 100).toInt()
                    val chance: Int = mIncentiveList!![i].getChance()
                    if (rnd <= chance) {
                        mIncentiveList!![i].unlock()
                        unlockCount++
                    }
                }
            }
        }
        playPomodoroFinishedSound()
        if (!mIncentiveList!!.isEmpty() && listContaintsActives) {
            if (unlockCount > 0) {
                val toast: Toast = Toast.makeText(
                    getActivity(),
                    "$unlockCount Incentives unlocked!",
                    Toast.LENGTH_LONG
                )
                toast.show()
            } else {
                val toast: Toast = Toast.makeText(getActivity(), "No luck this time! Keep it up!", Toast.LENGTH_LONG)
                toast.show()
            }
        } else {
            val toast: Toast =
                Toast.makeText(getActivity(), "Pomodoro finished! No active Incentives.", Toast.LENGTH_LONG)
            toast.show()
        }
        saveIncentiveList()
    }

    fun startBreak() {
        currentlyBreaking = true
        updateBreakColor()
        if (mPomodorosDone >= mMaxPomodoros) startLongBreak() else startShortBreak()
    }

    fun startShortBreak() {
        loadShortBreakLength()
        mCurrentlyShortBreak = true
        setTimerMax(mShortBreakLength)
        mMillisLeft = mShortBreakLength
        startTimer()
    }

    fun startLongBreak() {
        loadLongBreakLength()
        mCurrentlyLongBreak = true
        setTimerMax(mLongBreakLength)
        mMillisLeft = mLongBreakLength
        startTimer()
    }

    fun finishBreak() {
        loadPomodoroLength()
        currentlyBreaking = false
        mCurrentlyShortBreak = false
        mCurrentlyLongBreak = false
        updateBreakColor()
        if (mPomodorosDone >= mMaxPomodoros) mPomodorosDone = 0
        updatePomodorosFinished()
        savePomodorosFinished()
        mTimerMax = mPomodoroLength
        mMillisLeft = mPomodoroLength
        mTimerCircle.setProgress(0)
        setTimerMax(mPomodoroLength)
        startTimer()
        if (!mBreakSkipped) {
            playBreakOverSound()
            val toast: Toast = Toast.makeText(getActivity(), "Break is over. Time to get to work!", Toast.LENGTH_LONG)
            toast.show()
        }
        mBreakSkipped = false
    }

    fun pauseTimer() {
        if (timerRunning) {
            timerRunning = false
            startStop.setText(R.string.button_start)
            pomodoroTimer.cancel()
            updateResetButtons()
        }
    }

    fun pauseTimerFragmentChange() {
        if (timerRunning && !currentlyBreaking) {
            timerRunning = false
            startStop.setText(R.string.button_start)
            pomodoroTimer.cancel()
            updateResetButtons()
        }
    }

    fun updateClock() {
        val minutes = mMillisLeft / 60000
        val seconds = mMillisLeft % 60000 / 1000
        var clockText = ""
        if (minutes < 10) clockText += "0"
        clockText += minutes
        clockText += ":"
        if (seconds < 10) clockText += "0"
        clockText += seconds
        mTimerCircle.setProgress(mTimerMax - mMillisLeft)
        mTimerCircle.setText(clockText)
    }

    fun setTimerMax(max: Int) {
        mTimerCircle.setMax(max)
    }

    fun updateStartStopText() {
        if (timerRunning) startStop.setText(R.string.button_pause) else startStop.setText(R.string.button_start)
    }

    fun updateResetButtons() {
        if (!timerRunning) {
            resetTimerButton.setVisibility(View.VISIBLE)
            if (mMillisLeft < mTimerMax) resetTimerButton.setVisibility(View.VISIBLE) else resetTimerButton.setVisibility(
                View.INVISIBLE
            )
            if (mPomodorosDone > 0) {
                resetPomodorosButton.setVisibility(View.VISIBLE)
            } else resetPomodorosButton.setVisibility(View.INVISIBLE)
            if (currentlyBreaking) {
                skipBreakButton.setVisibility(View.VISIBLE)
            }
        } else {
            resetPomodorosButton.setVisibility(View.INVISIBLE)
            skipBreakButton.setVisibility(View.INVISIBLE)
            resetTimerButton.setVisibility(View.INVISIBLE)
        }
    }

    fun resetTimer() {
        if (!currentlyBreaking) {
            mMillisLeft = mPomodoroLength
            mTimerMax = mPomodoroLength
            setTimerMax(mTimerMax)
        } else if (mCurrentlyShortBreak) {
            mMillisLeft = mShortBreakLength
            mTimerMax = mShortBreakLength
            setTimerMax(mTimerMax)
        } else if (mCurrentlyLongBreak) {
            mMillisLeft = mLongBreakLength
            mTimerMax = mLongBreakLength
            setTimerMax(mTimerMax)
        }
        mPomLengthChanged = false
        mShortLengthChanged = false
        mLongLengthChanged = false
        updateClock()
        updateResetButtons()
    }

    fun updateTimerMax() {
        if (!currentlyBreaking) {
            mTimerMax = mPomodoroLength
            setTimerMax(mTimerMax)
        } else if (mCurrentlyShortBreak) {
            mTimerMax = mShortBreakLength
            setTimerMax(mTimerMax)
        } else if (mCurrentlyLongBreak) {
            mTimerMax = mLongBreakLength
            setTimerMax(mTimerMax)
        }
    }

    fun resetPomodoros() {
        mPomodorosDone = 0
        updatePomodorosFinished()
        savePomodorosFinished()
        updateResetButtons()
    }

    fun skipBreak() {
        mBreakSkipped = true
        finishBreak()
    }

    fun updatePomodorosFinished() {
        mPomodorosFinishedText.setText("$mPomodorosDone/$mMaxPomodoros")
    }

    fun playPomodoroFinishedSound() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.pomodorofinished)
        mediaPlayer.start()
    }

    fun playBreakOverSound() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.breakover)
        mediaPlayer.setOnCompletionListener(object : OnCompletionListener() {
            fun onCompletion(mediaPlayer: MediaPlayer?) {
                var mediaPlayer: MediaPlayer? = mediaPlayer
                mediaPlayer = null
            }
        })
        mediaPlayer.start()
    }

    fun saveTimerStats() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putInt(MILLIS_LEFT_FILE, mMillisLeft)
        editor.putInt(TIMER_MAX_FILE, mTimerMax)
        editor.putBoolean(CURRENTLY_BREAKING_FILE, currentlyBreaking)
        editor.putBoolean(CURRENTLY_SHORT_BREAK, mCurrentlyShortBreak)
        editor.putBoolean(CURRENTLY_LONG_BREAK, mCurrentlyLongBreak)
        editor.putBoolean(POMODOROS_LENGTH_CHANGED, mPomLengthChanged)
        editor.putBoolean(SHORT_LENGTH_CHANGED, mShortLengthChanged)
        editor.putBoolean(LONG_LENGTH_CHANGED, mLongLengthChanged)
        editor.apply()
    }

    fun savePomodorosFinished() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putInt(POMODOROS_DONE_FILE, mPomodorosDone)
        editor.apply()
    }

    fun saveCurrentlyBreaking() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putBoolean(CURRENTLY_BREAKING_FILE, currentlyBreaking)
    }

    fun loadTimerStats() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000)
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 30000)
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000)
        mMaxPomodoros = sharedPrefs.getInt(MAX_POMODOROS_FILE, 4)
        mPomLengthChanged = sharedPrefs.getBoolean(POMODOROS_LENGTH_CHANGED, false)
        mShortLengthChanged = sharedPrefs.getBoolean(SHORT_LENGTH_CHANGED, false)
        mLongLengthChanged = sharedPrefs.getBoolean(LONG_LENGTH_CHANGED, false)
    }

    fun loadTimerStats2() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mPomodorosDone = sharedPrefs.getInt(POMODOROS_DONE_FILE, 0)
        currentlyBreaking = sharedPrefs.getBoolean(CURRENTLY_BREAKING_FILE, false)
        mCurrentlyShortBreak = sharedPrefs.getBoolean(CURRENTLY_SHORT_BREAK, false)
        mCurrentlyLongBreak = sharedPrefs.getBoolean(CURRENTLY_LONG_BREAK, false)
        mMillisLeft = sharedPrefs.getInt(MILLIS_LEFT_FILE, mPomodoroLength)
        mTimerMax = sharedPrefs.getInt(TIMER_MAX_FILE, mPomodoroLength)
    }

    fun loadPomodoroLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mPomodoroLength = sharedPrefs.getInt(POMODORO_LENGTH_FILE, 1500000)
    }

    fun loadShortBreakLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mShortBreakLength = sharedPrefs.getInt(SHORT_BREAK_LENGTH_FILE, 300000)
    }

    fun loadLongBreakLength() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mLongBreakLength = sharedPrefs.getInt(LONG_BREAK_LENGTH_FILE, 1500000)
    }

    fun saveIncentiveList() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(mIncentiveList)
        editor.putString(INCENTIVE_LIST, json)
        editor.apply()
    }

    fun loadIncentiveList() {
        val sharedPrefs: SharedPreferences =
            getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String = sharedPrefs.getString(INCENTIVE_LIST, null)
        val type: Type = object : TypeToken<ArrayList<IncentiveItem?>?>() {}.getType()
        mIncentiveList = gson.fromJson(json, type)
        if (mIncentiveList == null) mIncentiveList = ArrayList<IncentiveItem>()
    }

    fun updateBreakColor() {
        if (currentlyBreaking) {
            mTimerCircle.setTextColor(Color.parseColor("#FF12CF28"))
            mTimerCircle.setUnfinishedStrokeColor(Color.parseColor("#FF12CF28"))
            breakImage.setVisibility(View.VISIBLE)
            breakText.setVisibility(View.VISIBLE)
        } else {
            mTimerCircle.setTextColor(fetchPrimaryColor())
            mTimerCircle.setUnfinishedStrokeColor(fetchPrimaryColor())
            breakImage.setVisibility(View.INVISIBLE)
            breakText.setVisibility(View.INVISIBLE)
        }
    }

    private fun fetchPrimaryColor(): Int {
        val typedValue = TypedValue()
        val a: TypedArray = getActivity().obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
        val color: Int = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun onResume() {
        super.onResume()
        mFragmentInactive = false
        loadTimerStats()
        updateBreakColor()
        updateTimerMax()
        updateClock()
        updateStartStopText()
        updateResetButtons()
        if (mPomLengthChanged && !currentlyBreaking || mShortLengthChanged && mCurrentlyShortBreak || mLongLengthChanged && mCurrentlyLongBreak) {
            pauseTimer()
            resetTimer()
        }
    }

    companion object {
        const val POMODORO_LENGTH_FILE = "POMODORO_LENGTH_FILE"
        const val SHORT_BREAK_LENGTH_FILE = "SHORT_BREAK_LENGTH_FILE"
        const val LONG_BREAK_LENGTH_FILE = "LONG_BREAK_LENGTH_FILE"
        private const val MAX_POMODOROS_FILE = "MAX_POMODOROS_FILE"
        const val POMODOROS_LENGTH_CHANGED = "POMODOROS_LENGTH_CHANGED"
        const val SHORT_LENGTH_CHANGED = "SHORT_LENGTH_CHANGED"
        const val LONG_LENGTH_CHANGED = "LONG_LENGTH_CHANGED"
    }
}