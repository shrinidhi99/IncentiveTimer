package net.coffeewarriors.incentivetimer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewIncentiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewIncentiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewIncentiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewIncentiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewIncentiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewIncentiveFragment newInstance(String param1, String param2) {
        NewIncentiveFragment fragment = new NewIncentiveFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inf = inflater.inflate(R.layout.fragment_new_incentive, container, false);

        Button addIncentive = inf.findViewById(R.id.add_incentive_button);
        addIncentive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewIncentiveButton();
            }
        });

        ImageView closeImage = inf.findViewById(R.id.close_image_new);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeNewIncentiveFragment();
            }
        });

        TextView closeText = inf.findViewById(R.id.close_text_new);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeNewIncentiveFragment();
            }
        });

        final TextView chanceNumber = inf.findViewById(R.id.new_incentive_chance_number);

        final SeekBar chanceBar = inf.findViewById(R.id.seekBar);
        chanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                chanceNumber.setText("" + i + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button add = inf.findViewById(R.id.add_incentive_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameInput = inf.findViewById(R.id.editText);

                mListener.addNewIncentiveButton(nameInput.getText().toString(), chanceBar.getProgress());
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
        void closeNewIncentiveFragment();
        void addNewIncentiveButton(String name, int chance);
    }

    public void addNewIncentiveButton() {

    }

    public void closeNewIncentiveFragment() {
        mListener.closeNewIncentiveFragment();
    }


}
