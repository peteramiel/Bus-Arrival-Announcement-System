package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {
    EditText mCompanyField;
    EditText mMissionField;
    EditText mVisionField;
    TextInputLayout missionWrapper, visionWrapper, companyWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        displayDrawer();
        mCompanyField = findViewById(R.id.editCompanyTextView);
        mMissionField = findViewById(R.id.editMissionTextView);
        mVisionField = findViewById(R.id.editVisionTextView);
        companyWrapper = findViewById(R.id.editCompanyWrapper);
        missionWrapper = findViewById(R.id.editMissionWrapper);
        visionWrapper = findViewById(R.id.editVisionWrapper);
        findViewById(R.id.saveEditProfileButton).setOnClickListener(this);
        findViewById(R.id.changepasswordEditProfileButton).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.saveEditProfileButton) {
            saveChanges(mCompanyField.getText().toString(),mMissionField.getText().toString(),mVisionField.getText().toString());
        }
        else if(i == R.id.changepasswordEditProfileButton) {
            changePassword();
        }
    }

    private void saveChanges(String company, String mission, String vision){
        if(!validateForm()){
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        myRef.child(userId).child("company").setValue(company);
        myRef.child(userId).child("mission").setValue(mission);
        myRef.child(userId).child("vision").setValue(vision);
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void changePassword(){
        Intent i=new Intent(this,ChangePasswordActivity.class);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private boolean validateForm() {
        boolean valid = true;

        String company = mCompanyField.getText().toString();
        if (TextUtils.isEmpty(company)) {
            companyWrapper.setError("Required");
            valid = false;
        } else {
            companyWrapper.setError(null);
        }

        String mission = mMissionField.getText().toString();
        if (TextUtils.isEmpty(mission)) {
            missionWrapper.setError("Required");
            valid = false;
        } else {
            missionWrapper.setError(null);
        }

        String vision = mVisionField.getText().toString();
        if (TextUtils.isEmpty(vision)) {
            visionWrapper.setError("Required");
            valid = false;
        } else {
            visionWrapper.setError(null);
        }

        return valid;
    }

}
