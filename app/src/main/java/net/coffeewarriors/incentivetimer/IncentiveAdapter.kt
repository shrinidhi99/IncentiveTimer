package net.coffeewarriors.incentivetimer

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import jdk.internal.jline.internal.Nullable
import java.util.*

class IncentiveAdapter(context: Activity?, incentiveList: ArrayList<IncentiveItem?>?) :
    ArrayAdapter<IncentiveItem?>(context, 0, incentiveList) {
    @NonNull
    fun getView(position: Int, @Nullable convertView: View?, @NonNull parent: ViewGroup?): View? {
        var listItemView: View? = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.incentive_item, parent, false)
        }
        val currentIncentiveItem: IncentiveItem = getItem(position)
        val name: TextView = listItemView.findViewById(R.id.incentive_name)
        val chance: TextView = listItemView.findViewById(R.id.chance_text)
        val star: ImageView = listItemView.findViewById(R.id.star_image)
        val unlocked: TextView = listItemView.findViewById(R.id.unlocked_text)
        val chanceNr: TextView = listItemView.findViewById(R.id.chance_number)
        if (currentIncentiveItem.getName().trim().length() <= 0) {
            name.setText(getContext().getString(R.string.no_name))
        } else name.setText(currentIncentiveItem.getName())
        chance.setText(getContext().getString(R.string.chance).toString() + " " + currentIncentiveItem.getChance().toString() + "%")
        if (currentIncentiveItem.isUnlocked()) {
            star.setImageResource(R.drawable.ic_star_unlocked)
            unlocked.setVisibility(View.VISIBLE)
            chance.setVisibility(View.INVISIBLE)
            chanceNr.setVisibility(View.INVISIBLE)
        } else {
            star.setImageResource(R.drawable.ic_star_locked)
            unlocked.setVisibility(View.INVISIBLE)
            chance.setVisibility(View.VISIBLE)
            chanceNr.setVisibility(View.VISIBLE)
        }
        return listItemView
    }
}