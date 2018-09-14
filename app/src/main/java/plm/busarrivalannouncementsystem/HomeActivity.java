package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeActivity extends BaseActivity implements View.OnClickListener{
    final String TAG = "HomeActivity";
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
    FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId=user.getUid();

        displayDrawer();
        if(isServicesOK()){

        }
        super.onCreate(savedInstanceState);
    }




    @Override
    public void onStart() {
        super.onStart();
        TextView welcomeTextView = findViewById(R.id.tv_welcome_message);
        welcomeTextView.setText(R.string.new_user_welcome_message);
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.newRouteButton){
//            newRoute();
//        }

    }




}
