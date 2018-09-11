package plm.busarrivalannouncementsystem;

import android.os.Bundle;
import android.view.View;

public class HelpActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_help);
        displayDrawer();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }
}
