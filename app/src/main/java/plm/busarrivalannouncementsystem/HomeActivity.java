package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    // [START declare_auth]
    private FirebaseAuth mAuth;

    // [END declare_auth]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.logoutButton).setOnClickListener(this);
        findViewById(R.id.newRouteButton).setOnClickListener(this);
        mAuth= FirebaseAuth.getInstance();
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(HomeActivity.this, currentUser.getEmail(),
//                Toast.LENGTH_SHORT).show();
    }
    // [END on_start_check_user]

    private void logout(){
        mAuth.signOut();
        Intent logout= new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(logout);
        finish();
    }

    private void newRoute(){
        Intent newRoute = new Intent (HomeActivity.this,ViewProfileActivity.class);
        startActivity(newRoute);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.logoutButton) {
            logout();
        }
        if (i == R.id.newRouteButton){
            newRoute();
        }

    }
}
