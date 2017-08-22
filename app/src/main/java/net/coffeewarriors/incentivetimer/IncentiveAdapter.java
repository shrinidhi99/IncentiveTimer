package net.coffeewarriors.incentivetimer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class IncentiveAdapter extends ArrayAdapter<IncentiveItem> {


    public IncentiveAdapter(Activity context, ArrayList<IncentiveItem> incentiveList) {
        super(context, 0, incentiveList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.incentive_item, parent, false);
        }

        IncentiveItem currentIncentiveItem = getItem(position);

        TextView name = listItemView.findViewById(R.id.incentive_name);
        TextView chance = listItemView.findViewById(R.id.chance_text);
        ImageView star = listItemView.findViewById(R.id.star_image);
        TextView unlocked = listItemView.findViewById(R.id.unlocked_text);
        TextView chanceNr = listItemView.findViewById(R.id.chance_number);

        if (!(currentIncentiveItem.getName().trim().length() > 0)) {
            name.setText(getContext().getString(R.string.no_name));
        } else name.setText(currentIncentiveItem.getName());

        chance.setText(getContext().getString(R.string.chance) + " " + currentIncentiveItem.getChance() + "%");

        if (currentIncentiveItem.isUnlocked()) {
            star.setImageResource(R.drawable.ic_star_unlocked);
            unlocked.setVisibility(View.VISIBLE);
            chance.setVisibility(View.INVISIBLE);
            chanceNr.setVisibility(View.INVISIBLE);
        } else {
            star.setImageResource(R.drawable.ic_star_locked);
            unlocked.setVisibility(View.INVISIBLE);
            chance.setVisibility(View.VISIBLE);
            chanceNr.setVisibility(View.VISIBLE);
        }

        return listItemView;
    }
}
