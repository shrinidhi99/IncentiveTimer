package net.coffeewarriors.incentivetimer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditIncentiveFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditIncentiveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditIncentiveFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mSelectedName: String? = null
    private var mSelectedChance = 0
    private var mPosition = 0
    private var chanceBar: SeekBar? = null
    private var name: EditText? = null
    private var mListener: OnFragmentInteractionListener? = null
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mSelectedName = getArguments().getString(SELECTED_NAME)
            mSelectedChance = getArguments().getInt(SELECTED_CHANCE)
            mPosition = getArguments().getInt(SELECTED_POSITION)
        }
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inf: View = inflater.inflate(R.layout.fragment_edit_incentive, container, false)
        name = inf.findViewById(R.id.edit_name)
        val chanceNumber: TextView = inf.findViewById(R.id.edit_chance_number)
        chanceBar = inf.findViewById(R.id.edit_seekBar)
        name.setText(mSelectedName)
        chanceNumber.setText("$mSelectedChance%")
        chanceBar.setProgress(mSelectedChance)
        val closeImage: ImageView = inf.findViewById(R.id.close_image_edit)
        closeImage.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                closeEditIncentiveFragment()
            }
        })
        val closeText: TextView = inf.findViewById(R.id.close_text_edit)
        closeText.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                closeEditIncentiveFragment()
            }
        })
        val deleteImage: ImageView = inf.findViewById(R.id.delete_image)
        deleteImage.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                deleteItem()
            }
        })
        val deleteText: TextView = inf.findViewById(R.id.delete_text)
        deleteText.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                deleteItem()
            }
        })
        val saveButton: Button = inf.findViewById(R.id.save_incentive_button)
        saveButton.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                saveChanges()
            }
        })
        chanceBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                chanceNumber.setText("$i%")
            }

            fun onStartTrackingTouch(seekBar: SeekBar?) {}
            fun onStopTrackingTouch(seekBar: SeekBar?) {}
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)

        fun closeEditIncentiveFragment()
        fun deleteItem(position: Int)
        fun saveChanges(newName: String?, newChance: Int, position: Int)
    }

    fun closeEditIncentiveFragment() {
        mListener!!.closeEditIncentiveFragment()
    }

    fun deleteItem() {
        mListener!!.deleteItem(mPosition)
    }

    fun saveChanges() {
        mListener!!.saveChanges(name.getText().toString(), chanceBar.getProgress(), mPosition)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val SELECTED_NAME = "SELECTED_NAME"
        private const val SELECTED_CHANCE = "SELECTED_CHANCE"
        private const val SELECTED_POSITION = "SELECTED_POSITION"
        // TODO: Rename and change types and number of parameters
        fun newInstance(
            selectedName: String?,
            selectedChance: Int,
            position: Int
        ): EditIncentiveFragment {
            val fragment = EditIncentiveFragment()
            val args = Bundle()
            args.putString(SELECTED_NAME, selectedName)
            args.putInt(SELECTED_CHANCE, selectedChance)
            args.putInt(SELECTED_POSITION, position)
            fragment.setArguments(args)
            return fragment
        }
    }
}