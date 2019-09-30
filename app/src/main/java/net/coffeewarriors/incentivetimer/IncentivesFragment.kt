package net.coffeewarriors.incentivetimer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
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

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IncentivesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [IncentivesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncentivesFragment :
    Fragment() {
    private var mAdView: AdView? = null
    // TODO: Rename and change types of parameters
    private var mIncentiveList: ArrayList<IncentiveItem>? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var adapter: IncentiveAdapter? = null
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mIncentiveList =
                getArguments().getSerializable(INCENTIVE_LIST_PARAM)
        }
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inf: View = inflater.inflate(R.layout.fragment_incentives, container, false)
        loadIncentiveList()
        val addIncentiveButton: FloatingActionButton = inf.findViewById(R.id.floatingActionButton)
        addIncentiveButton.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                clickAddIncentiveButton()
            }
        })
        val incentiveListView: ListView = inf.findViewById(R.id.incentive_listview)
        adapter = IncentiveAdapter(getActivity(), mIncentiveList)
        incentiveListView.setAdapter(adapter)
        val emptyListText: TextView = inf.findViewById(R.id.empty_list_text)
        incentiveListView.setEmptyView(emptyListText)
        incentiveListView.setOnItemClickListener(object : OnItemClickListener() {
            fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                mListener!!.listItemClick(i)
            }
        })
        MobileAds.initialize(getActivity(), "ca-app-pub-8523485353762681/1628269223")
        mAdView = inf.findViewById(R.id.adView) as AdView
        val adRequest: AdRequest = Builder()
            .addTestDevice("4530E445A10D980D67496DBFF2FD1EF7")
            .build()
        mAdView.loadAd(adRequest)
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

        fun openNewIncentiveFragment()
        fun listItemClick(position: Int)
        fun pauseTimerFragmentChange()
    }

    fun clickAddIncentiveButton() {
        mListener!!.openNewIncentiveFragment()
    }

    fun addNewIncentive(name: String?, chance: Int) {
        mIncentiveList!!.add(IncentiveItem(name, chance))
        adapter.notifyDataSetChanged()
        saveIncentiveList()
    }

    fun getCurrentName(position: Int): String? {
        return mIncentiveList!![position].getName()
    }

    fun getCurrentChance(position: Int): Int {
        return mIncentiveList!![position].getChance()
    }

    fun deleteItem(position: Int) {
        mIncentiveList!!.removeAt(position)
        adapter.notifyDataSetChanged()
        saveIncentiveList()
    }

    fun changeItemStats(newName: String?, newChance: Int, position: Int) {
        mIncentiveList!![position].setName(newName)
        mIncentiveList!![position].setChance(newChance)
        mIncentiveList!![position].lock()
        adapter.notifyDataSetChanged()
        saveIncentiveList()
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val INCENTIVE_LIST_PARAM = "INCENTIVE_LIST_PARAM"

        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: ArrayList<IncentiveItem?>?): IncentivesFragment {
            val fragment = IncentivesFragment()
            val args = Bundle()
            args.putSerializable(INCENTIVE_LIST_PARAM, param1)
            fragment.setArguments(args)
            return fragment
        }
    }
}