package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth;

    private String userId;
    TextView companyTextView;
    TextView missionTextView;
    TextView visionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId=user.getUid();
        companyTextView = findViewById(R.id.profileCompanyTextView);
        missionTextView= findViewById(R.id.profileMissionTextView);
        visionTextView= findViewById(R.id.profileVisionTextView);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        companyTextView.setText(ds.child(userId).child("company").getValue(String.class));
                        missionTextView.setText(ds.child(userId).child("mission").getValue(String.class));
                        visionTextView.setText(ds.child(userId).child("vision").getValue(String.class));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        displayDrawer();



        super.onCreate(savedInstanceState);
        findViewById(R.id.editProfileButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.editProfileButton){
            startActivity(new Intent (this,EditProfileActivity.class));
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

}
