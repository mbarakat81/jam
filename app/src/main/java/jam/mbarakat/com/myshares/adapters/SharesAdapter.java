package jam.mbarakat.com.myshares.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.modules.ShareItem;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/2/2016.
 */
public class SharesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ClickListener clickListener;
    private LayoutInflater inflater;
    List<SharesModel> data = Collections.emptyList();
    Context context = null;
    Activity activity=null;
    JamModel jamModel;
    Typeface tf;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public SharesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            View view = inflater.inflate(R.layout.new_jam_header, parent, false);
            NewJamHeader newJamHeader = new NewJamHeader(view);
            return newJamHeader ;
        }else if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.shares_row, parent, false);
            SharesViewHolder sharesViewHolder = new SharesViewHolder(view, context);
            return sharesViewHolder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NewJamHeader){
            NewJamHeader newJamHeader = (NewJamHeader) holder;
            newJamHeader.txtNewJamName.setText(jamModel.getjName());
            newJamHeader.txtNewJamAmount.setText(jamModel.getjAmount());
            newJamHeader.txtNewJamDate.setText(jamModel.getjDate());
            newJamHeader.txtSharesNo.setText(jamModel.getSharesNo());

        }else if(holder instanceof SharesViewHolder){
            SharesModel sharesModel = data.get(position - 1);
            SharesViewHolder sharesViewHolder = (SharesViewHolder) holder;
            sharesViewHolder.startDay.setText(sharesModel.getStartDay());
            sharesViewHolder.hiddenJID.setText(sharesModel.getJamId());
            sharesViewHolder.hiddenJAmount.setText(String.valueOf(sharesModel.getjAmount()));
            sharesViewHolder.hiddenShareNo.setText(String.valueOf(sharesModel.getShareOrder()));
        }

    }

    public SharesAdapter(Context context, List<SharesModel> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }
    public SharesAdapter(Context context, Activity activity, JamModel jamModel){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.activity = activity;
        this.jamModel = jamModel;
        this.data = jamModel.getSharesModel();
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/sheba.ttf");
    }
    public void updateData(List<SharesModel> sharesModel){
        this.data = sharesModel;
        notifyDataSetChanged();
    }

    public void updateData(JamModel jamModel){
        this.jamModel = jamModel;
        this.data = jamModel.getSharesModel();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return data.size()+1;
    }
    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class NewJamHeader extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button btnNewJamParticipants;
        EditText txtNewJamName, txtNewJamAmount, txtNewJamDate, txtSharesNo;
        Spinner periodSpinner ;
        private ProgressDialog progress;
        private int mYear,mMonth,mDay;

        int jamSharePeriod;
        public NewJamHeader(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            periodSpinner = (Spinner)itemView.findViewById(R.id.spinnerPeriod);
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.period_array
                    , android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            periodSpinner.setAdapter(adapter);

            btnNewJamParticipants = (Button) itemView.findViewById(R.id.btnNewJamPaticipants);
            btnNewJamParticipants.setTypeface(tf);
            txtNewJamName = (EditText) itemView.findViewById(R.id.txtNewJamName1);
            txtNewJamName.setTypeface(tf);
            txtNewJamAmount = (EditText) itemView.findViewById(R.id.txtNewJamAmount1);
            txtNewJamAmount.setTypeface(tf);
            txtNewJamDate = (EditText) itemView.findViewById(R.id.txtNewJamDate1);
            txtNewJamDate.setTypeface(tf);
            txtSharesNo = (EditText) itemView.findViewById(R.id.txtSharesNo);
            txtSharesNo.setTypeface(tf);

            Spinner spinner = (Spinner) itemView.findViewById(R.id.spinnerPeriod);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0)
                        jamSharePeriod = 31;
                    else if(position==1)
                        jamSharePeriod=7;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            txtNewJamDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener !=null){
                        clickListener.OnDateItemClick(v, getPosition());
                    }
                }
            });
            btnNewJamParticipants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtNewJamName.getText().toString().equals("")
                            || txtNewJamAmount.getText().toString().equals("")
                            || txtNewJamDate.getText().toString().equals("")
                            || txtSharesNo.getText().toString().equals("")) {
                        Toast.makeText(activity, "this is my Toast message!!! =)", Toast.LENGTH_LONG).show();

                    } else {
                        progress = ProgressDialog.show(context, "",
                                "", true);
                        //fab.setVisibility(View.VISIBLE);
                        jamModel = new JamModel(txtNewJamName.getText().toString()
                                , activity.getIntent().getExtras().get("USER_ID").toString()
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
                                    String jId = parseObject.getObjectId();
                                    saveJamShares(jId
                                            , Integer.parseInt(jamModel.getjAmount())
                                            , Integer.parseInt(jamModel.getSharesNo())
                                            , Integer.parseInt(jamModel.getjPeriod()));
                                    btnNewJamParticipants.setVisibility(View.GONE);
                                    txtNewJamName.setEnabled(false);
                                    txtNewJamAmount.setEnabled(false);
                                    txtNewJamDate.setEnabled(false);
                                    periodSpinner.setEnabled(false);
                                    txtSharesNo.setEnabled(false);
                                    progress.dismiss();
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
                jamShares.put("shares_jamNo", jamId);
                String day = HelperClass.setDate(txtNewJamDate.getText().toString(), sumDays);
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
            jamModel.setSharesModel(sharesModels);
            ParseObject.saveAllInBackground(sharesObject, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        updateData(jamModel);
                    }
                }
            });

        }
        protected String getSimpleDateFormat(){
            Calendar c=Calendar.getInstance();
            c.add(Calendar.MONTH, 1);
            mYear=c.get(Calendar.YEAR);
            mMonth=c.get(Calendar.MONTH);
            mDay=c.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(c.getTime());
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                clickListener.OnItemClick(v, getPosition());
            }
        }
    }
    class SharesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View view;
        EditText hiddenJID, hiddenShareNo, amount, hiddenJAmount, hiddenJAddedAmount;
        TextView startDay, lblStartDay;
        ListView lvParticipants;
        ImageView btnDeleteShareItem, btnAddRecToShare;
        AutoCompleteTextView addRecName;
        Button edit;

        List<ShareItem> shareItems= null;
        ShareItemAdapter shareItemAdapter = null;
        List<String []> contactList = Collections.emptyList();

        int shareAmountSum = 0;
        int mAmount ;

        public SharesViewHolder(View itemView, final Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.view = itemView;
            lblStartDay = (TextView) view.findViewById(R.id.lblShareStartDay);
            lblStartDay.setTypeface(tf);
            startDay = (TextView) view.findViewById(R.id.txtShareStartDay);
            startDay.setTypeface(tf);
            edit = (Button) view.findViewById(R.id.sharesAddParticipants);
            edit.setTypeface(tf);
            amount = (EditText)view.findViewById(R.id.subShareAmount);
            amount.setTypeface(tf);
            addRecName = (AutoCompleteTextView) view.findViewById(R.id.addRecName1);
            addRecName.setTypeface(tf);


            hiddenJID = (EditText)view.findViewById(R.id.hiddenJId);
            hiddenShareNo = (EditText)view.findViewById(R.id.hiddenShareNo);
            hiddenJAmount = (EditText)view.findViewById(R.id.hiddenJAmount);
            hiddenJAddedAmount = (EditText)view.findViewById(R.id.hiddenJAddedAmount);
            lvParticipants = (ListView) view.findViewById(R.id.lvSharesParticipants);

            shareItems = new ArrayList<>();
            shareItemAdapter = new ShareItemAdapter(context, shareItems, itemView);
            lvParticipants.setAdapter(shareItemAdapter);
            btnDeleteShareItem = (ImageView) view.findViewById(R.id.deleteShareItem);
            btnAddRecToShare = (ImageView)view.findViewById(R.id.btnAddRecToShare);

            btnAddRecToShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(addRecName.getText().toString().isEmpty() || amount.getText().toString().isEmpty()){
                            Toast.makeText(context, R.string.data_filling_issue, Toast.LENGTH_LONG).show();
                    }else {
                        mAmount = Integer.parseInt(amount.getText().toString());
                        int shareAmount = Integer.parseInt(hiddenJAmount.getText().toString());
                        shareAmountSum = (hiddenJAddedAmount.getText().toString().isEmpty())? 0 :Integer.parseInt(hiddenJAddedAmount.getText().toString());
                        shareAmountSum += mAmount;

                        if(shareAmountSum > shareAmount){
                            shareAmountSum -= mAmount;
                            Toast.makeText(context, R.string.sub_shares_count_error, Toast.LENGTH_LONG).show();
                        }else{
                            hiddenJAddedAmount.setText(String.valueOf(shareAmountSum));
                            String[] arr = addRecName.getText().toString().split(",");
                            final String name = arr[0];
                            final String phone = (arr.length > 1) ? arr[1] : "";
                            final ParseObject parseObject = new ParseObject("Share_owners");
                            parseObject.put("jamId", hiddenJID.getText().toString());
                            parseObject.put("share_owner", name);
                            parseObject.put("share_owner_id", "");
                            parseObject.put("obs", false);
                            parseObject.put("share_no", Integer.parseInt(hiddenShareNo.getText().toString()));
                            parseObject.put("owner_phone", phone);
                            parseObject.put("share_amount", Integer.parseInt(amount.getText().toString()));

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){

                                        shareItems.add(new ShareItem(name, phone, Integer.parseInt(amount.getText().toString()), parseObject.getObjectId(), Integer.parseInt(hiddenShareNo.getText().toString())));
                                        shareItemAdapter.notifyDataSetChanged();
                                        addRecName.setText("");
                                        addRecName.requestFocus();
                                        amount.setText("");

                                    }

                                }
                            });
                        }
                    }
                }
            });

            contactList = HelperClass.fetchContacts(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line, contactList.get(0));
            addRecName.setAdapter(adapter);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                clickListener.OnItemClick(v, getPosition());
            }
        }
    }

    public interface ClickListener{
        public void OnItemClick(View view, int pos);
        public void OnDateItemClick(View view, int pos);
        public void OnPartItemClick(View view, int pos);
    }
}
