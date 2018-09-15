package plm.busarrivalannouncementsystem;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public FirebaseAuth mAuth;
    public Dialog popupNoInternet;

    //Renders the Drawer
    void displayDrawer() {

        Toolbar mToolbar = findViewById(R.id.nav_action_bar);
        setSupportActionBar(mToolbar);
        DrawerLayout mDrawerLayout = findViewById(R.id.drawerLayoutHome);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences userPref = getSharedPreferences("User", 0);
        final String company = userPref.getString("company", "");
        final String email = userPref.getString("email", "");
        View headerView = navigationView.getHeaderView(0);
        TextView navCompany = headerView.findViewById(R.id.headerCompanyTextView);
        TextView navEmail =  headerView.findViewById(R.id.headerEmailTextView);
        navCompany.setText(company);
        navEmail.setText(email);
    }

    public boolean isServicesOK(){
        String TAG = "Base Activity";
        final int ERROR_DIALOG_REQUEST = 9001;
        Log.d(TAG,"isServicesOK: Checking Google Services if OK");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google play services is available");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: Solvable Error has been found");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(this,
                            available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"Can't make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        popupNoInternet = new Dialog(this);
        popupNoInternet.setContentView(R.layout.popup_no_internet);

        checkConnection();
        Button popupNoInternetButton= popupNoInternet.findViewById(R.id.retryConnectionButton);
        popupNoInternetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkConnection();
            }
        });

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public boolean checkInternetConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }

        return connected;
    }

    public void checkConnection() {

        if (!checkInternetConnection()) {
            Toast.makeText(getApplicationContext(), "Network error. Check your network connection and try again.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(popupNoInternet.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            popupNoInternet.show();


        } else {
            popupNoInternet.dismiss();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.id.nav_home) {
            viewHome();
        } else if (i == R.id.nav_profile) {
            viewProfile();
        } else if (i == R.id.nav_find) {
            viewFind();
        } else if (i == R.id.nav_help) {
            viewHelp();
        }else if (i == R.id.nav_bluetooth) {
            viewBluetoothConnect();
        } else if (i == R.id.nav_logout) {
            logout(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayoutHome);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void viewBluetoothConnect() {
        startActivity(new Intent(this,ConnectBluetoothModule.class));
        finish();
    }

    private void logout(final Activity act) {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences userPref = getSharedPreferences("User", 0);
                        SharedPreferences.Editor editor = userPref.edit();
                        editor.clear();
                        editor.apply();
                        mAuth.signOut();
                        act.startActivity(new Intent(act,LoginActivity.class));
                        finish();
                    }
                }).create().show();

    }

    private void viewHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void viewProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

    private void viewFind() {
        startActivity(new Intent(this, FindActivity.class));

    }

    private void viewHelp() {
        startActivity(new Intent(this, HelpActivity.class));
        finish();
    }

}