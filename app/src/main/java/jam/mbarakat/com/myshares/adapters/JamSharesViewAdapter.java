package jam.mbarakat.com.myshares.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.ShareItem;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/7/2016.
 */
public class JamSharesViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    ClickListener clickListener;
    List<SharesModel> data = Collections.emptyList();
    Context context = null;
    private LayoutInflater layoutInflater;
    JamModel jamModel;
    View parentView;
    boolean isMyJam;

    public JamSharesViewAdapter(Context context, JamModel data){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.isMyJam = data.isMysJam(ParseUser.getCurrentUser().getObjectId());
        this.jamModel = data;
        this.data = data.getSharesModel();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER)
        {
            View view = layoutInflater.inflate(R.layout.jam_header, parent, false);
            this.parentView = view;
            JamHeaderViewHolder holder = new JamHeaderViewHolder(view);
            return holder;
        }else if(viewType == TYPE_ITEM)
        {
            View view = layoutInflater.inflate(R.layout.shares_row, parent, false);
            this.parentView = view;
            SharesViewHolder holder = new SharesViewHolder(view);
            return holder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof JamHeaderViewHolder)
        {
            JamHeaderViewHolder jamHeaderViewHolder = (JamHeaderViewHolder)holder;
            jamHeaderViewHolder.jamAmount.setText(jamModel.getjAmount());
            jamHeaderViewHolder.jamDate.setText(jamModel.getjDate());
            jamHeaderViewHolder.jamName.setText(jamModel.getjName());
            jamHeaderViewHolder.sharesNo.setText(jamModel.getSharesNo());

        }else if(holder instanceof SharesViewHolder)
        {
            SharesViewHolder sharesViewHolder = (SharesViewHolder)holder;
            SharesModel sharesModel = data.get(position-1);
            sharesViewHolder.startDay.setText(HelperClass.getFormatedDateFromString(sharesModel.getStartDay()));
            sharesViewHolder.hiddenJID.setText(sharesModel.getJamId());
            sharesViewHolder.hiddenShareNo.setText(String.valueOf(sharesModel.getShareOrder()));
            sharesViewHolder.hiddenJAmount.setText(String.valueOf(sharesModel.getjAmount()));
            sharesViewHolder.hiddenJAddedAmount.setText(String.valueOf(sharesModel.getAddedAmount()));

            sharesViewHolder.shareItems = sharesModel.getShareItems();
            sharesViewHolder.shareItemAdapter = new ShareItemAdapter(context, sharesViewHolder.shareItems, isMyJam, parentView);
            sharesViewHolder.lvParticipants.setAdapter(sharesViewHolder.shareItemAdapter);
            if(!isMyJam){
                sharesViewHolder.btnAddRecToShare.setVisibility(View.GONE);
                sharesViewHolder.amount.setVisibility(View.GONE);
                sharesViewHolder.addRecName.setVisibility(View.GONE);
            }
        }
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


    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    public void updateData(List<SharesModel> sharesModel) {
        this.data = sharesModel;
        notifyDataSetChanged();
    }

    class JamHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView jamName, jamAmount, sharesNo, jamDate;

        CheckBox jamIsPublic;

        public JamHeaderViewHolder(View itemView) {
            super(itemView);
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/sheba.ttf");
            jamName = (TextView)itemView.findViewById(R.id.txtNewJamName1);
            jamName.setTypeface(tf);
            jamAmount = (TextView)itemView.findViewById(R.id.txtNewJamAmount1);
            jamAmount.setTypeface(tf);
            sharesNo = (TextView)itemView.findViewById(R.id.txtSharesNo);
            sharesNo.setTypeface(tf);
            jamDate = (TextView)itemView.findViewById(R.id.txtNewJamDate1);
            jamDate.setTypeface(tf);
            jamIsPublic = (CheckBox)itemView.findViewById(R.id.cbNewJamIsPublic);

        }

        @Override
        public void onClick(View v) {

        }
    }

    class SharesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public View view;
        List<String []> contactList = Collections.emptyList();
        EditText hiddenJID, hiddenShareNo , amount,hiddenJAmount, hiddenJAddedAmount;
        TextView startDay, lblStartDay;
        ListView lvParticipants;
        ImageView btnDeleteShareItem, btnAddRecToShare;
        AutoCompleteTextView addRecName;
        Button edit;
        List<ShareItem> shareItems= new ArrayList<>();
        ShareItemAdapter shareItemAdapter = null;
        int shareAmountSum = 0;
        int mAmount ;

        public SharesViewHolder(View itemView) {
            super(itemView);
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/sheba.ttf");
            itemView.setOnClickListener(this);
            contactList = HelperClass.fetchContacts(context);
            this.view = itemView;
            startDay = (TextView) view.findViewById(R.id.txtShareStartDay);
            startDay.setTypeface(tf);
            lblStartDay = (TextView) view.findViewById(R.id.lblShareStartDay);
            lblStartDay.setTypeface(tf);
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

            btnDeleteShareItem = (ImageView) view.findViewById(R.id.deleteShareItem);
            btnAddRecToShare = (ImageView)view.findViewById(R.id.btnAddRecToShare);

            btnAddRecToShare.setOnClickListener(new View.OnClickListener() {
                public ProgressDialog progress;

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

                            progress = ProgressDialog.show(context, "",
                                    "", true);
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
                                    if(shareItems.size()==0){
                                        shareItems = new ArrayList<ShareItem>();
                                        shareItemAdapter = new ShareItemAdapter(context, shareItems, isMyJam, view);
                                        lvParticipants.setAdapter(shareItemAdapter);
                                    }

                                    shareItems.add(new ShareItem(name
                                            , phone
                                            , Integer.parseInt(amount.getText().toString())
                                            , parseObject.getObjectId()
                                            ,Integer.parseInt(hiddenShareNo.getText().toString())));
                                    shareItemAdapter.notifyDataSetChanged();
                                    addRecName.setText("");
                                    addRecName.requestFocus();
                                    amount.setText("");
                                    progress.dismiss();
                                }
                            });
                        }
                }}
            });
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
    }
}
