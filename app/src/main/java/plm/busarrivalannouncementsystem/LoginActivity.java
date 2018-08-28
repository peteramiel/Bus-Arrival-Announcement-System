package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;

    // [START declare_auth]
    private FirebaseAuth mAuth;

    // [END declare_auth]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Views
        mEmailField = findViewById(R.id.userNameEditText);
        mPasswordField = findViewById(R.id.passwordEditText);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.userNameSignInWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordSignInWrapper);
        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");
        // Buttons
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.goSignUpButton).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        Toast.makeText(LoginActivity.this, currentUser.getEmail().toString(),
//                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartUser:" + currentUser);
        if(currentUser== null){

        }
       else{
            Intent gotoHome = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(gotoHome);
        }

    }
    // [END on_start_check_user]


    private void signIn(String email, String password) {
//        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent gotoHome = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(gotoHome);

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                        hideProgressDialog();
                    }
                });

        // [END sign_in_with_email]

    }

    private void goSignUp() {
        Intent gotoSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(gotoSignUp);
        finish();
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.signInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.goSignUpButton){
            goSignUp();
        }

//          else if (i == R.id.verify_email_button) {
//            sendEmailVerification();
//        }
    }


}
