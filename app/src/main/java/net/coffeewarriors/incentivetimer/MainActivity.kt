package net.coffeewarriors.incentivetimer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sun.jndi.toolkit.url.Uri
import sun.awt.im.InputMethodManager

class MainActivity : AppCompatActivity(), TimerFragment.OnFragmentInteractionListener,
    IncentivesFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
    NewIncentiveFragment.OnFragmentInteractionListener,
    EditIncentiveFragment.OnFragmentInteractionListener {
    private var navigation: BottomNavigationView? = null
    private var manager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null
    private var timerFragment: TimerFragment? = null
    private var newIncentiveFragment: NewIncentiveFragment? = null
    private var editIncentiveFragment: EditIncentiveFragment? = null
    private var incentivesFragment: IncentivesFragment? = null
    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : OnNavigationItemSelectedListener() {
            fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.navigation_timer -> {
                        openTimerFragment()
                        return true
                    }
                    R.id.navigation_incentives -> {
                        openIncentivesFragment()
                        return true
                    }
                    R.id.navigation_settings -> {
                        openSettingsFragment()
                        return true
                    }
                }
                return false
            }
        }

    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        instantiateFragmentsAndManager()
    }

    fun instantiateFragmentsAndManager() {
        timerFragment = TimerFragment()
        timerFragment.setRetainInstance(true)
        manager = getSupportFragmentManager()
        transaction = manager.beginTransaction()
        transaction.replace(R.id.content, timerFragment)
        // transaction.addToBackStack(null);


        transaction.commit()
    }

    fun openTimerFragment() {
        transaction = manager.beginTransaction()
        transaction.replace(R.id.content, timerFragment)
        if (newIncentiveFragment != null) transaction.remove(newIncentiveFragment)
        // transaction.addToBackStack(null);


        transaction.commit()
    }

    fun openIncentivesFragment() {
        incentivesFragment = IncentivesFragment()
        transaction = manager.beginTransaction()
        transaction.replace(R.id.content, incentivesFragment)
        //   transaction.addToBackStack(null);


        transaction.commit()
    }

    fun openNewIncentiveFragment() {
        newIncentiveFragment = NewIncentiveFragment()
        transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.new_incentive_holder, newIncentiveFragment)
        transaction.commit()
        editIncentiveFragment = null
    }

    fun addNewIncentiveButton(name: String?, chance: Int) {
        incentivesFragment.addNewIncentive(name, chance)
        closeNewIncentiveFragment()
    }

    fun closeNewIncentiveFragment() {
        closeKeyboard()
        transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.remove(newIncentiveFragment)
        transaction.commit()
        newIncentiveFragment = null
    }

    fun listItemClick(position: Int) {
        openEditIncentiveFragment(position)
    }

    fun openEditIncentiveFragment(position: Int) {
        val selectedName: String = incentivesFragment.getCurrentName(position)
        val selectedChance: Int = incentivesFragment.getCurrentChance(position)
        editIncentiveFragment = EditIncentiveFragment.newInstance(selectedName, selectedChance, position)
        transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.replace(R.id.new_incentive_holder, editIncentiveFragment)
        transaction.commit()
        newIncentiveFragment = null
    }

    fun closeEditIncentiveFragment() {
        closeKeyboard()
        transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
        transaction.remove(editIncentiveFragment)
        transaction.commit()
        editIncentiveFragment = null
    }

    fun openSettingsFragment() {
        val settingsFragment = SettingsFragment()
        transaction = manager.beginTransaction()
        transaction.replace(R.id.content, settingsFragment)
        if (newIncentiveFragment != null) transaction.remove(newIncentiveFragment)
        transaction.commit()
    }

    fun deleteItem(position: Int) {
        incentivesFragment.deleteItem(position)
        closeEditIncentiveFragment()
    }

    fun saveChanges(newName: String?, newChance: Int, position: Int) {
        incentivesFragment.changeItemStats(newName, newChance, position)
        closeEditIncentiveFragment()
    }

    fun closeKeyboard() {
        val view: View = this.getCurrentFocus()
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    fun onBackPressed() {
        if (newIncentiveFragment != null) {
            closeNewIncentiveFragment()
        } else if (editIncentiveFragment != null) {
            closeEditIncentiveFragment()
        } else if (!timerFragment.isVisible()) {
            openTimerFragment()
            navigation.setSelectedItemId(R.id.navigation_timer)
        } else super.onBackPressed()
    }

    fun onFragmentInteraction(uri: Uri?) {}
    fun pauseTimerFragmentChange() {
        val timerRunning: Boolean = timerFragment.getTimerRunning()
        val currentlyBreaking: Boolean = timerFragment.getCurrentlyBreaking()
        if (timerRunning && !currentlyBreaking) {
            timerFragment.pauseTimerFragmentChange()
            val toast: Toast = Toast.makeText(this, "Timer paused", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    companion object {
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
        const val INCENTIVE_LIST = "INCENTIVE_LIST"
    }
}