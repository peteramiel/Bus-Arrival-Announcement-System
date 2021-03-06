package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
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


public class HomeActivity extends BaseActivity implements View.OnClickListener {
    final String TAG = "HomeActivity";
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
    FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        displayDrawer();

        if (isServicesOK()) {

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
//        startActivity(new Intent(this, HomeActivity.class));
//        finish();
        //SHOULD LOGOUT
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences userPref = getSharedPreferences("User", 0);
                        SharedPreferences.Editor editor = userPref.edit();
                        editor.clear();
                        editor.apply();
                        mAuth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                }).create().show();
;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.newRouteButton){
//            newRoute();
//        }

    }


}
