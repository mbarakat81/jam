package jam.mbarakat.com.myshares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MBARAKAT on 2/6/2016.
 */
public class CurrentJamAdapter extends ArrayAdapter<JamModel> {
    protected Context mContext;
    protected List<ParseObject> mJams;
    List<JamModel> currentJams;
    List<SharesModel> jamSharesObject;

    public CurrentJamAdapter(Context context, List<JamModel> mJams) {
        super(context, R.layout.current_jam_item, mJams);
        mContext = context;
        this.currentJams = mJams;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final JamModel jamItem = currentJams.get(position);
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.current_jam_item,null);
            holder = new ViewHolder();
            holder.jamId = (EditText) convertView.findViewById(R.id.hiddenCurrentJamId);
            holder.jamName = (TextView) convertView.findViewById(R.id.txtCurrentJamName);
            holder.jamShareDueDate = (TextView) convertView.findViewById(R.id.txtNextJamDate);
            holder.jamNextShareOwner = (TextView) convertView.findViewById(R.id.txtOwner);
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

                    holder.jamNextShareOwner.setText(jamItem.getNextJamOwner());
                    holder.jamShareDueDate.setText(HelperClass.getFormatedDateFromString(jamItem.getNextJamDate()));
                    holder.jamId.setText(jamItem.getjId());
                    holder.jamName.setText(jamItem.getjName());

                }
            });
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView jamName, jamShareDueDate, jamNextShareOwner;
        EditText jamId;

    }
}
