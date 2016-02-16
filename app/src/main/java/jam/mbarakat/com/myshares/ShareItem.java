package jam.mbarakat.com.myshares;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MBARAKAT on 2/4/2016.
 */
public class ShareItem implements Parcelable {
    private String shareOwnerName;
    private String shareOwnerPhone;
    private int shareAmount;
    private String shareId;

    protected ShareItem(Parcel in) {
        shareOwnerName = in.readString();
        shareOwnerPhone = in.readString();
        shareAmount = in.readInt();
        shareId = in.readString();
        shareNo = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shareOwnerName);
        dest.writeString(shareOwnerPhone);
        dest.writeInt(shareAmount);
        dest.writeString(shareId);
        dest.writeInt(shareNo);
    }

    public static final Creator<ShareItem> CREATOR = new Creator<ShareItem>() {
        @Override
        public ShareItem createFromParcel(Parcel in) {
            return new ShareItem(in);
        }

        @Override
        public ShareItem[] newArray(int size) {
            return new ShareItem[size];
        }
    };

    public int getShareNo() {
        return shareNo;
    }

    public void setShareNo(int shareNo) {
        this.shareNo = shareNo;
    }

    private int shareNo;

    public ShareItem(String shareOwnerName,String phone, int shareAmount, String shareId, int shareNo) {
        setShareAmount(shareAmount);
        setShareId(shareId);
        setShareOwnerName(shareOwnerName);
        setShareNo(shareNo);
        setShareOwnerPhone(phone);
    }
    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }
    public String getShareOwnerName() {
        return shareOwnerName;
    }

    public void setShareOwnerName(String shareOwnerName) {
        this.shareOwnerName = shareOwnerName;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(int shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getShareOwnerPhone() {
        return shareOwnerPhone;
    }

    public void setShareOwnerPhone(String shareOwnerPhone) {
        this.shareOwnerPhone = shareOwnerPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
