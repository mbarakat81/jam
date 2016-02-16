package jam.mbarakat.com.myshares;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MBARAKAT on 2/7/2016.
 */
public class JamSharesViewAdapter extends RecyclerView.Adapter<JamSharesViewAdapter.SharesViewHolder> {
    ClickListener clickListener;
    List<SharesModel> data = Collections.emptyList();
    Context context = null;
    private LayoutInflater layoutInflater;
View parentView;
    boolean isMyJam;

    JamSharesViewAdapter(Context context, JamModel data){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.isMyJam = data.isMysJam(ParseUser.getCurrentUser().getObjectId());
        this.data = data.getSharesModel();
    }

    @Override
    public SharesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.shares_row, parent, false);
        this.parentView = view;
        SharesViewHolder holder = new SharesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SharesViewHolder holder, int position) {
        SharesModel sharesModel = data.get(position);
        holder.startDay.setText(sharesModel.getStartDay());
        holder.hiddenJID.setText(sharesModel.getJamId());
        holder.hiddenShareNo.setText(String.valueOf(sharesModel.getShareOrder()));
        holder.hiddenJAmount.setText(String.valueOf(sharesModel.getjAmount()));
        holder.hiddenJAddedAmount.setText(String.valueOf(sharesModel.getAddedAmount()));

        holder.shareItems = sharesModel.getShareItems();
        holder.shareItemAdapter = new ShareItemAdapter(context, holder.shareItems, isMyJam, parentView);
        holder.lvParticipants.setAdapter(holder.shareItemAdapter);
        if(!isMyJam){
            holder.btnAddRecToShare.setVisibility(View.GONE);
            holder.amount.setVisibility(View.GONE);
            holder.addRecName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<SharesModel> sharesModel) {
        this.data = sharesModel;
        notifyDataSetChanged();
    }

    class SharesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public View view;
        List<String []> contactList = Collections.emptyList();
        EditText hiddenJID, hiddenShareNo , amount,hiddenJAmount, hiddenJAddedAmount;
        TextView startDay;
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
            itemView.setOnClickListener(this);
            contactList = HelperClass.fetchContacts(context);
            this.view = itemView;
            startDay = (TextView) view.findViewById(R.id.txtShareStartDay);
            edit = (Button) view.findViewById(R.id.sharesAddParticipants);
            amount = (EditText)view.findViewById(R.id.subShareAmount);
            addRecName = (AutoCompleteTextView) view.findViewById(R.id.addRecName1);
            hiddenJID = (EditText)view.findViewById(R.id.hiddenJId);
            hiddenShareNo = (EditText)view.findViewById(R.id.hiddenShareNo);
            hiddenJAmount = (EditText)view.findViewById(R.id.hiddenJAmount);
            hiddenJAddedAmount = (EditText)view.findViewById(R.id.hiddenJAddedAmount);
            lvParticipants = (ListView) view.findViewById(R.id.lvSharesParticipants);
            // TODO: 2/7/2016 set shares owners

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
