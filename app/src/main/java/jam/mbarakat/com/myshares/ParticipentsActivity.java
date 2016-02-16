package jam.mbarakat.com.myshares;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipentsActivity extends AppCompatActivity {

    TextView txtNewJamName, txtNewJamAmount, txtNewJamDate;
    JamModel jamModel;
    RadioButton selectByName, selectByPhone;
    ListView lvParticipants;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;

    AutoCompleteTextView txtPhoneNo;
    AutoCompleteTextView addRecName;
    Button btnAddParticipent;
    public ArrayList<String> c_Name = new ArrayList<String>();
    public ArrayList<String> c_Number = new ArrayList<String>();
    String[] name_Val=null;
    String[] phone_Val=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtPhoneNo = (AutoCompleteTextView) findViewById(R.id.addRecPhone);
        addRecName = (AutoCompleteTextView) findViewById(R.id.addRecName);

        lvParticipants = (ListView) findViewById(R.id.lvRecToBeAdded);
        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        lvParticipants.setAdapter(adapter);

        btnAddParticipent = (Button)findViewById(R.id.btnAddRec);
        btnAddParticipent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItems.add(addRecName.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        selectByName = (RadioButton) findViewById(R.id.rbByName);
        selectByPhone = (RadioButton) findViewById(R.id.rbByPhone);
        selectByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    txtPhoneNo.setEnabled(false);
                    addRecName.setEnabled(true);
            }
        });
        selectByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPhoneNo.setEnabled(true);
                addRecName.setEnabled(false);
            }
        });

        txtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                Toast toast = Toast.makeText(getApplicationContext(), s + " is clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        Intent intent = getIntent();
        jamModel = (JamModel) intent.getParcelableExtra("jamModel");

        txtNewJamName = (TextView) findViewById(R.id.txtSaveJamName);
        txtNewJamAmount = (TextView) findViewById(R.id.txtSaveJamAmount);
        txtNewJamDate = (TextView) findViewById(R.id.txtSaveJamStartDay);

        txtNewJamAmount.setText(jamModel.getjAmount());
        txtNewJamDate.setText(jamModel.getjDate());
        txtNewJamName.setText(jamModel.getjName());
        fetchContacts();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseQuery jPeriod = ParseQuery.getQuery("period")
                        .whereEqualTo("days_no", 31);
                jPeriod.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        String periodId = "";
                        periodId = list.get(0).getObjectId().toString();
                        ParseObject newJam = new ParseObject("Jam_header");
                        newJam.put(ParseConstants.KEY_JNAME, jamModel.getjName());
                        newJam.put(ParseConstants.KEY_JAMOUNT, Integer.parseInt(jamModel.getjAmount()));
                        newJam.put(ParseConstants.KEY_JSTART_DAY, jamModel.getjDate());
                        newJam.put(ParseConstants.KEY_JCREATOR, jamModel.getjOwnerId());
                        newJam.put(ParseConstants.KEY_IS_PUBLIC, false);
                        newJam.put(ParseConstants.KEY_JPERIOID, periodId);
                        newJam.put(ParseConstants.KEY_OBS, false);
                        newJam.put(ParseConstants.KEY_JSTATUS, true);
                        newJam.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                }
                            }
                        });
                    }
                });

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public ArrayList<String> recPhoneAndName(String text){
        String[] arr = text.split(":");
        ArrayList<String> contact = new ArrayList<String>();
        if(selectByName.isChecked()){
            contact.add(arr[0].trim());
            contact.add(arr[1].trim());
        }else {
            contact.add(arr[1].trim());
            contact.add(arr[0].trim());
        }
            return contact;
    }

    public void fetchContacts()
    {

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID};



        String _ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String DISPLAY_NAME =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=?", new String[] { "1" },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        System.out.println("contact size.." + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(NUMBER));


                c_Name.add(name + " : " + extractNumbers(phoneNumber));
                c_Number.add(extractNumbers(phoneNumber) + " : " + name);
            }
            name_Val = (String[]) c_Name.toArray(new String[c_Name.size()]);
            phone_Val= (String[]) c_Number.toArray(new String[c_Name.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, phone_Val);
            txtPhoneNo.setAdapter(adapter);
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, name_Val);
            addRecName.setAdapter(adapter);

        }


    }

    public static String extractNumbers(String s){
        List<Integer> numbers = new ArrayList<Integer>();
    String number = "";

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);

        while(m.find()){
            numbers.add(Integer.parseInt(m.group()));
            number+=m.group();
        }
        return number;
    }
}
