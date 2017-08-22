package net.coffeewarriors.incentivetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static net.coffeewarriors.incentivetimer.MainActivity.INCENTIVE_LIST;
import static net.coffeewarriors.incentivetimer.MainActivity.SHARED_PREFERENCES;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IncentivesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IncentivesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncentivesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String INCENTIVE_LIST_PARAM = "INCENTIVE_LIST_PARAM";

    // TODO: Rename and change types of parameters
    private ArrayList<IncentiveItem> mIncentiveList;

    private OnFragmentInteractionListener mListener;

    private IncentiveAdapter adapter;

    public IncentivesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static IncentivesFragment newInstance(ArrayList<IncentiveItem> param1) {
        IncentivesFragment fragment = new IncentivesFragment();
        Bundle args = new Bundle();
        args.putSerializable(INCENTIVE_LIST_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIncentiveList = (ArrayList<IncentiveItem>) getArguments().getSerializable(INCENTIVE_LIST_PARAM);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.fragment_incentives, container, false);

        loadIncentiveList();


        FloatingActionButton addIncentiveButton = inf.findViewById(R.id.floatingActionButton);
        addIncentiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAddIncentiveButton();
            }
        });


        ListView incentiveListView = inf.findViewById(R.id.incentive_listview);
        adapter = new IncentiveAdapter(getActivity(), mIncentiveList);

        incentiveListView.setAdapter(adapter);

        TextView emptyListText = inf.findViewById(R.id.empty_list_text);
        incentiveListView.setEmptyView(emptyListText);

        incentiveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.listItemClick(i);
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
        void openNewIncentiveFragment();
        void listItemClick(int position);
        void pauseTimerFragmentChange();
    }


    public void clickAddIncentiveButton() {
        mListener.openNewIncentiveFragment();
    }

    public void addNewIncentive(String name, int chance) {
        mIncentiveList.add(new IncentiveItem(name, chance));
        adapter.notifyDataSetChanged();
        saveIncentiveList();
    }

    public String getCurrentName(int position) {
        return mIncentiveList.get(position).getName();
    }

    public int getCurrentChance(int position) {
        return mIncentiveList.get(position).getChance();
    }

    public void deleteItem(int position) {
        mIncentiveList.remove(position);
        adapter.notifyDataSetChanged();
        saveIncentiveList();
    }

    public void changeItemStats(String newName, int newChance, int position) {
        mIncentiveList.get(position).setName(newName);
        mIncentiveList.get(position).setChance(newChance);
        mIncentiveList.get(position).lock();
        adapter.notifyDataSetChanged();
        saveIncentiveList();
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

        if (mIncentiveList == null) mIncentiveList = new ArrayList<>();
    }



}
