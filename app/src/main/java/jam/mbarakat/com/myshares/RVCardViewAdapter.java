package jam.mbarakat.com.myshares;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MBARAKAT on 2/14/2016.
 */
public class RVCardViewAdapter extends RecyclerView.Adapter<RVCardViewAdapter.JamViewHolder> {
    ClickListener clickListener;
    protected Context mContext;
    List<JamModel> currentJams;
    List<SharesModel> jamSharesObject;

    RVCardViewAdapter(Context context, List<JamModel> currentJams){
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
    public void onBindViewHolder(final JamViewHolder holder, int i) {
        final JamModel jamItem = currentJams.get(i);
        /*holder.jamName.setText(currentJams.get(i).getjName());
        holder.jamShareDueDate.setText(currentJams.get(i).getNextJamDate());
        holder.jamNextShareOwner.setText(jamItem.getjOwnerName());
        holder.jamId.setText(currentJams.get(i).getjId());
        holder.jamAmount.setText(jamItem.getjAmount());*/

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
        TextView jamName, jamShareDueDate, jamNextShareOwner, jamAmount;
        EditText jamId;

        JamViewHolder(View itemView, Context mContext) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView) itemView.findViewById(R.id.card_view);

            jamName = (TextView) itemView.findViewById(R.id.txtCurrentJamName);
            jamShareDueDate = (TextView) itemView.findViewById(R.id.txtNextJamDate);
            jamId = (EditText) itemView.findViewById(R.id.hiddenCurrentJamId);
            jamNextShareOwner = (TextView) itemView.findViewById(R.id.txtOwner);
            jamAmount = (TextView) itemView.findViewById(R.id.txtshareValue);
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
