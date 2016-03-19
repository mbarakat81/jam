package jam.mbarakat.com.myshares.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pushwizard.sdk.PushWizard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.adapters.SectionsPagerAdapter;
import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.helpers.ParseConstants;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.modules.SharesModel;

public class MainActivity extends AppCompatActivity {

    private static final String PUSHWIZARD_APPKEY = "56dddfdaa3fc27053d8b47ec";
    private static final String GOOGLE_PROJECT_NUMBER = "932288062635";


    public static final String TAG = MainActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    final Context context = this;
    Typeface tf;
    int jamSharePeriod;
    Button btnNewJamParticipants;
    EditText txtNewJamName, txtNewJamAmount, txtNewJamDate, txtSharesNo;
    Spinner periodSpinner;
    ProgressDialog progress;
    int mYear, mMonth, mDay;
    JamModel jamModel;
    Dialog dialog;
    static final int DATE_DIALOG_ID = 0;
    ParseUser currentUser;
    List<SharesModel> jamSharesObject;


    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 & getIntent().getExtras() == null) {
            finish();
            return;
        }


        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            Log.i(TAG, currentUser.getUsername());
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jamId = extras.getString("jamId");
            String shareOwner = extras.getString("shareOwner");
            ParseQuery queryShareOwner = ParseQuery.getQuery("Share_owners");
            queryShareOwner.whereEqualTo("objectId", shareOwner);
            ParseQuery querySharePaidOwner = ParseQuery.getQuery("share_paid_users");
            try {
                ParseObject parseShareOwnerObject = queryShareOwner.getFirst();
                parseShareOwnerObject.put("share_owner_id", currentUser.getObjectId());
                parseShareOwnerObject.save();
                querySharePaidOwner.whereEqualTo("share_payer_id", parseShareOwnerObject.getObjectId());
                List<ParseObject> shareOwnerEdit = querySharePaidOwner.find();
                for(ParseObject parseObject: shareOwnerEdit){
                    parseObject.put("user_id", currentUser.getObjectId());
                    parseObject.put("user_name", currentUser.getUsername());
                }
                ParseObject.saveAll(shareOwnerEdit);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(jamId != null){
                ParseQuery<ParseObject> queryByJamId = ParseQuery.getQuery(ParseConstants.CLASS_JAM);
                queryByJamId.whereEqualTo(ParseConstants.KEY_JAM_ID, jamId)
                        .whereEqualTo(ParseConstants.KEY_JSTATUS, true);
                try {
                    jamSharesObject = new ArrayList<SharesModel>();
                    ParseObject parseObject = queryByJamId.getFirst();
                    final JamModel currentJam = new JamModel(parseObject, context);
                    ParseQuery<ParseObject> jShares = ParseQuery.getQuery("jShares");
                    jShares.whereEqualTo("shares_jamNo", jamId)
                            .orderByAscending("share_order");
                    List<ParseObject> list = jShares.find();
                    for (ParseObject share : list) {
                        SharesModel shareItem = new SharesModel();
                        shareItem.setJamId(share.getString("shares_jamNo"));
                        shareItem.setShareOrder(share.getInt("share_order"));
                        shareItem.setShareDelivered(share.getBoolean("share_status"));
                        shareItem.setjAmount(Integer.parseInt(currentJam.getjAmount()));
                        shareItem.setStartDay(share.getDate("share_due_date").toLocaleString().toString());
                        shareItem.setShareId(share.getObjectId());
                        jamSharesObject.add(shareItem);
                    }
                    currentJam.setSharesModel(jamSharesObject);
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(this,ViewJamDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    bundle.putParcelable("currentJamModel", currentJam);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/sheba.ttf");
        dialog = new Dialog(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.new_jam_header);
                dialog.setTitle(R.string.title_activity_create_new_jam);

                btnNewJamParticipants = (Button) dialog.findViewById(R.id.btnNewJamPaticipants);
                btnNewJamParticipants.setTypeface(tf);
                txtNewJamName = (EditText) dialog.findViewById(R.id.txtNewJamName1);
                txtNewJamName.setTypeface(tf);
                txtNewJamAmount = (EditText) dialog.findViewById(R.id.txtNewJamAmount1);
                txtNewJamAmount.setTypeface(tf);
                txtNewJamDate = (EditText) dialog.findViewById(R.id.txtNewJamDate1);
                String jamDate = HelperClass.getStringFormatDate(HelperClass.getCurrentDate(), context);
                txtNewJamDate.setText(jamDate);
                txtNewJamDate.setTypeface(tf);
                txtSharesNo = (EditText) dialog.findViewById(R.id.txtSharesNo);
                txtSharesNo.setTypeface(tf);
                txtNewJamDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSimpleDateFormat();
                        DatePickerDialog d = new DatePickerDialog(MainActivity.this,
                                DialogFragment.STYLE_NORMAL, mDateSetListener, mYear, mMonth, mDay);
                        d.show();
                    }
                });
                btnNewJamParticipants.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                periodSpinner = (Spinner) dialog.findViewById(R.id.spinnerPeriod);
                final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.period_array
                        , android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                periodSpinner.setAdapter(adapter);
                periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0)
                            jamSharePeriod = 31;
                        else if (position == 1)
                            jamSharePeriod = 7;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Button dialogButton = (Button) dialog.findViewById(R.id.btnNewJamPaticipants);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreateNewJam(dialog);

                    }

                });

                dialog.show();
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void CreateNewJam(final Dialog dialog) {
        if (txtNewJamName.getText().toString().equals("")
                || txtNewJamAmount.getText().toString().equals("")
                || txtNewJamDate.getText().toString().equals("")
                || txtSharesNo.getText().toString().equals("")) {
            Toast.makeText(context, R.string.data_filling_issue, Toast.LENGTH_LONG).show();

        } else {
            progress = ProgressDialog.show(context, "",
                    "", true);
            jamModel = new JamModel(txtNewJamName.getText().toString()
                    , currentUser.getObjectId()
                    , txtNewJamAmount.getText().toString()
                    , String.valueOf((periodSpinner.getSelectedItemPosition() == 0) ? 31 : 7)
                    , txtNewJamDate.getText().toString()
                    , txtSharesNo.getText().toString(), "true");

            Date formattedDate = HelperClass.getFormatDate(txtNewJamDate.getText().toString());
            final ParseObject parseObject = new ParseObject("Jam_header");

            parseObject.put("jName", jamModel.getjName());
            parseObject.put("jCreator", ParseUser.getCurrentUser().getObjectId());
            parseObject.put("jCreatorName", ParseUser.getCurrentUser().getUsername());
            parseObject.put("jStart_date", formattedDate);
            parseObject.put("jAmount", Integer.parseInt(jamModel.getjAmount()));
            parseObject.put("jPeriod", (jamModel.getjPeriod()));
            parseObject.put("jSharesNo", Integer.parseInt(jamModel.getSharesNo()));
            parseObject.put("obs", false);
            parseObject.put("jIsPublic", true);
            parseObject.put("jam_status", true);

            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        jamModel.setjId(parseObject.getObjectId());
                        saveJamShares();
                    }
                }
            });
        }
    }

    private void saveJamShares() {
        final List<ParseObject> sharesObject = new ArrayList<>();
        final List<SharesModel> sharesModels = new ArrayList<>();
        int shareOrder = 1;
        int sumDays = Integer.parseInt(jamModel.getjPeriod());
        for (int i = 0; i < Integer.parseInt(jamModel.getSharesNo()); i++) {
            ParseObject jamShares = new ParseObject("jShares");
            jamShares.put("shares_jamNo", jamModel.getjId());
            String day = HelperClass.setDate(txtNewJamDate.getText().toString(), sumDays);
            String[] arrDay = day.split("/");
            int _day, month, year;
            _day = Integer.parseInt(arrDay[0]);
            month = Integer.parseInt(arrDay[1]) - 1;
            year = Integer.parseInt(arrDay[2]) - 1900;
            jamShares.put("share_due_date", new Date(year, month, _day));
            jamShares.put("share_order", shareOrder);
            jamShares.put("share_status", false);
            jamShares.put("share_paid_amount", 0);
            sumDays += Integer.parseInt(jamModel.getjPeriod());
            shareOrder++;
            sharesObject.add(jamShares);
        }

        ParseObject.saveAllInBackground(sharesObject, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    for(ParseObject parseObject: sharesObject){
                        SharesModel sharesModel = new SharesModel(parseObject, context);
                        sharesModel.setjAmount(Integer.parseInt(jamModel.getjAmount()));
                        sharesModels.add(sharesModel);
                    }
                    jamModel.setSharesModel(sharesModels);
                    Bundle bundle = new Bundle();
                    JamModel currentJam = jamModel;
                    Intent intent = new Intent(MainActivity.this, ViewJamDetailsActivity.class);
                    bundle.putParcelable("currentJamModel", currentJam);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    progress.dismiss();
                    dialog.dismiss();
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    protected String getSimpleDateFormat() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(c.getTime());
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(context,
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
            txtNewJamDate.setText(new StringBuilder().append(mDay).append("/").append(mMonth).append("/").append(mYear));
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        PushWizard.launchPushWizard(this,
                GOOGLE_PROJECT_NUMBER, PUSHWIZARD_APPKEY,
                null,
                "start",
                PushWizard.GEOLOCATION_PROCESSING_MODE.OFF //comment out on SDK 1.0.3
        );
        //the null param can be a String array for Tags
    }

    @Override protected void onPause() {
        super.onPause();
        PushWizard.handleSession(null,
                "end",
                PushWizard.GEOLOCATION_PROCESSING_MODE.OFF //comment out on SDK 1.0.3
        );
        //the null param can be a String array for Tags
    }
}
