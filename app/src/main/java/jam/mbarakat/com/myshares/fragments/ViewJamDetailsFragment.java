package jam.mbarakat.com.myshares.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.modules.ShareItem;
import jam.mbarakat.com.myshares.modules.SharesModel;
import jam.mbarakat.com.myshares.adapters.JamDetailsViewAdapter;


public class ViewJamDetailsFragment extends Fragment {
    JamModel jamModel = new JamModel();
    List<SharesModel> sharesModels = Collections.emptyList();
    private ProgressDialog progress;

    RecyclerView recyclerView;
    JamDetailsViewAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JamDetailsViewAdapter(sharesModels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_view_jam_details,container,false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.shareJamDetailsView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData(){
        Intent intent;
        intent = getActivity().getIntent();
        jamModel = intent.getParcelableExtra("currentJamModel");

        ParseQuery<ParseObject> shareOwners = ParseQuery.getQuery("Share_owners");
        shareOwners.whereEqualTo("jamId", jamModel.getjId())
                .whereEqualTo("obs", false);
        shareOwners.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    progress = ProgressDialog.show(getActivity(), "", "", true);
                    for (SharesModel shareModel : jamModel.getSharesModel()) {
                        List<ShareItem> shareItems = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {

                            if (list.get(i).getInt("share_no") == shareModel.getShareOrder()) {
                                ShareItem shareItem = new ShareItem(
                                        list.get(i).getString("share_owner")
                                        , list.get(i).getString("owner_phone")
                                        , list.get(i).getInt("share_amount")
                                        , list.get(i).getObjectId()
                                        , list.get(i).getInt("share_no")
                                        , list.get(i).getString("share_owner_id"));
                                shareItems.add(shareItem);
                            }
                        }


                        if (shareItems.size() > 0)
                            shareModel.setShareItems(shareItems);
                    }
                    progress.dismiss();
                    adapter = new JamDetailsViewAdapter(getActivity(), jamModel);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

}
