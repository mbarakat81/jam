package jam.mbarakat.com.myshares;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewJamDetailsFragment extends Fragment {
    JamModel jamModel = new JamModel();
    List<SharesModel> jamSharesObject;
    TextView txtNewJamName, txtNextOwner, txtNextDueDate;

    RecyclerView recyclerView;
    JamSharesViewAdapter adapter;
    public ViewJamDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_view_jam_details,container,false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.shareJamDetailsView);
        txtNewJamName = (TextView) layout.findViewById(R.id.txtNewJamName);
        txtNextDueDate = (TextView) layout.findViewById(R.id.txtNextDueDate);
        txtNextOwner = (TextView) layout.findViewById(R.id.txtNextOwner);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData(){
        Intent intent = getActivity().getIntent();
        String jId = intent.getExtras().get("jId").toString();
        jamModel = intent.getParcelableExtra("currentJamModel");

        txtNewJamName.setText(jamModel.getjName());
        txtNextOwner.setText(jamModel.getNextJamOwner());
        txtNextDueDate.setText(jamModel.getNextJamDate());

        ParseQuery<ParseObject> shareOwners = ParseQuery.getQuery("Share_owners");
        shareOwners.whereEqualTo("jamId", jamModel.getjId())
                .whereEqualTo("obs", false);
        shareOwners.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (SharesModel shareModel : jamModel.getSharesModel()) {
                    List<ShareItem> shareItems = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getInt("share_no") == shareModel.getShareOrder()) {
                            ShareItem shareItem = new ShareItem(
                                    list.get(i).getString("share_owner")
                                    ,list.get(i).getString("owner_phone")
                                    , list.get(i).getInt("share_amount")
                                    , list.get(i).getObjectId()
                                    , list.get(i).getInt("share_no"));
                            shareItems.add(shareItem);
                        }
                    }
                    if (shareItems.size() > 0)
                        shareModel.setShareItems(shareItems);

                }
                adapter = new JamSharesViewAdapter(getActivity(),jamModel);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
