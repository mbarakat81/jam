package jam.mbarakat.com.myshares.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
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
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
import jam.mbarakat.com.myshares.helpers.HelperClass;
import jam.mbarakat.com.myshares.modules.JamModel;
import jam.mbarakat.com.myshares.helpers.ParseConstants;
import jam.mbarakat.com.myshares.R;
import jam.mbarakat.com.myshares.activities.ViewJamDetailsActivity;
import jam.mbarakat.com.myshares.adapters.MyJamsViewAdapter;
import jam.mbarakat.com.myshares.modules.SharesModel;

/**
 * Created by MBARAKAT on 1/30/2016.
 */
public class CurrentJamsFragment extends ListFragment implements MyJamsViewAdapter.ClickListener{
    private SmoothProgressBar smoothProgressBar;
    TextView empty;
    protected ParseUser mCurrentUser;
    protected List<ParseObject> mJams;
    List<JamModel> currentJams = Collections.emptyList();

    RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MyJamsViewAdapter myJamsViewAdapter;

    boolean deleted = false;
    ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myJamsViewAdapter = new MyJamsViewAdapter(getContext(), currentJams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_jams, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.rvCardView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(myJamsViewAdapter);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/sheba.ttf");
        empty = (TextView) rootView.findViewById(R.id.noJams);
        empty.setTypeface(tf);

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
        displayMyActiveCurrentJamsAsCardView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    private void displayMyActiveCurrentJamsAsCardView(){
        smoothProgressBar.setVisibility(ProgressBar.VISIBLE);
        smoothProgressBar.progressiveStart();
        mCurrentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Share_owners");
        q.whereEqualTo("share_owner_id", mCurrentUser.getObjectId())
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
                                    myJamsViewAdapter = new MyJamsViewAdapter(getContext(), currentJams);
                                    myJamsViewAdapter.setClickListener(CurrentJamsFragment.this);
                                    recyclerView.setAdapter(myJamsViewAdapter);

                                    empty.setVisibility(View.GONE);
                                } else {
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
    String dialogMsgBody="";
    @Override
    public void OnItemClick(View view, int pos) {
        if(view.getContentDescription()!=null) {
            String action = view.getContentDescription().toString();

            if (action.equals("delete_jam")) {
                dialogMsgBody= getResources().getString(R.string.progress_msg_delete);
                onDeleteJamClick(view, pos);
            } else if (action.equals("share_jam")) {
                dialogMsgBody= getResources().getString(R.string.progress_msg_share_jam);
                onShareJamClick(view, pos);
            } else if (action.equals("view_jam")) {
                dialogMsgBody= getResources().getString(R.string.progress_msg_show_jam);
                onViewJamClick(view, pos);
            }

        }
    }
    private void onDeleteJamClick(View view, int pos){
        showDialog(pos);
    }
    private void onShareJamClick(View view, int pos){
        shareIt(currentJams.get(pos).getjId(), currentJams.get(pos).getNextJamOwner(), currentJams.get(pos).getjAmount());
    }
    private void onViewJamClick(View view, int pos){
        ParseQuery<ParseObject> jShares = ParseQuery.getQuery("jShares");
        final JamModel jamItem = currentJams.get(pos);
        jShares.whereEqualTo("shares_jamNo",jamItem.getjId())
                .orderByAscending("share_order");
        jShares.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ArrayList<SharesModel> jamSharesObject = new ArrayList<SharesModel>();
                    for (ParseObject share : list) {
                        SharesModel shareItem = new SharesModel();
                        shareItem.setJamId(share.getString("shares_jamNo"));
                        shareItem.setShareOrder(share.getInt("share_order"));
                        shareItem.setShareDelivered(share.getBoolean("share_status"));
                        shareItem.setjAmount(Integer.parseInt(jamItem.getjAmount()));
                        shareItem.setStartDay(share.getDate("share_due_date").toLocaleString().toString());
                        shareItem.setShareId(share.getObjectId());
                        shareItem.setSharePaidAmount(share.getInt("share_paid_amount"));
                        jamSharesObject.add(shareItem);
                    }
                    jamItem.setSharesModel(jamSharesObject);
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(getContext(), ViewJamDetailsActivity.class);
                    bundle.putParcelable("currentJamModel", jamItem);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });


    }

    private void shareIt(String jamId, String jamCreator, String jamShareValue) {
                startProgressDialog(getResources().getString(R.string.progress_title), dialogMsgBody);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.share_jam_body_msg).replace("_AMOUNT", jamShareValue)
                        .replace("_LINK",getResources().getString(R.string.jam_invite_link));
                onShareClick(shareBody, getResources().getString(R.string.str_jam_invite));
                endProgressDialod();
            }
    private void showDialog(final int id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.delete_jam_title);
                alert.setMessage(R.string.delete_jam_msg);
                alert.setNegativeButton(R.string.delete_jam_cancel_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                );
                alert.setPositiveButton(R.string.delete_jam_confirm_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startProgressDialog(getResources().getString(R.string.progress_title), dialogMsgBody);
                        ParseQuery<ParseObject> query = new ParseQuery<>("Jam_header");
                        query.whereEqualTo("objectId", currentJams.get(id).getjId());
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    deleteJam(parseObject, id);
                                    endProgressDialod();
                                }
                            }
                        });

                    }
                });
                alert.show();
    }
    private void deleteJam(ParseObject parseObject, final int id) {
                parseObject.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseQuery<ParseObject> queryShares = new ParseQuery<>("jShares");
                            ParseQuery<ParseObject> querySubShares = new ParseQuery<>("Share_owners");
                            queryShares.whereEqualTo("shares_jamNo", currentJams.get(id).getjId());
                            querySubShares.whereEqualTo("jamId", currentJams.get(id).getjId());
                            try {
                                ParseObject.deleteAll(queryShares.find());
                                ParseObject.deleteAll(querySubShares.find());
                                Toast.makeText(getActivity(), "done", Toast.LENGTH_LONG).show();
                                deleted = true;
                                currentJams.remove(id);
                                myJamsViewAdapter.notifyItemRemoved(id);

                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
    private void startProgressDialog(String title, String msg) {
                progress = ProgressDialog.show(getContext(), "",
                        "", true);

    }
    private void endProgressDialod() {
                progress.dismiss();
            }

    public void onShareClick(String text, String subject) {
        Resources resources = getContext().getResources();
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = getContext().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName.toLowerCase();
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("viber") || packageName.contains("whatsapp") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if(packageName.contains("viber")) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                } else if(packageName.contains("whatsapp")) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                } else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                } else if(packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        getContext().startActivity(openInChooser);
    }
}