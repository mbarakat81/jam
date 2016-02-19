package jam.mbarakat.com.myshares.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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


public class NewJamFragment extends Fragment implements SharesAdapter.ClickListener {

    RecyclerView rv;
    EditText txtNewJamDate;
    static final int DATE_DIALOG_ID = 0;
    private int mYear,mMonth,mDay;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharesAdapter adapter;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fab;

    JamModel jamModel = new JamModel();

    public NewJamFragment() {
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
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
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

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return  new DatePickerDialog(getContext(),
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

    @Override
    public void OnItemClick(View view, int pos) {

    }

    @Override
    public void OnDateItemClick(View view, int pos) {
        txtNewJamDate = (EditText) view;
        getSimpleDateFormat();
        DatePickerDialog d = new DatePickerDialog(getActivity(),
                DialogFragment.STYLE_NORMAL, mDateSetListener, mYear, mMonth, mDay);
        d.show();
    }

    @Override
    public void OnPartItemClick(View view, int pos) {

    }

    protected String getSimpleDateFormat(){
        Calendar c=Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(c.getTime());
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
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
        jamModel.setSharesModel(getData(0));
        adapter = new SharesAdapter(getActivity(), getActivity(),jamModel);
        adapter.setClickListener(NewJamFragment.this);
        rv.setAdapter(adapter);
    }
}
