package jam.mbarakat.com.myshares.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.List;

import jam.mbarakat.com.myshares.anim.AnimationUtils;
import jam.mbarakat.com.myshares.helpers.FlipAnimation;
import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.helpers.ParseConstants;
import jam.mbarakat.com.myshares.helpers.SessionUser;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.ShareItem;
import jam.mbarakat.com.myshares.modules.SharePayersModel;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/7/2016.
 */
public class JamDetailsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    ClickListener clickListener;
    List<SharesModel> data = Collections.emptyList();
    Context context = null;
    private LayoutInflater layoutInflater;
    JamModel jamModel;
    View parentView;
    boolean isMyJam;
    int prevPos = 0;
    private SharesModel sharesModel;

    public JamDetailsViewAdapter(List<SharesModel> data){
        this.data = data;
    }
    public JamDetailsViewAdapter(Context context, JamModel data){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.isMyJam = data.isMysJam();
        this.jamModel = data;
        this.data = data.getSharesModel();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER)
        {
            View view = layoutInflater.inflate(R.layout.jam_header, parent, false);
            this.parentView = view;
            return new JamHeaderViewHolder(view);
        }else if(viewType == TYPE_ITEM)
        {
            View view = layoutInflater.inflate(R.layout.shares_row, parent, false);
            this.parentView = view;
            SharesViewHolder sharesViewHolder = new SharesViewHolder(view);
            return sharesViewHolder;
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
            if(!isMyJam){
                jamHeaderViewHolder.imgNotifyAllUsers.setVisibility(View.GONE);
                jamHeaderViewHolder.lblNotify.setVisibility(View.GONE);
            }
            else{
                jamHeaderViewHolder.imgNotifyAllUsers.setVisibility(View.VISIBLE);
                jamHeaderViewHolder.lblNotify.setVisibility(View.VISIBLE);
            }

        }else if(holder instanceof SharesViewHolder){
            SharesViewHolder sharesViewHolder = (SharesViewHolder)holder;
            sharesModel = data.get(position - 1);
            sharesModel.setJamModel(jamModel);
            sharesViewHolder.startDay.setText(HelperClass.getFormatedDateFromString(sharesModel.getStartDay()));
            sharesViewHolder.amount.setText("");
            sharesViewHolder.addRecName.setText("");
            sharesViewHolder.lblShareOrder.setText(String.format(context.getResources().getString(R.string.share_order),sharesModel.getShareOrder()));
            sharesViewHolder.shareItems = sharesModel.getShareItems();
            sharesViewHolder.sharePayersModels = sharesModel.getSharePayersModels();
            sharesViewHolder.shareOwnersAdapter = new ShareOwnersAdapter(context, parentView,sharesModel);
            sharesViewHolder.lvParticipants.setAdapter(sharesViewHolder.shareOwnersAdapter);
            sharesViewHolder.cardBack.setVisibility(View.GONE);
            sharesViewHolder.cardFace.setVisibility(View.VISIBLE);
            sharesViewHolder.flipToBack.setText(context.getString(R.string.lbl_payers_list));
            showHideDeliveredView(sharesViewHolder, sharesModel);
            if(jamModel.isMysJam()){
                sharesViewHolder.lblNotifyAllUsersToPay.setVisibility(View.VISIBLE);
                sharesViewHolder.notifyAllUsersToPay.setVisibility(View.VISIBLE);
            }else {
                sharesViewHolder.lblNotifyAllUsersToPay.setVisibility(View.GONE);
                sharesViewHolder.notifyAllUsersToPay.setVisibility(View.GONE);
            }
        }
        if(position > prevPos){
            AnimationUtils.animate(holder, true);
        }else{
            AnimationUtils.animate(holder, false);
        }
        prevPos = position;
    }

    private void showHideDeliveredView(SharesViewHolder sharesViewHolder, SharesModel sharesModel) {
        if(sharesModel.isSharePaid(data.size())){
            hideAddRec(true, sharesViewHolder);
            sharesViewHolder.btnDelivered.setVisibility(View.VISIBLE);
        }else{
            if(!isMyJam || jamModel.isOneShareBeenDelivered()){
                hideAddRec(true, sharesViewHolder);
            }else{
                hideAddRec(false, sharesViewHolder);
            }
            sharesViewHolder.btnDelivered.setVisibility(View.GONE);
        }
    }

    private void hideAddRec(boolean hide, SharesViewHolder sharesViewHolder){
        if(hide) {
            sharesViewHolder.btnAddRecToShare.setVisibility(View.GONE);
            sharesViewHolder.amount.setVisibility(View.GONE);
            sharesViewHolder.addRecName.setVisibility(View.GONE);
        }else{
            sharesViewHolder.btnAddRecToShare.setVisibility(View.VISIBLE);
            sharesViewHolder.amount.setVisibility(View.VISIBLE);
            sharesViewHolder.addRecName.setVisibility(View.VISIBLE);
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
        if(data.size() == 0){
            return 0;
        }
        return data.size()+1;
    }

    class JamHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView lblNewJamName1, lblSharesNo, lblNewJamAmount1, lblNewJamDate1,jamName, jamAmount, sharesNo, jamDate, lblNotify;
        CheckBox jamIsPublic;
        ImageView imgNotifyAllUsers;

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
            lblNotify = (TextView)itemView.findViewById(R.id.lblNotify);
            lblNotify.setTypeface(tf);

            lblNewJamName1 = (TextView)itemView.findViewById(R.id.lblNewJamName1);
            lblNewJamName1.setTypeface(tf);
            lblSharesNo = (TextView)itemView.findViewById(R.id.lblSharesNo);
            lblSharesNo.setTypeface(tf);
            lblNewJamAmount1 = (TextView)itemView.findViewById(R.id.lblNewJamAmount1);
            lblNewJamAmount1.setTypeface(tf);
            lblNewJamDate1 = (TextView)itemView.findViewById(R.id.lblNewJamDate1);
            lblNewJamDate1.setTypeface(tf);
            imgNotifyAllUsers = (ImageView) itemView.findViewById(R.id.imgNotify);
            jamIsPublic = (CheckBox)itemView.findViewById(R.id.cbNewJamIsPublic);
            imgNotifyAllUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v);
                }
            });

        }

        private void sendNotificationToAll(View v) {
            Animation shake = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);
            v.startAnimation(shake);
            ParseObject notifyMsg = createNotificationMsg();
            notifyAllUsers(notifyMsg);
        }

        private void showDialog(final View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(R.string.dlg_ntf_title);
            alert.setMessage(R.string.dlg_ntf_msg);
            alert.setNegativeButton(R.string.delete_jam_cancel_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }
            );
            alert.setPositiveButton(R.string.dlg_nft_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendNotificationToAll(v);
                }
            });
            alert.show();
        }
        public ParseObject createNotificationMsg(){
            ParseObject msg = new ParseObject(ParseConstants.CLASS_MSG);
            try {
                msg.put(ParseConstants.KEY_SENDER_ID, SessionUser.getUser().getUserId());
                msg.put(ParseConstants.KEY_SENDER_NAME, SessionUser.getUser().getUserName());
                msg.put(ParseConstants.KEY_MSG_BODY, getNotificationMsgBody());
                msg.put(ParseConstants.KEY_USERS_IDS, jamModel.getJamUsers());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return msg;
        }

        protected String getNotificationMsgBody() {
            String msg;
            msg = context.getString(R.string.ntf_msg_body, jamModel.getjName());
            return msg;
        }

        protected void notifyAllUsers(ParseObject msg){
            msg.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        try {
                            ParseQuery<ParseInstallation> parseInstallationParseQuery = ParseInstallation.getQuery();
                            parseInstallationParseQuery.whereContainedIn(ParseConstants.KEY_USER_ID, jamModel.getJamUsers());
                            ParsePush parsePush = new ParsePush();
                            parsePush.setQuery(parseInstallationParseQuery);
                            parsePush.setMessage(context.getString(R.string.txt_new_notification, SessionUser.getUser().getUserName()));
                            parsePush.sendInBackground(new SendCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(context, R.string.alert_done, Toast.LENGTH_LONG).show();
                                }
                            });


                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
    class SharesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        EditText amount,addRecName;
        TextView startDay, lblStartDay, lblNotifyAllUsersToPay, flipToBack, lblShareOrder, backText;
        ImageView btnAddRecToShare, notifyAllUsersToPay;
        Button btnDelivered;
        ListView lvParticipants, lvSharePayments;

        public View view;

        List<ShareItem> shareItems= new ArrayList<>();
        List<SharePayersModel> sharePayersModels = Collections.EMPTY_LIST;
        ShareOwnersAdapter shareOwnersAdapter = null;
        SharePayersAdapter sharePayersAdapter = null;
        int mAmount ;


        View rootLayout;
        View cardFace;
        View cardBack;

        public SharesViewHolder(View itemView) {
            super(itemView);
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/sheba.ttf");
            itemView.setOnClickListener(this);

            this.view = itemView;
            startDay = (TextView) view.findViewById(R.id.txtShareStartDay);
            startDay.setTypeface(tf);
            lblNotifyAllUsersToPay = (TextView) view.findViewById(R.id.lblNotifyAllUsersToPay);
            lblNotifyAllUsersToPay.setTypeface(tf);
            backText = (TextView) view.findViewById(R.id.backText);
            backText.setTypeface(tf);
            lblShareOrder = (TextView) view.findViewById(R.id.lblShareOrder);
            lblShareOrder.setTypeface(tf);
            lblStartDay = (TextView) view.findViewById(R.id.lblShareStartDay);
            lblStartDay.setTypeface(tf);
            btnDelivered = (Button) view.findViewById(R.id.btnDelivered);
            amount = (EditText)view.findViewById(R.id.subShareAmount);
            amount.setTypeface(tf);
            addRecName = (EditText) view.findViewById(R.id.addRecName1);
            addRecName.setTypeface(tf);

            rootLayout = view.findViewById(R.id.main_view);
            cardFace = view.findViewById(R.id.front);
            cardBack = view.findViewById(R.id.back);

            flipToBack = (TextView) view.findViewById(R.id.btnFlipToBack);
            flipToBack.setTypeface(tf);
            flipToBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePayersModels = new ArrayList<>();
                    ParseQuery<ParseObject> sharePaidUsers = ParseQuery.getQuery("share_paid_users");
                    sharePaidUsers.whereEqualTo("share_id", data.get(getPosition() - 1).getShareId())
                            .whereEqualTo("obs", false);
                    sharePaidUsers.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            for (ParseObject parseObject : list)
                                sharePayersModels.add(new SharePayersModel(
                                        parseObject.getString("share_payer_name")
                                        , parseObject.getString("share_id")
                                        , parseObject.getBoolean("status")
                                        , parseObject.getObjectId()
                                        , parseObject.getString("share_payer_id")
                                        , parseObject.getString("user_id")
                                        , parseObject.getString("user_name")
                                        , parseObject.getInt("amount")
                                ));
                            SharesModel sharesModel = data.get(getPosition() - 1);
                            sharesModel.setSharePayersModels(sharePayersModels);
                            sharesModel.setJamModel(jamModel);
                            sharePayersAdapter = new SharePayersAdapter(context, data.get(getPosition() - 1), parentView, jamModel.getjName());
                            lvSharePayments.setAdapter(sharePayersAdapter);
                        }
                    });

                    flipCard();
                    String text = ((TextView) v).getText().toString();
                    if (!text.equals(context.getString(R.string.lbl_back_to_share)))
                        ((TextView) v).setText(context.getString(R.string.lbl_back_to_share));
                    else {
                        ((TextView) v).setText(context.getString(R.string.lbl_payers_list));
                        showHideDeliveredView(data.get(getPosition() - 1));
                    }
                }
            });

            lvParticipants = (ListView) view.findViewById(R.id.lvSharesParticipants);
            lvParticipants.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            lvSharePayments = (ListView) view.findViewById(R.id.lvSharePayments);
            lvSharePayments.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            notifyAllUsersToPay = (ImageView) view.findViewById(R.id.notifyAllUsersToPay);
            btnAddRecToShare = (ImageView)view.findViewById(R.id.btnAddRecToShare);

            btnAddRecToShare.setOnClickListener(new View.OnClickListener() {
                public ProgressDialog progress;

                @Override
                public void onClick(View v) {
                    if (addRecName.getText().toString().isEmpty() || amount.getText().toString().isEmpty() || amount.getText().toString().equals("0")) {
                        Toast.makeText(context, R.string.data_filling_issue, Toast.LENGTH_LONG).show();
                    } else {
                        mAmount = Integer.parseInt(amount.getText().toString());
                        final SharesModel sharesModel = jamModel.getSharesModel().get(getPosition()-1);
                        final List<ShareItem> shareItems = sharesModel.getShareItems();
                        int sum=0;
                        for(ShareItem shareItem : shareItems){
                            sum = sum + shareItem.getShareAmount();
                        }
                        if ((sum + mAmount) > Integer.parseInt(jamModel.getjAmount())) {
                            Toast.makeText(context, R.string.sub_shares_count_error, Toast.LENGTH_LONG).show();
                        } else {

                            progress = ProgressDialog.show(context, "",
                                    "", true);
                            final String name = addRecName.getText().toString();
                            final String phone = "";

                            final ParseObject parseObject = new ParseObject("Share_owners");
                            parseObject.put("jamId", jamModel.getjId());
                            parseObject.put("share_owner", addRecName.getText().toString());
                            parseObject.put("share_owner_id", "");
                            parseObject.put("obs", false);
                            parseObject.put("share_no", sharesModel.getShareOrder());
                            parseObject.put("share_amount", Integer.parseInt(amount.getText().toString()));

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                if(e==null){
                                    List<ParseObject> parseObjectList = new ArrayList<>();
                                    for(SharesModel sharesModel: data){
                                        ParseObject saveToSharePayers = new ParseObject("share_paid_users");
                                        saveToSharePayers.put("share_payer_id",parseObject.getObjectId());
                                        saveToSharePayers.put("share_id",sharesModel.getShareId());
                                        saveToSharePayers.put("share_payer_name",parseObject.getString("share_owner"));
                                        saveToSharePayers.put("status",false);
                                        saveToSharePayers.put("obs",false);
                                        saveToSharePayers.put("amount", parseObject.getInt("share_amount"));
                                        parseObjectList.add(saveToSharePayers);
                                    }

                                    try {

                                        ParseObject.saveAll(parseObjectList);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }


                                    shareItems.add(new ShareItem(name
                                            , phone
                                            , Integer.parseInt(amount.getText().toString())
                                            , parseObject.getObjectId()
                                            , sharesModel.getShareOrder()
                                            , parseObject.getString("share_owner_id")));

                                    jamModel.getSharesModel().get(getPosition()-1).setShareItems(shareItems);
                                    shareOwnersAdapter = new ShareOwnersAdapter(context, view, jamModel.getSharesModel().get(getPosition()-1));
                                    lvParticipants.setAdapter(shareOwnersAdapter);
                                    addRecName.setText("");
                                    addRecName.requestFocus();
                                    amount.setText("");
                                    progress.dismiss();
                                }else {
                                    Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_LONG).show();
                                }
                            }

                            });
                        }
                    }
                }
            });
            notifyAllUsersToPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v);
                }
            });
        }

        private void sendNotificationToAll(View v) {
            ArrayList users = new ArrayList();
            Animation shake = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.shake);
            v.startAnimation(shake);
            for(SharePayersModel sharePayersModel: sharePayersModels){
                if(sharePayersModel.isPayerVerified() && !sharePayersModel.isPaid())
                    users.add(sharePayersModel.getUser_id());
            }
            ParseObject notifyMsg = createNotificationMsg(users);
            notifyUsers(notifyMsg, users);
        }

        private void showDialog(final View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(R.string.dlg_ntf_title);
            alert.setMessage(R.string.dlg_ntf_msg);
            alert.setNegativeButton(R.string.delete_jam_cancel_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }
            );
            alert.setPositiveButton(R.string.dlg_nft_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendNotificationToAll(v);
                }
            });
            alert.show();
        }
        public ParseObject createNotificationMsg(List<String> users){
            ParseObject msg = new ParseObject(ParseConstants.CLASS_MSG);
            msg.put(ParseConstants.KEY_SENDER_ID, SessionUser.getUser().getUserId());
            msg.put(ParseConstants.KEY_SENDER_NAME, SessionUser.getUser().getUserName());
            msg.put(ParseConstants.KEY_MSG_BODY, context.getString(R.string.ntf_msg_body, jamModel.getjName()));
            msg.put(ParseConstants.KEY_USERS_IDS, users);
            return msg;
        }

        protected void notifyUsers(ParseObject msg, final List<String> users){
            msg.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseQuery<ParseInstallation> parseInstallationParseQuery = ParseInstallation.getQuery();
                        parseInstallationParseQuery.whereContainedIn(ParseConstants.KEY_USER_ID, users);
                        ParsePush parsePush = new ParsePush();
                        parsePush.setQuery(parseInstallationParseQuery);
                        parsePush.setMessage(context.getString(R.string.txt_new_notification, SessionUser.getUser().getUserName()));
                        parsePush.sendInBackground(new SendCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(context, R.string.alert_done, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
        private void showHideDeliveredView( SharesModel sharesModel) {
            if(sharesModel.isSharePaid(data.size())){
                btnAddRecToShare.setVisibility(View.GONE);
                amount.setVisibility(View.GONE);
                addRecName.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.VISIBLE);
            }else{
                if(!isMyJam || jamModel.isJamStarted()){
                    btnAddRecToShare.setVisibility(View.GONE);
                    amount.setVisibility(View.GONE);
                    addRecName.setVisibility(View.GONE);
                }else{
                    btnAddRecToShare.setVisibility(View.VISIBLE);
                    amount.setVisibility(View.VISIBLE);
                    addRecName.setVisibility(View.VISIBLE);
                }
                btnDelivered.setVisibility(View.GONE);
            }
        }

    @Override
    public void onClick(View v) {
        if(clickListener !=null){
            clickListener.OnItemClick(v, getPosition());
        }
    }
        private void flipCard()
        {


            FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

            if (cardFace.getVisibility() == View.GONE)
            {
                flipAnimation.reverse();
            }
            rootLayout.startAnimation(flipAnimation);
        }
    }

    public interface ClickListener{
        public void OnItemClick(View view, int pos);
    }
}
