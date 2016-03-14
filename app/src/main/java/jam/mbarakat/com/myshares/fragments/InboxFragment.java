package jam.mbarakat.com.myshares.fragments;


import android.graphics.Typeface;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.adapters.InboxRecyclerViewAdapter;
import jam.mbarakat.com.myshares.helpers.ParseConstants;
import jam.mbarakat.com.myshares.helpers.SessionUser;
import jam.mbarakat.com.myshares.modules.InboxModel;

/**
 * Created by MBARAKAT on 1/30/2016.
 */
public class InboxFragment extends ListFragment {

    public static final String TAG = InboxFragment.class.getSimpleName();
    RecyclerView inboxRecyclerView;
    private InboxRecyclerViewAdapter inboxRecyclerViewAdapter;
    private TextView txtEmpty;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        txtEmpty = (TextView) rootView.findViewById(R.id.txtEmpty);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/sheba.ttf");
        txtEmpty.setTypeface(tf);
        inboxRecyclerView = (RecyclerView)rootView.findViewById(R.id.inboxRecycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        inboxRecyclerView.setLayoutManager(mLinearLayoutManager);
        inboxRecyclerView.setAdapter(inboxRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseQuery<ParseObject> inboxQuery = ParseQuery.getQuery("message");
        inboxQuery.whereEqualTo(ParseConstants.KEY_USERS_IDS, SessionUser.getUser().getUserId());
        inboxQuery.setLimit(20);
        inboxQuery.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        inboxQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e==null){
                    List<InboxModel> inboxList = new ArrayList<InboxModel>();
                    for(ParseObject message:messages){
                        inboxList.add(new InboxModel(message));
                    }
                    if(inboxList.size()==0) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/sheba.ttf");
                        txtEmpty.setTypeface(tf);
                    }
                    else {
                        txtEmpty.setVisibility(View.GONE);
                        inboxRecyclerViewAdapter = new InboxRecyclerViewAdapter(getContext(), inboxList);
                        inboxRecyclerView.setAdapter(inboxRecyclerViewAdapter);
                    }
                }
            }
        });
    }
}
