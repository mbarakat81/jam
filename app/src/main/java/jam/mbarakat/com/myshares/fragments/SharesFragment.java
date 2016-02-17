package jam.mbarakat.com.myshares.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.SharesModel;
import jam.mbarakat.com.myshares.adapters.SharesAdapter;


public class SharesFragment extends Fragment {

    RecyclerView rv;
    Button btnNewJamShares;
    EditText txtNewJamName, txtNewJamAmount, txtNewJamDate, txtSharePeriod, sharesNo;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharesAdapter adapter   ;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Spinner spinnerPeriod;
    private FloatingActionButton fab;

    JamModel jamModel = new JamModel();
    public static SharesFragment newInstance(String param1, String param2) {
        SharesFragment fragment = new SharesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SharesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnNewJamShares = (Button) getActivity().findViewById(R.id.btnNewJamPaticipants);
        txtNewJamName = (EditText) getActivity().findViewById(R.id.txtNewJamName1);
        txtNewJamAmount = (EditText) getActivity().findViewById(R.id.txtNewJamAmount1);
        txtNewJamDate = (EditText) getActivity().findViewById(R.id.txtNewJamDate1);
        txtSharePeriod = (EditText) getActivity().findViewById(R.id.txtSharePeriod);
        spinnerPeriod = (Spinner) getActivity().findViewById(R.id.spinnerPeriod);
        sharesNo = (EditText) getActivity().findViewById(R.id.txtSharesNo);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        btnNewJamShares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtNewJamName.getText().toString().equals("") || txtSharePeriod.getText().toString().equals("") ||
                        txtNewJamAmount.getText().toString().equals("") || txtNewJamDate.getText().toString().equals("")
                        || sharesNo.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "this is my Toast message!!! =)", Toast.LENGTH_LONG).show();

                } else {
                    fab.setVisibility(View.VISIBLE);
                    jamModel = new JamModel(txtNewJamName.getText().toString()
                            , getActivity().getIntent().getExtras().get("USER_ID").toString()
                            , txtNewJamAmount.getText().toString()
                            , String.valueOf((spinnerPeriod.getSelectedItemPosition() == 0) ? 31 : 7)
                            , txtNewJamDate.getText().toString()
                            , sharesNo.getText().toString(), "true");

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
                                String jId = parseObject.getObjectId();
                                saveJamShares(jId
                                        , Integer.parseInt(jamModel.getjAmount())
                                        , Integer.parseInt(jamModel.getSharesNo())
                                        , Integer.parseInt(txtSharePeriod.getText().toString()));
                                btnNewJamShares.setVisibility(View.GONE);
                                txtNewJamName.setEnabled(false);
                                txtNewJamAmount.setEnabled(false);
                                txtNewJamDate.setEnabled(false);
                                txtSharePeriod.setEnabled(false);
                                spinnerPeriod.setEnabled(false);
                                sharesNo.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });
    }

    private void saveJamShares(final String jamId,final int shareAmount , final int sharesNo, int days) {
        final List<ParseObject> sharesObject = new ArrayList<>();
        final List<SharesModel> sharesModels = new ArrayList<>();
        int shareOrder = 1;
        int sumDays = days;
        for(int i=0;i<sharesNo;i++){
            ParseObject jamShares = new ParseObject("jShares");
            jamShares.put("shares_jamNo",jamId);
            String day = setDate(txtNewJamDate.getText().toString(), sumDays);
            String[] arrDay = day.split("/");
            int _day, month, year;
            _day = Integer.parseInt(arrDay[0]);
            month = Integer.parseInt(arrDay[1])-1;
            year =  Integer.parseInt(arrDay[2])-1900;
            jamShares.put("share_due_date",new Date(year,month,_day));
            jamShares.put("share_order", shareOrder);
            jamShares.put("share_status", false);

            sumDays += days;
            shareOrder++;
            SharesModel sharesModel = new SharesModel(jamShares,day);
            sharesModel.setjAmount(shareAmount);
            sharesModels.add(sharesModel);
            sharesObject.add(jamShares);

        }

        ParseObject.saveAllInBackground(sharesObject, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    adapter.updateData(sharesModels);
                }
            }
        });

    }

    private String setDate(String date, int days)  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] arr = date.split("/");
        int day,year,month;
        day = Integer.parseInt(arr[0]);
        month = Integer.parseInt(arr[1]);
        year = Integer.parseInt(arr[2]);
        Calendar c = Calendar.getInstance();
        c.set(year,month-1,day);
        c.add(Calendar.DAY_OF_YEAR, days);

        return sdf.format(c.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_shares, container, false);
        rv  = (RecyclerView) layout.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public List<SharesModel> getData(int count, String date, int days){

        List<SharesModel> sharesModels = new ArrayList<>();
        int sumDays = days;
        for(int i=0 ; i<count;i++){
            sharesModels.add(new SharesModel(setDate(date, sumDays)));
            sumDays += days;
        }

        return sharesModels;
    }
    public static List<SharesModel> getData(int count){
        List<SharesModel> sharesModels = new ArrayList<>();
        for(int i=0 ; i<count;i++){
            sharesModels.add(new SharesModel());
        }

        return sharesModels;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new SharesAdapter(getActivity(),getData(0));
        rv.setAdapter(adapter);

    }
}
