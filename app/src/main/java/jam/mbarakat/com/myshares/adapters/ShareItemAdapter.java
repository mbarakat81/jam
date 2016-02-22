package jam.mbarakat.com.myshares.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.ShareItem;

/**
 * Created by MBARAKAT on 2/4/2016.
 */
public class ShareItemAdapter extends ArrayAdapter<ShareItem> {
    Context mContext;
    private List<ShareItem> mShareItems;
    boolean isMyJam = true;
    public View parentView;

    public ShareItemAdapter(Context context, List<ShareItem> shareItems){
        super(context, R.layout.shares_owner_layout, shareItems);
        this.mContext = context;
        this.mShareItems = shareItems;
    }
    public ShareItemAdapter(Context context, List<ShareItem> shareItems, View view){
        super(context, R.layout.shares_owner_layout, shareItems);
        parentView = view;
        this.mContext = context;
        this.mShareItems = shareItems;
    }

    public ShareItemAdapter(Context context, List<ShareItem> shareItems, boolean isMyJam, View view){
        super(context, R.layout.shares_owner_layout, shareItems);
        this.mContext = context;
        parentView = view;
        this.mShareItems = shareItems;
        this.isMyJam = isMyJam;
    }
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ShareItemViewHolder shareItemViewHolder;
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/sheba.ttf");
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shares_owner_layout, null);
            shareItemViewHolder = new ShareItemViewHolder();
            shareItemViewHolder.shareOwnerName = (TextView) convertView.findViewById(R.id.txtShareOwner);
            shareItemViewHolder.shareOwnerName.setTypeface(tf);
            shareItemViewHolder.shareAmount = (TextView) convertView.findViewById(R.id.txtShareAmount);
            shareItemViewHolder.shareAmount.setTypeface(tf);
            shareItemViewHolder.hiddenSubShareId = (EditText) convertView.findViewById(R.id.hiddenSubShareId);
            shareItemViewHolder.deleteShareItem = (ImageView) convertView.findViewById(R.id.deleteShareItem);
            shareItemViewHolder.addPhone = (EditText)convertView.findViewById(R.id.addPhone);
            shareItemViewHolder.hiddenCurrentJamAmount = (EditText) parentView.findViewById(R.id.hiddenJAddedAmount);
            convertView.setTag(shareItemViewHolder);
        }else {
            shareItemViewHolder = (ShareItemViewHolder) convertView.getTag();
        }

        ShareItem shareItem = mShareItems.get(position);

        final String shareId = shareItem.getShareId();
        final int shareAmount = shareItem.getShareAmount();
        if(!isMyJam){
            shareItemViewHolder.deleteShareItem.setVisibility(View.GONE);
            shareItemViewHolder.addPhone.setVisibility(View.GONE);
        }
        if(!shareItem.getShareOwnerPhone().isEmpty())
            shareItemViewHolder.addPhone.setText(shareItem.getShareOwnerPhone());
        shareItemViewHolder.addPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    final int tag = (Integer) view.getTag();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Share_owners");
                    query.getInBackground(shareId, new GetCallback<ParseObject>() {
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                parseObject.put("owner_phone", shareItemViewHolder.addPhone.getText().toString());
                                parseObject.saveInBackground();
                                mShareItems.get(tag).setShareOwnerPhone(shareItemViewHolder.addPhone.getText().toString());
                            }
                        }
                    });
                }
            }
        });
        shareItemViewHolder.shareOwnerName.setText(shareItem.getShareOwnerName());
        shareItemViewHolder.shareAmount.setText(String.valueOf(shareItem.getShareAmount()));
        shareItemViewHolder.hiddenSubShareId.setText(shareItem.getShareId());
        shareItemViewHolder.deleteShareItem.setTag(position);
        shareItemViewHolder.addPhone.setTag(position);
        shareItemViewHolder.deleteShareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progress;
                final int tag = (Integer) view.getTag();
                progress = ProgressDialog.show(getContext(), "",
                        "", true);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Share_owners");
                query.getInBackground(shareId, new GetCallback<ParseObject>() {
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            parseObject.put("obs", true);
                            parseObject.saveInBackground();
                            mShareItems.remove(tag);
                            int currentSubSharesSum = Integer.parseInt(shareItemViewHolder.hiddenCurrentJamAmount.getText().toString());
                            currentSubSharesSum -= shareAmount;
                            shareItemViewHolder.hiddenCurrentJamAmount.setText(String.valueOf(currentSubSharesSum));
                            notifyDataSetChanged();
                            progress.dismiss();
                        }
                    }
                });
            }
        });
        return convertView;
    }


    private static class ShareItemViewHolder{
        TextView shareOwnerName, shareAmount;
        EditText hiddenSubShareId, hiddenCurrentJamAmount;
        ImageView deleteShareItem;
        EditText addPhone;
    }
}
