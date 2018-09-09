package plm.busarrivalannouncementsystem;

import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        displayDrawer();
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.newRouteButton){
//            newRoute();
//        }

    }


}
