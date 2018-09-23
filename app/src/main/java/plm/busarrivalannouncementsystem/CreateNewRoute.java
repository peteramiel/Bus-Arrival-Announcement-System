package plm.busarrivalannouncementsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateNewRoute extends BaseActivity implements View.OnClickListener {
    private EditText routeNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_route);
        displayDrawer();
        findViewById(R.id.setTerminalsButton).setOnClickListener(this);
        routeNameEditText = findViewById(R.id.routeNameEditText);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.setTerminalsButton) {
            String routeName= routeNameEditText.getText().toString();
            Intent setTerminals= new Intent(CreateNewRoute.this,CreateTerminals.class);
            setTerminals.putExtra("routeName",routeName);
            startActivity(setTerminals);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CreateNewRoute.this,HomeActivity.class));
        finish();
    }
}
