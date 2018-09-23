package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText forgotpassEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViewById(R.id.btForgotPasswordCancel).setOnClickListener(this);
        findViewById(R.id.btForgotPasswordSendEmail).setOnClickListener(this);
        forgotpassEmail = findViewById(R.id.etForgotPasswordEmail);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btForgotPasswordSendEmail) {
            sendEmail();
        } else if (i == R.id.btForgotPasswordCancel) {
            onBackPressed();
            finish();
        }
    }


    private void sendEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = forgotpassEmail.getText().toString();

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String TAG = "ForgotPassword";
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(ForgotPasswordActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
