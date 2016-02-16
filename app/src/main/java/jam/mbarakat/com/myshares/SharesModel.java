package jam.mbarakat.com.myshares;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by MBARAKAT on 2/2/2016.
 */
public class SharesModel implements Parcelable {
    String startDay;
    int shareOrder, jAmount;
    boolean isShareDelivered;
    String jamId;
    List<ShareRow> shareRows = Collections.emptyList();
    List<ShareItem> shareItems = Collections.emptyList();

    public int getAddedAmount() {
        int addedAmount = 0;
        for(int i=0;i<getShareItems().size();i++){
            addedAmount += getShareItems().get(i).getShareAmount();
        }
        return addedAmount;
    }

    public int getjAmount() {
        return jAmount;
    }

    public void setjAmount(int jAmount) {
        this.jAmount = jAmount;
    }

    public List<ShareItem> getShareItems() {
        return shareItems;
    }

    public void setShareItems(List<ShareItem> shareItems) {
        this.shareItems = shareItems;
    }

    public int getShareOrder() {
        return shareOrder;
    }

    public void setShareOrder(int shareOrder) {
        this.shareOrder = shareOrder;
    }

    public boolean isShareDelivered() {
        return !isShareDelivered;
    }

    public void setShareDelivered(boolean shareDelivered) {
        this.isShareDelivered = shareDelivered;
    }

    public String getJamId() {
        return jamId;
    }

    public void setJamId(String jamId) {
        this.jamId = jamId;
    }

    SharesModel(ParseObject parseObject, String date){
        startDay = setDate(date);
        shareOrder = parseObject.getInt("share_order");
        isShareDelivered = parseObject.getBoolean("share_status");
        jamId = parseObject.getString("shares_jamNo");
    }

    SharesModel(){
        this.startDay = "Start Day";
    }

    SharesModel(String date){
        this.startDay = date;
    }

    private String setDate(String date)  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] arr = date.split("/");
        int day,year,month;
        day = Integer.parseInt(arr[0]);
        month = Integer.parseInt(arr[1]);
        year = Integer.parseInt(arr[2]);
        Calendar c = Calendar.getInstance();
        c.set(year,month-1,day);

        return sdf.format(c.getTime());
    }

    protected SharesModel(Parcel in) {
        startDay = in.readString();
        shareOrder = in.readInt();
        jamId = in.readString();
        shareItems = new ArrayList<>();
        in.readTypedList(this.shareItems, ShareItem.CREATOR);
        jAmount = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(startDay);
        dest.writeInt(shareOrder);
        dest.writeString(jamId);
        dest.writeTypedList(shareItems);
        dest.writeInt(jAmount);
    }

    public static final Creator<SharesModel> CREATOR = new Creator<SharesModel>() {
        @Override
        public SharesModel createFromParcel(Parcel in) {
            return new SharesModel(in);
        }

        @Override
        public SharesModel[] newArray(int size) {
            return new SharesModel[size];
        }
    };

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public List<ShareRow> getShareRows() {
        return shareRows;
    }

    public void setShareRows(List<ShareRow> shareRows) {
        this.shareRows = shareRows;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    public class ShareRow {
        String shareOwner;
        String value;

        public String getShareOwner() {
            return shareOwner;
        }

        public void setShareOwner(String shareOwner) {
            this.shareOwner = shareOwner;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /** Helpers Methods **/

    public boolean isCurrentShare(){
        String period = "";
        if(period == HelperClass.Period.WEEKLY.toString()){

        }else if(period == HelperClass.Period.MONTHLY.toString()){

        }
        Date currentDate = HelperClass.getCurrentDate();
        Date nextMonthDate = HelperClass.getCurrentDatePlusMonth(1);
        Date shareDate = HelperClass.getDate(getStartDay());

       // if(getStartDay())
        return false;
    }
}
