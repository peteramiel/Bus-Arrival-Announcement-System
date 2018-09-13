package plm.busarrivalannouncementsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EditProfileActivity extends BaseActivity {

    Button bt_EditProfileChangePassword;
    Button bt_EditProfileSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        displayDrawer();

        bt_EditProfileChangePassword = findViewById(R.id.btEditProfileChangePassword);
        bt_EditProfileSave = findViewById(R.id.btEditProfileSave);

        bt_EditProfileChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditProfileActivity.this,ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        bt_EditProfileSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

    }
}
