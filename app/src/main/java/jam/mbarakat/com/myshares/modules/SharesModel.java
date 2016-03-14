package jam.mbarakat.com.myshares.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jam.mbarakat.com.myshares.helpers.HelperClass;

/**
 * Created by MBARAKAT on 2/2/2016.
 */
public class SharesModel implements Parcelable {
    String startDay, shareOwnerId, shareId, jamId;
    int shareOrder;
    int jAmount;
    int sharePaidAmount;

    public int getSharePaidAmount() {
        return sharePaidAmount;
    }

    public void setSharePaidAmount(int sharePaidAmount) {
        this.sharePaidAmount = sharePaidAmount;
    }

    boolean isShareDelivered;
    List<SharePayersModel> sharePayersModels;
    List<ShareItem> shareItems = Collections.emptyList();

    public String getShareOwnerId() {
        return shareOwnerId;
    }

    public void setShareOwnerId(String shareOwnerId) {
        this.shareOwnerId = shareOwnerId;
    }


    public String getShareId() {
        return shareId;
    }
    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
    public List<SharePayersModel> getSharePayersModels() {
        return sharePayersModels;
    }
    public void setSharePayersModels(List<SharePayersModel> sharePayersModels) {
        this.sharePayersModels = sharePayersModels;
    }
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
        return isShareDelivered;
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

    public SharesModel(ParseObject parseObject, Context context){
        startDay =  HelperClass.getStringFormatDate(parseObject.getDate("share_due_date"), context);
        shareOrder = parseObject.getInt("share_order");
        isShareDelivered = parseObject.getBoolean("share_status");
        jamId = parseObject.getString("shares_jamNo");
        shareOwnerId = parseObject.getString("share_owner_id");
        shareId = parseObject.getObjectId();
        sharePaidAmount = parseObject.getInt("share_paid_amount");
    }

    public SharesModel(){
        this.startDay = "Start Day";
    }

    public SharesModel(String date){
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
        isShareDelivered = in.readByte() != 0;
        this.sharePayersModels = new ArrayList<>();
        in.readTypedList(this.sharePayersModels, SharePayersModel.CREATOR);
        shareId = in.readString();
        shareOwnerId = in.readString();
        sharePaidAmount = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startDay);
        dest.writeInt(shareOrder);
        dest.writeString(jamId);
        dest.writeTypedList(shareItems);
        dest.writeInt(jAmount);
        dest.writeByte((byte) (isShareDelivered ? 1 : 0));
        dest.writeTypedList(this.sharePayersModels);
        dest.writeString(shareId);
        dest.writeString(shareOwnerId);
        dest.writeInt(sharePaidAmount);
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


    @Override
    public int describeContents() {
        return 0;
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

    public boolean isSharePaid(int sharesCount){
        if(getSharePaidAmount() == (sharesCount * getjAmount()))
            return true;
        return false;
    }
}
