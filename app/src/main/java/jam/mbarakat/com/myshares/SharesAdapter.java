package jam.mbarakat.com.myshares;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MBARAKAT on 2/2/2016.
 */
public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.SharesViewHolder> {
    ClickListener clickListener;
    private LayoutInflater inflater;
    List<SharesModel> data = Collections.emptyList();
    Context context = null;

    @Override
    public SharesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shares_row, parent, false);
        SharesViewHolder sharesViewHolder = new SharesViewHolder(view, context);
        return sharesViewHolder;
    }
    SharesAdapter(Context context, List<SharesModel> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }
    public void updateData(List<SharesModel> sharesModel){
        this.data = sharesModel;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(SharesViewHolder holder, int position) {
        SharesModel sharesModel = data.get(position);
        holder.startDay.setText(sharesModel.getStartDay());
        holder.hiddenJID.setText(sharesModel.getJamId());
        holder.hiddenJAmount.setText(String.valueOf(sharesModel.getjAmount()));
        holder.hiddenShareNo.setText(String.valueOf(sharesModel.getShareOrder()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SharesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public View view;
        EditText hiddenJID, hiddenShareNo, amount, hiddenJAmount, hiddenJAddedAmount;
        TextView startDay;
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
            startDay = (TextView) view.findViewById(R.id.txtShareStartDay);
            edit = (Button) view.findViewById(R.id.sharesAddParticipants);
            amount = (EditText)view.findViewById(R.id.subShareAmount);
            addRecName = (AutoCompleteTextView) view.findViewById(R.id.addRecName1);


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
    }


}
