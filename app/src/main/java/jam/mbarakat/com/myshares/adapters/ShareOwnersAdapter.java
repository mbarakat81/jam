package jam.mbarakat.com.myshares.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.util.ArrayList;
import java.util.List;

import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.helpers.ParseConstants;
import jam.mbarakat.com.myshares.helpers.SessionUser;
import jam.mbarakat.com.myshares.modules.ShareItem;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/4/2016.
 */
public class ShareOwnersAdapter extends ArrayAdapter<ShareItem> {
    Context mContext;
    private List<ShareItem> mShareItems;
    private List<SharesModel> shareModels;
    boolean isMyJam = true;
    String jName;
    public View parentView;
    ShareItem shareItem;
    public ShareOwnersAdapter(Context context, List<ShareItem> shareItems, boolean isMyJam, View view, List<SharesModel> shareModels, String jName){
        super(context, R.layout.shares_owner_layout, shareItems);
        this.mContext = context;
        parentView = view;
        this.mShareItems = shareItems;
        this.shareModels = shareModels;
        this.jName=jName;
        this.isMyJam = isMyJam;
    }
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ShareItemViewHolder shareItemViewHolder;
        final Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/sheba.ttf");
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shares_owner_layout, null);
            shareItemViewHolder = new ShareItemViewHolder();
            shareItemViewHolder.shareOwnerName = (TextView) convertView.findViewById(R.id.txtShareOwner);
            shareItemViewHolder.shareOwnerName.setTypeface(tf);
            shareItemViewHolder.shareAmount = (TextView) convertView.findViewById(R.id.txtShareAmount);
            shareItemViewHolder.shareAmount.setTypeface(tf);
            shareItemViewHolder.hiddenSubShareId = (EditText) convertView.findViewById(R.id.hiddenSubShareId);
            shareItemViewHolder.deleteShareItem = (ImageView) convertView.findViewById(R.id.deleteShareItem);
            shareItemViewHolder.notifyUserToReceive = (ImageView) convertView.findViewById(R.id.notifyUserToReceive);
            shareItemViewHolder.shareViaSocialMedia = (ImageView) convertView.findViewById(R.id.shareViaSocialMedia);
            shareItemViewHolder.sharingInfo = (ImageView) convertView.findViewById(R.id.sharingInfo);
            shareItemViewHolder.hiddenCurrentJamAmount = (EditText) parentView.findViewById(R.id.hiddenJAddedAmount);
            convertView.setTag(shareItemViewHolder);
        }else {
            shareItemViewHolder = (ShareItemViewHolder) convertView.getTag();
        }

        shareItem = mShareItems.get(position);
        final String shareId = shareItem.getShareId();
        final int shareAmount = shareItem.getShareAmount();


        if(!isMyJam){
            shareItemViewHolder.deleteShareItem.setVisibility(View.GONE);
            if(shareItem.isMyShare()){
                shareItemViewHolder.shareViaSocialMedia.setVisibility(View.VISIBLE);
                shareItemViewHolder.sharingInfo.setVisibility(View.VISIBLE);
            }else{
                shareItemViewHolder.shareViaSocialMedia.setVisibility(View.GONE);
                shareItemViewHolder.sharingInfo.setVisibility(View.GONE);
            }
            shareItemViewHolder.notifyUserToReceive.setVisibility(View.GONE);
        }else {
            shareItemViewHolder.deleteShareItem.setVisibility(View.VISIBLE);
            shareItemViewHolder.shareViaSocialMedia.setVisibility(View.VISIBLE);
            shareItemViewHolder.sharingInfo.setVisibility(View.VISIBLE);
            if(shareItem.isShareTaken()){
                shareItemViewHolder.notifyUserToReceive.setVisibility(View.VISIBLE);
            }else{
                shareItemViewHolder.notifyUserToReceive.setVisibility(View.GONE);
            }
        }

        shareItemViewHolder.shareOwnerName.setText(shareItem.getShareOwnerName());
        shareItemViewHolder.shareAmount.setText(String.valueOf(shareItem.getShareAmount()));
        shareItemViewHolder.hiddenSubShareId.setText(shareItem.getShareId());
        shareItemViewHolder.deleteShareItem.setTag(position);


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
                ParseQuery<ParseObject> deleteFromSharePaidUsers = ParseQuery.getQuery("share_paid_users");
                deleteFromSharePaidUsers.whereEqualTo("share_payer_id", shareId);
                deleteFromSharePaidUsers.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        try {
                            ParseObject.deleteAll(list);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
        shareItemViewHolder.shareViaSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareJamWithUser(shareModels.get(0).getJamId(), shareId, v);
            }
        });
        final View finalConvertView = convertView;
        shareItemViewHolder.sharingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Snackbar snackbar = Snackbar
                        .make(finalConvertView, "", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(mContext.getString(R.string.msg_sharing_info_msg)
                        + System.getProperty("line.separator") +
                        mContext.getString(R.string.share_info_with_friends_msg), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                textView.setTextSize(10);
                textView.setMaxLines(4);
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(tf);
                snackbar.show();
            }
        });
        shareItemViewHolder.notifyUserToReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> users = new ArrayList<>();
                users.add(shareItem.getShareOwnerId());
                ParseObject notifyMsg = createNotificationMsg(users);
                notifyUser(notifyMsg);
            }
        });
        return convertView;
    }
    public ParseObject createNotificationMsg(List<String> users){
        ParseObject msg = new ParseObject(ParseConstants.CLASS_MSG);
        msg.put(ParseConstants.KEY_SENDER_ID, SessionUser.getUser().getUserId());
        msg.put(ParseConstants.KEY_SENDER_NAME, SessionUser.getUser().getUserName());
        msg.put(ParseConstants.KEY_MSG_BODY, mContext.getString(R.string.ntf_receive_msg_body, jName));
        msg.put(ParseConstants.KEY_USERS_IDS, users);
        return msg;
    }
    protected void notifyUser(ParseObject msg){
        msg.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                        ParseQuery<ParseInstallation> parseInstallationParseQuery = ParseInstallation.getQuery();
                        parseInstallationParseQuery.whereEqualTo(ParseConstants.KEY_USER_ID, shareItem.getShareOwnerId());
                        ParsePush parsePush = new ParsePush();
                        parsePush.setQuery(parseInstallationParseQuery);
                        parsePush.setMessage(mContext.getString(R.string.txt_new_notification, SessionUser.getUser().getUserName()));
                        parsePush.sendInBackground(new SendCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(mContext, R.string.alert_done, Toast.LENGTH_LONG).show();
                            }
                        });
                }
            }
        });
    }
    private void shareJamWithUser(String jamId, String shareId, View v) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = v.getContext().getResources().getString(R.string.share_invite_body_msg);
        String link = v.getContext().getResources().getString(R.string.share_invite_link).replace("_JAMID", jamId);
        link = link.replace("_SHAREID", shareId);
        shareBody += "   " + link;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        v.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private static class ShareItemViewHolder{
        TextView shareOwnerName, shareAmount;
        EditText hiddenSubShareId, hiddenCurrentJamAmount;
        ImageView deleteShareItem, shareViaSocialMedia, sharingInfo, notifyUserToReceive;
    }
}