package net.coffeewarriors.incentivetimer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditIncentiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditIncentiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditIncentiveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SELECTED_NAME = "SELECTED_NAME";
    private static final String SELECTED_CHANCE = "SELECTED_CHANCE";
    private static final String SELECTED_POSITION = "SELECTED_POSITION";

    // TODO: Rename and change types of parameters
    private String mSelectedName;
    private int mSelectedChance;
    private int mPosition;

    private SeekBar chanceBar;
    private EditText name;

    private OnFragmentInteractionListener mListener;

    public EditIncentiveFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditIncentiveFragment newInstance(String selectedName, int selectedChance, int position) {
        EditIncentiveFragment fragment = new EditIncentiveFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_NAME, selectedName);
        args.putInt(SELECTED_CHANCE, selectedChance);
        args.putInt(SELECTED_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSelectedName = getArguments().getString(SELECTED_NAME);
            mSelectedChance = getArguments().getInt(SELECTED_CHANCE);
            mPosition = getArguments().getInt(SELECTED_POSITION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.fragment_edit_incentive, container, false);

        name = inf.findViewById(R.id.edit_name);
        final TextView chanceNumber = inf.findViewById(R.id.edit_chance_number);
        chanceBar = inf.findViewById(R.id.edit_seekBar);

        name.setText(mSelectedName);
        chanceNumber.setText("" + mSelectedChance + "%");
        chanceBar.setProgress(mSelectedChance);

        ImageView closeImage = inf.findViewById(R.id.close_image_edit);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditIncentiveFragment();
            }
        });

        TextView closeText = inf.findViewById(R.id.close_text_edit);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeEditIncentiveFragment();
            }
        });

        ImageView deleteImage = inf.findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        TextView deleteText = inf.findViewById(R.id.delete_text);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        Button saveButton = inf.findViewById(R.id.save_incentive_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void closeEditIncentiveFragment();
        void deleteItem(int position);
        void saveChanges(String newName, int newChance, int position);
    }

    public void closeEditIncentiveFragment() {
        mListener.closeEditIncentiveFragment();
    }

    public void deleteItem() {
        mListener.deleteItem(mPosition);
    }

    public void saveChanges() {

        mListener.saveChanges(name.getText().toString(), chanceBar.getProgress(), mPosition);
    }
}
