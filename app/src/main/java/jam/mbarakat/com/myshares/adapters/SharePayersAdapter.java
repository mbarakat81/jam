package jam.mbarakat.com.myshares.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import jam.mbarakat.com.myshares.modules.SharePayersModel;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/26/2016.
 */
public class SharePayersAdapter extends ArrayAdapter<SharePayersModel> {

    Context mContext;
    private SharesModel jamShare;
    public View parentView;
    Typeface tf;
    String jName;

    SharePayersViewHolder sharePayersViewHolder;

    public SharePayersAdapter(Context context, SharesModel jamShare, View view, String jName) {
        super(context, R.layout.share_cashing_user, jamShare.getSharePayersModels());
        this.jamShare = jamShare;
        this.mContext = context;
        this.parentView = view;
        this.jName = jName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/sheba.ttf");
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.share_cashing_user, null);
            sharePayersViewHolder = new SharePayersViewHolder();
            sharePayersViewHolder.paid = (ImageView)convertView.findViewById(R.id.paid);
            sharePayersViewHolder.notifyUserToPay = (ImageButton)convertView.findViewById(R.id.notifyUserToPay);
            sharePayersViewHolder.userPaymentPaid = (ImageView)convertView.findViewById(R.id.userPaymentPaid);
            sharePayersViewHolder.userPaymentPending = (ImageView)convertView.findViewById(R.id.userPaymentPending);
            sharePayersViewHolder.txtSharePayer = (TextView)convertView.findViewById(R.id.txtSharePayer);
            sharePayersViewHolder.txtSharePayer.setTypeface(tf);
            sharePayersViewHolder.txtSharePayer.setTypeface(tf);
            sharePayersViewHolder.lblAskForPayment = (TextView)convertView.findViewById(R.id.lblAskForPayment);
            sharePayersViewHolder.lblAskForPayment.setTypeface(tf);
            sharePayersViewHolder.txtSharePaidAmount = (TextView)convertView.findViewById(R.id.txtSharePaidAmount);
            sharePayersViewHolder.txtSharePaidAmount.setTypeface(tf);
            convertView.setTag(sharePayersViewHolder);
        }else {
            sharePayersViewHolder = (SharePayersViewHolder) convertView.getTag();
        }

        sharePayersViewHolder.txtSharePayer.setText(jamShare.getSharePayersModels().get(position).getPayerName());
        sharePayersViewHolder.txtSharePaidAmount.setText(String.valueOf(jamShare.getSharePayersModels().get(position).getAmount()));

        if(jamShare.getSharePayersModels().get(position).isPaid()){
            sharePayersViewHolder.userPaymentPaid.setVisibility(View.VISIBLE);
            sharePayersViewHolder.userPaymentPending.setVisibility(View.INVISIBLE);
            sharePayersViewHolder.notifyUserToPay.setVisibility(View.GONE);
            if(!jamShare.getJamModel().isMysJam()){
                sharePayersViewHolder.lblAskForPayment.setText(R.string.lbl_share_paid_user_side);
            }else{
                sharePayersViewHolder.lblAskForPayment.setText(R.string.lbl_cancel_payment);
            }
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));

        }else{
            sharePayersViewHolder.userPaymentPaid.setVisibility(View.INVISIBLE);
            sharePayersViewHolder.userPaymentPending.setVisibility(View.VISIBLE);
            if(jamShare.getSharePayersModels().get(position).isPayerVerified() && jamShare.getJamModel().isMysJam())
                sharePayersViewHolder.notifyUserToPay.setVisibility(View.VISIBLE);
            else
                sharePayersViewHolder.notifyUserToPay.setVisibility(View.GONE);

            if(!jamShare.getJamModel().isMysJam()){
                sharePayersViewHolder.lblAskForPayment.setText("لم يتم التحصيل");
            }else{
                sharePayersViewHolder.lblAskForPayment.setText(R.string.lbl_ask_if_paid);
            }
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        final View finalConvertView = convertView;
        if(!jamShare.getJamModel().isMysJam()){
            sharePayersViewHolder.paid.setVisibility(View.GONE);
        }else{
            sharePayersViewHolder.paid.setVisibility(View.VISIBLE);
        }

        sharePayersViewHolder.paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("share_paid_users");
                query.getInBackground(jamShare.getSharePayersModels().get(position).getId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        ParseQuery<ParseObject> sharesQuery = ParseQuery.getQuery("jShares");
                        parseObject.put("status", !jamShare.getSharePayersModels().get(position).isPaid());
                        try {
                            parseObject.save();
                            int amount = parseObject.getInt("amount");
                            ParseObject sharesObject = sharesQuery.get(parseObject.getString("share_id"));
                            jamShare.getSharePayersModels().get(position).setIsPaid(parseObject.getBoolean("status"));

                            if (parseObject.getBoolean("status")) {
                                sharePayersViewHolder.userPaymentPaid.setVisibility(View.VISIBLE);
                                sharePayersViewHolder.userPaymentPending.setVisibility(View.INVISIBLE);
                                sharePayersViewHolder.lblAskForPayment.setText(R.string.lbl_cancel_payment);
                                finalConvertView.setBackgroundColor(mContext.getResources().getColor(R.color.holo_green_dark));
                            } else {
                                sharePayersViewHolder.userPaymentPaid.setVisibility(View.INVISIBLE);
                                sharePayersViewHolder.userPaymentPending.setVisibility(View.VISIBLE);
                                sharePayersViewHolder.lblAskForPayment.setText(R.string.lbl_ask_if_paid);
                                finalConvertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                amount = amount * -1;
                            }
                            sharesObject.put("share_paid_amount", sharesObject.getInt("share_paid_amount") + amount);
                            sharesObject.save();
                            jamShare.setSharePaidAmount(sharesObject.getInt("share_paid_amount"));
                            notifyDataSetChanged();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
        sharePayersViewHolder.notifyUserToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                v.startAnimation(shake);
                ParseObject notifyMsg = createNotificationMsg(jamShare.getSharePayersModels().get(position).getUser_id());
                notifyUser(notifyMsg, jamShare.getSharePayersModels().get(position).getUser_id());
            }

        });
        return convertView;
    }
    public ParseObject createNotificationMsg(String userId){
        List<String> users = new ArrayList<>();
        users.add(userId);
        ParseObject msg = new ParseObject(ParseConstants.CLASS_MSG);
        msg.put(ParseConstants.KEY_SENDER_ID, SessionUser.getUser().getUserId());
        msg.put(ParseConstants.KEY_SENDER_NAME, SessionUser.getUser().getUserName());
        msg.put(ParseConstants.KEY_MSG_BODY,  mContext.getString(R.string.ntf_msg_body, jName));
        msg.put(ParseConstants.KEY_USERS_IDS, users);
        return msg;
    }
    protected void notifyUser(ParseObject msg, final String userId){
        msg.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) return;
                ParseQuery<ParseInstallation> parseInstallationParseQuery = ParseInstallation.getQuery();
                parseInstallationParseQuery.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
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
        });
    }
    private static class SharePayersViewHolder{
        TextView txtSharePayer, lblAskForPayment, txtSharePaidAmount;
        ImageView userPaymentPaid, userPaymentPending, paid ;
        ImageButton notifyUserToPay;
    }
}
