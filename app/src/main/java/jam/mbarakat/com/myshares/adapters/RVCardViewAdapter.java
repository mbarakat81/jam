package jam.mbarakat.com.myshares.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import jam.mbarakat.com.myshares.anim.AnimationUtils;
import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 2/14/2016.
 */
public class RVCardViewAdapter extends RecyclerView.Adapter<RVCardViewAdapter.JamViewHolder> {
    ClickListener clickListener;
    protected Context mContext;
    List<JamModel> currentJams;
    List<SharesModel> jamSharesObject;
    int prevPos = 0;

    public RVCardViewAdapter(Context context, List<JamModel> currentJams){
        this.currentJams = currentJams;
        mContext = context;
    }
    @Override
    public JamViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.current_jam_item, viewGroup, false);
        JamViewHolder pvh = new JamViewHolder(v, mContext);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final JamViewHolder holder, int position) {
        final JamModel jamItem = currentJams.get(position);

        ParseQuery<ParseObject> jShares = ParseQuery.getQuery("jShares");
        jShares.whereEqualTo("shares_jamNo", jamItem.getjId())
                .orderByAscending("share_order");
        jShares.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                jamSharesObject = new ArrayList<SharesModel>();
                for (ParseObject share : list) {

                    SharesModel shareItem = new SharesModel();
                    shareItem.setJamId(share.getString("shares_jamNo"));
                    shareItem.setShareOrder(share.getInt("share_order"));
                    shareItem.setShareDelivered(share.getBoolean("share_status"));
                    shareItem.setjAmount(Integer.parseInt(jamItem.getjAmount()));
                    shareItem.setStartDay(share.getDate("share_due_date").toLocaleString().toString());
                    jamSharesObject.add(shareItem);
                }
                jamItem.setSharesModel(jamSharesObject);

                holder.jamNextShareOwner.setText(jamItem.getjOwnerName());
                holder.jamShareDueDate.setText(HelperClass.getFormatedDateFromString(jamItem.getNextJamDate()));
                holder.jamId.setText(jamItem.getjId());
                holder.jamName.setText(jamItem.getjName());
                holder.jamAmount.setText(jamItem.getjAmount());
            }
        });
        if(position > prevPos){
            AnimationUtils.animate(holder, true);
        }else{
            AnimationUtils.animate(holder, false);
        }
        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return currentJams.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class JamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView jamName, jamShareDueDate, jamNextShareOwner
                , jamAmount, lblJamName, lblNextJamDate, lblOwner, lblShareValue;
        EditText jamId;
        ImageView share_jam, delete_jam, view_jam_details;

        JamViewHolder(View itemView, Context mContext) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/sheba.ttf");

            lblJamName = (TextView) itemView.findViewById(R.id.lblJamName);
            lblJamName.setTypeface(tf);
            lblNextJamDate = (TextView) itemView.findViewById(R.id.lblNextJamDate);
            lblNextJamDate.setTypeface(tf);
            lblOwner = (TextView) itemView.findViewById(R.id.lblOwner);
            lblOwner.setTypeface(tf);
            lblShareValue = (TextView) itemView.findViewById(R.id.lblShareValue);
            lblShareValue.setTypeface(tf);

            jamName = (TextView) itemView.findViewById(R.id.txtCurrentJamName);
            jamName.setTypeface(tf);
            jamShareDueDate = (TextView) itemView.findViewById(R.id.txtNextJamDate);
            jamShareDueDate.setTypeface(tf);
            jamId = (EditText) itemView.findViewById(R.id.hiddenCurrentJamId);
            jamNextShareOwner = (TextView) itemView.findViewById(R.id.txtOwner);
            jamNextShareOwner.setTypeface(tf);
            jamAmount = (TextView) itemView.findViewById(R.id.txtshareValue);
            jamAmount.setTypeface(tf);

            share_jam = (ImageView) itemView.findViewById(R.id.share_jam);
            delete_jam = (ImageView) itemView.findViewById(R.id.delete_jam);
            view_jam_details = (ImageView) itemView.findViewById(R.id.view_jam_details);
            share_jam.setOnClickListener(this);
            delete_jam.setOnClickListener(this);
            view_jam_details.setOnClickListener(this);

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
