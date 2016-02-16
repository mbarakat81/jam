package jam.mbarakat.com.myshares;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Created by MBARAKAT on 1/30/2016.
 */
public class CurrentJamsFragment extends ListFragment implements RVCardViewAdapter.ClickListener{
    private SmoothProgressBar smoothProgressBar;
    TextView empty;
    protected ParseUser mCurrentUser;
    protected List<ParseObject> mJams;
    CurrentJamAdapter adapter = null;
    List<JamModel> currentJams = Collections.emptyList();
    RecyclerView rv;
    private RVCardViewAdapter RVCardsdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_jams, container, false);
        rv = (RecyclerView)rootView.findViewById(R.id.rvCardView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        smoothProgressBar = (SmoothProgressBar) rootView.findViewById(R.id.progressBar);
        smoothProgressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(getContext())
                .interpolator(new AccelerateInterpolator()).build());
        smoothProgressBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) smoothProgressBar.getIndeterminateDrawable()).getStrokeWidth()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean activeJams = true;
       // displayMyActiveCurrentJams();
        displayMyActiveCurrentJamsAsCardView();
    }

    private void displayMyActiveCurrentJams(){
        mCurrentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Share_owners");
        q.whereEqualTo("owner_phone", mCurrentUser.getString("phone"))
        .whereEqualTo("obs", false);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    List<String> jamIds = new ArrayList<>();
                    for (ParseObject jam : list) {
                        jamIds.add(jam.getString("jamId"));
                    }
                    ParseQuery<ParseObject> queryByJamId = ParseQuery.getQuery(ParseConstants.CLASS_JAM);
                    queryByJamId.whereContainedIn(ParseConstants.KEY_JAM_ID, jamIds)
                            .whereEqualTo(ParseConstants.KEY_JSTATUS, true);
                    ParseQuery<ParseObject> queryByOwnerId = ParseQuery.getQuery(ParseConstants.CLASS_JAM);
                    queryByOwnerId.whereEqualTo("jCreator", mCurrentUser.getObjectId())
                            .whereEqualTo(ParseConstants.KEY_JSTATUS, true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(queryByJamId);
                    queries.add(queryByOwnerId);

                    ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                    mainQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> results, ParseException e) {
                            if (e == null) {
                                mJams = results;
                                currentJams = new ArrayList<JamModel>();
                                for (ParseObject parseObject : results) {
                                    JamModel jamModel = new JamModel();
                                    jamModel.setjId(parseObject.getObjectId());
                                    jamModel.setjIsPublic(parseObject.getBoolean("jIsPublic") ? "true" : "false");
                                    jamModel.setjOwnerId(parseObject.getString("jCreator"));
                                    jamModel.setjAmount(String.valueOf(parseObject.getNumber("jAmount")));

                                    jamModel.setjDate(HelperClass.getStringFormatDate(parseObject.getDate("jStart_date"), getContext()));
                                    jamModel.setSharesNo(String.valueOf(parseObject.getNumber("jSharesNo")));
                                    jamModel.setjPeriod(parseObject.getString("jPeriod"));
                                    jamModel.setjName(parseObject.getString("jName"));
                                    jamModel.setjStatus("true");
                                    currentJams.add(jamModel);
                                }

                                adapter = new CurrentJamAdapter(getListView().getContext(), currentJams);
                                setListAdapter(adapter);

                            } else {

                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle bundle = new Bundle();

        JamModel currentJam = currentJams.get(position);
        Intent intent = new Intent(getActivity(), ViewJamDetailsActivity.class);
        bundle.putParcelable("currentJamModel", currentJam);
        intent.putExtras(bundle);
        intent.putExtra("jId", currentJam.getjId());
        startActivity(intent);
    }

    private void displayMyActiveCurrentJamsAsCardView(){
        smoothProgressBar.setVisibility(ProgressBar.VISIBLE);
        smoothProgressBar.progressiveStart();
        mCurrentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Share_owners");
        q.whereEqualTo("owner_phone", mCurrentUser.getString("phone"))
                .whereEqualTo("obs", false);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                        List<String> jamIds = new ArrayList<>();
                        for (ParseObject jam : list) {
                            jamIds.add(jam.getString("jamId"));
                        }
                        ParseQuery<ParseObject> queryByJamId = ParseQuery.getQuery(ParseConstants.CLASS_JAM);
                        queryByJamId.whereContainedIn(ParseConstants.KEY_JAM_ID, jamIds)
                                .whereEqualTo(ParseConstants.KEY_JSTATUS, true);
                        ParseQuery<ParseObject> queryByOwnerId = ParseQuery.getQuery(ParseConstants.CLASS_JAM);
                        queryByOwnerId.whereEqualTo("jCreator", mCurrentUser.getObjectId())
                                .whereEqualTo(ParseConstants.KEY_JSTATUS, true);
                        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                        queries.add(queryByJamId);
                        queries.add(queryByOwnerId);

                        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                        mainQuery.orderByDescending("jStart_date");
                        mainQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                                if (e == null) {
                                    if (results.size() > 0) {
                                        mJams = results;
                                        currentJams = new ArrayList<JamModel>();
                                        for (ParseObject parseObject : results) {
                                            JamModel jamModel = new JamModel();
                                            jamModel.setjId(parseObject.getObjectId());
                                            jamModel.setjIsPublic(parseObject.getBoolean("jIsPublic") ? "true" : "false");
                                            jamModel.setjOwnerId(parseObject.getString("jCreator"));
                                            jamModel.setjAmount(String.valueOf(parseObject.getNumber("jAmount")));
                                            jamModel.setjOwnerName(parseObject.getString("jCreatorName"));
                                            jamModel.setjDate(HelperClass.getStringFormatDate(parseObject.getDate("jStart_date"), getContext()));
                                            jamModel.setSharesNo(String.valueOf(parseObject.getNumber("jSharesNo")));
                                            jamModel.setjPeriod(parseObject.getString("jPeriod"));
                                            jamModel.setjName(parseObject.getString("jName"));
                                            jamModel.setjStatus("true");
                                            currentJams.add(jamModel);
                                        }
                                        RVCardsdapter = new RVCardViewAdapter(getContext(), currentJams);
                                        RVCardsdapter.setClickListener(CurrentJamsFragment.this);
                                        rv.setAdapter(RVCardsdapter) ;
                                    }else{
                                            empty = (TextView) getActivity().findViewById(R.id.noJams);
                                            empty.setVisibility(View.VISIBLE);
                                        }
                                    }

                                smoothProgressBar.progressiveStop();
                                smoothProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                }
                        });
                }
            }
        });

    }

    @Override
    public void OnItemClick(View view, int pos) {
        Bundle bundle = new Bundle();
        JamModel currentJam = currentJams.get(pos);
        Intent intent = new Intent(getActivity(), ViewJamDetailsActivity.class);
        bundle.putParcelable("currentJamModel", currentJam);
        intent.putExtras(bundle);
        intent.putExtra("jId", currentJam.getjId());
        startActivity(intent);
    }
}
