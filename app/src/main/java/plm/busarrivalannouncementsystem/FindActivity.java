package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class FindActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "MapsActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    String[] nameArray = {"Route 1","Balintawak LRT - EDSA LRT","Route 3","Route 4","Alternate Route for Route 3", "Route 5" };

    String[] infoArray = {
            "Sta. Cruz - Marilao",
            "Balintawak LRT - EDSA LRT",
            "Balintawak LRT - EDSA LRT",
            "Monumento - Mabini",
            "Balintawak LRT - EDSA LRT",
            "Monumento - Balintawak."
    };
    ListView listViewRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_find);
        displayDrawer();
        super.onCreate(savedInstanceState);
        if (isServicesOK()){
            RoutesListAdapter routes = new RoutesListAdapter(this, nameArray,infoArray);
            listViewRoutes = findViewById(R.id.listViewRoutes);
            listViewRoutes.setAdapter(routes);
            listViewRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent intent = new Intent(FindActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            });

        }
    }
    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is Working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: a resolvable error has occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }
}
