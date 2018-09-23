package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText oldPass, newPass, verifyPass;
    private TextInputLayout oldpassWrapper, newpassWrapper, verifypassWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        displayDrawer();
        findViewById(R.id.btChangePasswordSave).setOnClickListener(this);
        findViewById(R.id.btChangePasswordCancel).setOnClickListener(this);
        newPass = findViewById(R.id.etChangePasswordNewPassword);
        oldPass = findViewById(R.id.etChangePasswordOldPassword);
        verifyPass = findViewById(R.id.etChangePasswordVerifyPassword);

        oldpassWrapper = findViewById(R.id.changepasswordOldWrapper);
        newpassWrapper = findViewById(R.id.changepasswordNewWrapper);
        verifypassWrapper = findViewById(R.id.changepasswordVerifyWrapper);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btChangePasswordSave) {
            savePassword();
        } else if (i == R.id.btChangePasswordCancel) {
            onBackPressed();
            finish();
        }
    }

    private boolean checkValidity() {
        boolean valid = true;


        String newpass = newPass.getText().toString();
        String verifypass = verifyPass.getText().toString();
        if (!newpass.equals(verifypass)) {
            verifypassWrapper.setError("Passwords do not match. Please enter your desired password again.");
            valid = false;
        } else {
            verifypassWrapper.setError(null);
        }


        return valid;

    }

    private void savePassword() {
        if(!checkValidity()){
            return;
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String oldpass = oldPass.getText().toString();
// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final String TAG = "ChangePassword";
                String newpass = newPass.getText().toString();
                if (task.isSuccessful()) {
                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                                Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ChangePasswordActivity.this,EditProfileActivity.class));
                                finish();
                            } else {
                                Log.d(TAG, "Error password not updated");
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error auth failed");
                    Toast.makeText(ChangePasswordActivity.this, "Old Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}