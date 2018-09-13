package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class MapsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        displayDrawer();
        String routeName = getIntent().getStringExtra("routeName");
        TextView routeNameTextView= findViewById(R.id.routeNameTextView);
        routeNameTextView.setText(routeName);
    }

}
