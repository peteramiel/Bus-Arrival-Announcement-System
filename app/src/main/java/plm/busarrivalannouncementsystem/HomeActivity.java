package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends BaseActivity implements View.OnClickListener{
    final String TAG = "HomeActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        displayDrawer();
        if(isServicesOK()){

        }
        super.onCreate(savedInstanceState);
    }

    public boolean isServicesOK(){

        Log.d(TAG,"isServicesOK: Checking Google Services if OK");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google play services is available");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: Solvable Error has been found");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(HomeActivity.this,
                    available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"Can't make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView welcomeTextView = findViewById(R.id.tv_welcome_message);
        welcomeTextView.setText(R.string.new_user_welcome_message);
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.newRouteButton){
//            newRoute();
//        }

    }




}
