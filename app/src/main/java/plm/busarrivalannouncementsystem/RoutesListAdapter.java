package plm.busarrivalannouncementsystem;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RoutesListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the list of countries
    private final String[] nameArray;

    //to store the list of countries
    private final String[] infoArray;


    public RoutesListAdapter(Activity context, String[] nameArrayParam, String[] infoArrayParam) {
        super(context, R.layout.listview_routes, nameArrayParam);
        this.context = context;
        this.nameArray = nameArrayParam;
        this.infoArray = infoArrayParam;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_routes, null, true);
        TextView routeName = (TextView) rowView.findViewById(R.id.routeNameTextView);
        TextView routeInfo = (TextView) rowView.findViewById(R.id.routeInfoTextView);
        routeName.setText(nameArray[position]);
        routeInfo.setText(infoArray[position]);
        return rowView;
    }
}
