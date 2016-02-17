package jam.mbarakat.com.myshares.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.adapters.SharesAdapter;

public class ViewJamDetailsActivity extends AppCompatActivity {
    TextView txtNewJamName, txtNextOwner, txtNextDueDate;
    JamModel jamModel = new JamModel();
    private SharesAdapter adapter;
    static final int DATE_DIALOG_ID = 0;
    private int mYear,mMonth,mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jam_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtNewJamName = (TextView) findViewById(R.id.txtNewJamName);
        txtNextOwner = (TextView) findViewById(R.id.txtNextOwner);
        txtNextDueDate = (TextView) findViewById(R.id.txtNextDueDate);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
