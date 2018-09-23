package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.net.UrlEscapers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FindActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    List<String> routeNameArrayList;
    List<String> routeTerminalArrayList;

//    String[] nameArray = {"Route 1", "Balintawak LRT - EDSA LRT", "Route 3", "Route 4", "Alternate Route for Route 3", "Route 5"};
//
//    String[] infoArray = {
//            "Sta. Cruz - Marilao",
//            "Balintawak LRT - EDSA LRT",
//            "Balintawak LRT - EDSA LRT",
//            "Monumento - Mabini",
//            "Balintawak LRT - EDSA LRT",
//            "Monumento - Balintawak."
//    };
    String[] nameArray;
    String[] terminalArray;
    ListView listViewRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_find);
        displayDrawer();
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate: Starting");
        routeNameArrayList= new ArrayList<>();
        routeTerminalArrayList=new ArrayList<>();

        getRoutes();

        if (isServicesOK()) {

        }

        findViewById(R.id.newRouteButton).setOnClickListener(this);
    }

    private void getRoutes(){
        Log.d(TAG, "getRoutes: staring void");
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId=user.getUid();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/"+userId+"/routes");
        Log.d(TAG,"routes reference: "+myRef);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG,"ds value: "+ds.getRef().toString());
                    String newRoute = ds.getKey();
                    Log.d(TAG,"newRoute Name: "+newRoute);
                    String newRouteTerminal1 = ds.child("terminals").child("terminal1").child("name").getValue(String.class);
                    String newRouteTerminal2 = ds.child("terminals").child("terminal2").child("name").getValue(String.class);
                    String routeTerminals= newRouteTerminal1+ " - " +newRouteTerminal2;
                    Log.d(TAG,"newRoute Terminal: "+routeTerminals);
                    routeNameArrayList.add(newRoute);
                    routeTerminalArrayList.add(routeTerminals);
                    nameArray = routeNameArrayList.toArray(new String[0]);
                    terminalArray = routeTerminalArrayList.toArray(new String[0]);
                }
                Log.d(TAG,"routes: creating RoutesListAdapter :" + nameArray);
                if(nameArray==null){
                    String noAvail[]={"No Available Routes"};
                    String noAvailInfo[]={"Information not Available"};
                    RoutesListAdapter routes = new RoutesListAdapter(FindActivity.this, noAvail, noAvailInfo);
                    listViewRoutes = findViewById(R.id.listViewRoutes);
                    listViewRoutes.setAdapter(routes);
                }
                else {
                    RoutesListAdapter routes = new RoutesListAdapter(FindActivity.this, nameArray, terminalArray);
                    listViewRoutes = findViewById(R.id.listViewRoutes);
                    listViewRoutes.setAdapter(routes);
                    Log.d(TAG, "routes: setOnItemClick");
                    listViewRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {

                            Log.d(TAG, "MapsActivity: Starting MapsActivity");
                            Intent intent = new Intent(FindActivity.this, MapsActivity.class);
                            intent.putExtra("routeName", routeNameArrayList.get(position));
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is Working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: a resolvable error has occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int i =view.getId();
        if (i ==R.id.newRouteButton){
            startActivity(new Intent(FindActivity.this,CreateNewRoute.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }
}
