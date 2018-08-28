package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRetypePasswordField;
    private EditText mCompanyField;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;
    private TextInputLayout companyWrapper;
    private TextInputLayout retypeWrapper;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameWrapper = findViewById(R.id.userNameSignUpWrapper);
        passwordWrapper = findViewById(R.id.passwordSignUpWrapper);
        companyWrapper = findViewById(R.id.companySignUpWrapper);
        retypeWrapper = findViewById(R.id.retypePasswordSignUpWrapper);
        companyWrapper.setHint("Company Name");
        retypeWrapper.setHint("Re-type Password");
        usernameWrapper.setHint("Email");
        passwordWrapper.setHint("Password");
        mAuth = FirebaseAuth.getInstance();
        mPasswordField=findViewById(R.id.passwordSignUpEditText);
        mEmailField=findViewById(R.id.userNameSignUpEditText);
        mRetypePasswordField=findViewById(R.id.retypePasswordSignUpEditText);
        mCompanyField=findViewById(R.id.companySignUpEditText);
        // Write a message to the database

        findViewById(R.id.signUpButton).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        String company = mCompanyField.getText().toString();
        if (TextUtils.isEmpty(company)) {
            companyWrapper.setError("Required");
            valid = false;
        } else {
            companyWrapper.setError(null);
        }
        String retypePassword = mRetypePasswordField.getText().toString();
        if (TextUtils.isEmpty(retypePassword)) {
            retypeWrapper.setError("Required");
            valid = false;
        } else {
            retypeWrapper.setError(null);
        }
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordWrapper.setError("Required");
            valid = false;
        } else {
            passwordWrapper.setError(null);
        }

        if(!password.equals(retypePassword)){
            retypeWrapper.setError("Passwords do not match. Please enter your desired password again");
            valid=false;
        }

        return valid;
    }

    public void createAccount(String email, String password){
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            myRef.setValue(mCompanyField.getText().toString());
                            myRef.child("company").push().setValue(mCompanyField.getText().toString());
                            myRef.child("email").push().setValue(mEmailField.getText().toString());
                            Intent signInNewUser = new Intent(SignUpActivity.this,HomeActivity.class);
                            startActivity(signInNewUser);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        hideProgressDialog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.email_create_account_button) {
//            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        if (i == R.id.signUpButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }

//          else if (i == R.id.verify_email_button) {
//            sendEmailVerification();
//        }
    }
}
