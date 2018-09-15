package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener{
    TextView mCompanyField;
    TextView mMissionField;
    TextView mVisionField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        displayDrawer();
        mCompanyField=findViewById(R.id.editCompanyTextView);
        mMissionField=findViewById(R.id.editMissionTextView);
        mVisionField=findViewById(R.id.editVisionTextView);
        findViewById(R.id.saveEditProfileButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.saveEditProfileButton){
            FirebaseUser user = mAuth.getCurrentUser();
            String userId=user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
            myRef.child(userId).child("company").setValue(mCompanyField.getText().toString());
            myRef.child(userId).child("mission").setValue(mMissionField.getText().toString());
            myRef.child(userId).child("vision").setValue(mVisionField.getText().toString());
            startActivity(new Intent(this,HomeActivity.class));
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
