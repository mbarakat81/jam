package jam.mbarakat.com.myshares.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.SharesModel;
import jam.mbarakat.com.myshares.adapters.SharesAdapter;

public class CreateNewJamActivity extends AppCompatActivity {
    Button btnNewJamPaticipants;
    EditText txtNewJamName, txtNewJamAmount, txtNewJamDate, txtSharesNo, txtSharePeriod;
    Spinner periodSpinner ;
    JamModel jamModel = new JamModel();
    private SharesAdapter adapter;
    static final int DATE_DIALOG_ID = 0;
    private int mYear,mMonth,mDay;
    int jamSharePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_jam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        periodSpinner = (Spinner)findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.period_array
                , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        periodSpinner.setAdapter(adapter);
        btnNewJamPaticipants = (Button) findViewById(R.id.btnNewJamPaticipants);
        txtNewJamName = (EditText) findViewById(R.id.txtNewJamName1);
        txtNewJamAmount = (EditText) findViewById(R.id.txtNewJamAmount1);
        txtNewJamDate = (EditText) findViewById(R.id.txtNewJamDate1);
        txtSharesNo = (EditText) findViewById(R.id.txtSharesNo);
        txtSharePeriod = (EditText) findViewById(R.id.txtSharePeriod);
        final String ownerId =  getIntent().getExtras().getString("USER_ID").toString();
        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        mYear=c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtNewJamDate.setText( sdf.format(c.getTime()));
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPeriod);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    jamSharePeriod = 31;
                }
                else if(position==1){
                    jamSharePeriod=7;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtNewJamDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);

            }
        });
        btnNewJamPaticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save New Jam
            }
        });
    }
    public static List<SharesModel> getData(int count){
        List<SharesModel> sharesModels = new ArrayList<>();
        for(int i=0 ; i<=count;i++){
            sharesModels.add(new SharesModel());
        }

        return sharesModels;
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }

        return null;

    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            txtNewJamDate.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));

        }

    };

}
