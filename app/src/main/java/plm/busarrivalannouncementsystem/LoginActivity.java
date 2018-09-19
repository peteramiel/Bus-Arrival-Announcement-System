package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;
    private Dialog popupNoInternet;
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
        usernameWrapper = findViewById(R.id.userNameSignInWrapper);
        passwordWrapper = findViewById(R.id.passwordSignInWrapper);
        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");
        // Buttons
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.goSignUpButton).setOnClickListener(this);
//        findViewById(R.id.retryConnectionButton).setOnClickListener(this);
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
        checkConnection();
        Log.d(TAG, "onStartUser:" + currentUser);
        if (currentUser == null) {

        } else {

            Intent gotoHome = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(gotoHome);
            finish();
        }


    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.goSignUpButton) {
            goSignUp();
        }

//          else if (i == R.id.verify_email_button) {
//            sendEmailVerification();
//        }
    }

    private void signIn(String email, String password) {
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
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String userId = user.getUid();
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        SharedPreferences userPref = getSharedPreferences("User", 0);
                                        SharedPreferences.Editor editor = userPref.edit();
                                        editor.putString("company", ds.child(userId).child("company").getValue(String.class));
                                        editor.putString("email", ds.child(userId).child("email").getValue(String.class));
                                        editor.apply();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
            usernameWrapper.setError("Required");
            valid = false;
        } else {
            usernameWrapper.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordWrapper.setError("Required");
            valid = false;
        } else {
            passwordWrapper.setError(null);
        }

        return valid;
    }


    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, HomeActivity.class));
//        CREATE A DIALOG BOX TO EXIT
        super.onBackPressed();
    }

}
