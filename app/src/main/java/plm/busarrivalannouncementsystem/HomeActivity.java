package plm.busarrivalannouncementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        displayDrawer();
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        TextView welcomeTextView = findViewById(R.id.tv_welcome_message);
        welcomeTextView.setText(R.string.new_user_welcome_message);
    }




    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.newRouteButton){
//            newRoute();
//        }

    }




}
