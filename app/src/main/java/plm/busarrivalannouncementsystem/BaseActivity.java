package plm.busarrivalannouncementsystem;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
        } else if (i == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayoutHome);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        mAuth.signOut();
        Intent logout = new Intent(this, LoginActivity.class);
        startActivity(logout);
        finish();
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