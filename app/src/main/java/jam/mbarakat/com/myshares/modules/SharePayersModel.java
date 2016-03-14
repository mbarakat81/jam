package jam.mbarakat.com.myshares.modules;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MBARAKAT on 2/26/2016.
 */
public class SharePayersModel implements Parcelable {
    private String payerName;
    private String id;
    private String shareId;
    private String payerId;
    private String userId;
    private String userName;
    private boolean isPaid;
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUser_id() {
        return userId;
    }

    public String getUser_name() {
        return userName;
    }

    public String getPayerId() {
        return payerId;
    }
    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPayerName() {
        if(isPayerVerified())
            return getUser_name();
        return payerName;
    }
    public boolean isPayerVerified(){
        return (getUser_name() != null && !getUser_name().isEmpty());
    }
    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    protected SharePayersModel(Parcel in) {
        payerName = in.readString();
        shareId = in.readString();
        isPaid = in.readByte() != 0;
        id = in.readString();
        payerId = in.readString();
        userId = in.readString();
        userName = in.readString();
        amount = in.readInt();
    }
    public SharePayersModel(String payerName, String shareId, boolean isPaid, String id, String payerId, String userId, String userName, int amount){
        this.payerName = payerName;
        this.shareId = shareId;
        this.isPaid = isPaid;
        this.id = id;
        this.payerId = payerId;
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }
    public static final Creator<SharePayersModel> CREATOR = new Creator<SharePayersModel>() {
        @Override
        public SharePayersModel createFromParcel(Parcel in) {
            return new SharePayersModel(in);
        }

        @Override
        public SharePayersModel[] newArray(int size) {
            return new SharePayersModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(payerName);
        dest.writeString(shareId);
        dest.writeByte((byte) (isPaid ? 1 : 0));
        dest.writeString(id);
        dest.writeString(payerId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeInt(amount);
    }
}
