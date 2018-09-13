package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
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
        super.onBackPressed();
    }

}
