package jam.mbarakat.com.myshares.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.anim.AnimationUtils;
import jam.mbarakat.com.myshares.modules.InboxModel;

/**
 * Created by MBARAKAT on 3/10/2016.
 */
public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<InboxRecyclerViewAdapter.InboxViewHolder>{
    List<InboxModel> inboxMessages;
    Context mContext;
    int prevPos = 0;

    public InboxRecyclerViewAdapter(Context context, List<InboxModel> inboxMsgs){
        this.inboxMessages = inboxMsgs;
        this.mContext = context;
    }
    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item, parent, false);
        InboxViewHolder inboxViewHolder = new InboxViewHolder(view);
        return inboxViewHolder;
    }

    @Override
    public void onBindViewHolder(InboxViewHolder inboxViewHolder, int position) {
        inboxViewHolder.notificationBody.setText(inboxMessages.get(position).getMsgBody());
        inboxViewHolder.txtSenderName.setText(mContext.getString(R.string.ntf_from, inboxMessages.get(position).getSenderName()));
        inboxViewHolder.txtNotificationDate.setText(inboxMessages.get(position).getMsgDate().toString().replace("EET", ""));
        if(position > prevPos){
            AnimationUtils.animate(inboxViewHolder, true);
        }else{
            AnimationUtils.animate(inboxViewHolder, false);
        }
        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return inboxMessages.size();
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder {
        TextView txtSenderName, notificationBody, txtNotificationDate;
        Typeface tf;
        public InboxViewHolder(View itemView) {
            super(itemView);
            tf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/sheba.ttf");
            txtSenderName = (TextView) itemView.findViewById(R.id.txtSenderName);
            notificationBody = (TextView) itemView.findViewById(R.id.notificationBody);
            txtNotificationDate = (TextView) itemView.findViewById(R.id.txtNotificationDate);

            txtSenderName.setTypeface(tf);
            notificationBody.setTypeface(tf);
            txtNotificationDate.setTypeface(tf);

        }
    }
}
