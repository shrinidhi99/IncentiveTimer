package net.coffeewarriors.incentivetimer;

import android.content.Context;

import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements TimerFragment.OnFragmentInteractionListener, IncentivesFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, NewIncentiveFragment.OnFragmentInteractionListener, EditIncentiveFragment.OnFragmentInteractionListener {
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String INCENTIVE_LIST = "INCENTIVE_LIST";

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private TimerFragment timerFragment;
    private NewIncentiveFragment newIncentiveFragment;
    private EditIncentiveFragment editIncentiveFragment;
    private IncentivesFragment incentivesFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_timer:
                    openTimerFragment();
                    return true;
                case R.id.navigation_incentives:
                    openIncentivesFragment();
                    return true;
                case R.id.navigation_settings:
                    openSettingsFragment();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        instantiateFragmentsAndManager();

    }

    public void instantiateFragmentsAndManager() {
        timerFragment = new TimerFragment();
        timerFragment.setRetainInstance(true);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, timerFragment);
       // transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openTimerFragment() {
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, timerFragment);
        if (newIncentiveFragment != null) transaction.remove(newIncentiveFragment);
       // transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openIncentivesFragment() {
        incentivesFragment = new IncentivesFragment();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, incentivesFragment);
     //   transaction.addToBackStack(null);
        transaction.commit();
        timerPausedToast();
    }

    public void openNewIncentiveFragment() {
        newIncentiveFragment = new NewIncentiveFragment();
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.replace(R.id.new_incentive_holder, newIncentiveFragment);
        transaction.commit();
        editIncentiveFragment = null;
    }

    public void addNewIncentiveButton(String name, int chance) {
        incentivesFragment.addNewIncentive(name, chance);
        closeNewIncentiveFragment();
    }

    public void closeNewIncentiveFragment() {
        closeKeyboard();
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.remove(newIncentiveFragment);
        transaction.commit();
        newIncentiveFragment = null;
    }

    public void listItemClick(int position) {
        openEditIncentiveFragment(position);
    }

    public void openEditIncentiveFragment(int position) {
        String selectedName = incentivesFragment.getCurrentName(position);
        int selectedChance = incentivesFragment.getCurrentChance(position);

        editIncentiveFragment = EditIncentiveFragment.newInstance(selectedName, selectedChance, position);
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.replace(R.id.new_incentive_holder, editIncentiveFragment);
        transaction.commit();
        newIncentiveFragment = null;
    }

    public void closeEditIncentiveFragment() {
        closeKeyboard();
        transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.remove(editIncentiveFragment);
        transaction.commit();
        editIncentiveFragment = null;
    }

    public void openSettingsFragment() {
        SettingsFragment settingsFragment = new SettingsFragment();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content, settingsFragment);
        if (newIncentiveFragment != null) transaction.remove(newIncentiveFragment);
        transaction.commit();
        timerPausedToast();
    }

    public void timerPausedToast(){
        Toast toast = Toast.makeText(this, "Timer paused", Toast.LENGTH_LONG);
        toast.show();
    }

    public void deleteItem(int position) {
        incentivesFragment.deleteItem(position);
        closeEditIncentiveFragment();
    }

    public void saveChanges(String newName, int newChance, int position) {
        incentivesFragment.changeItemStats(newName, newChance, position);
        closeEditIncentiveFragment();
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (newIncentiveFragment != null) {
            closeNewIncentiveFragment();

        } else if (editIncentiveFragment != null) {
            closeEditIncentiveFragment();

        } else super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void pauseTimerFragmentChange() {
        timerFragment.pauseTimerFragmentChange();
    }
}
