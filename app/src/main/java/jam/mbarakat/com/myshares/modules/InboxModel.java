package jam.mbarakat.com.myshares.modules;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.util.Date;

import jam.mbarakat.com.myshares.helpers.ParseConstants;

/**
 * Created by MBARAKAT on 3/10/2016.
 */
public class InboxModel implements Parcelable {
    String msgBody;
    String senderName;
    String senderId;
    Date msgDate;

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(Date msgDate) {
        this.msgDate = msgDate;
    }

    public InboxModel(ParseObject inboxParseObject){
        setMsgBody(inboxParseObject.getString(ParseConstants.KEY_MSG_BODY));
        setSenderId(inboxParseObject.getString(ParseConstants.KEY_SENDER_ID));
        setSenderName(inboxParseObject.getString(ParseConstants.KEY_SENDER_NAME));
        setMsgDate(inboxParseObject.getCreatedAt());
    }

    public static final Creator<InboxModel> CREATOR = new Creator<InboxModel>() {
        @Override
        public InboxModel createFromParcel(Parcel in) {
            return new InboxModel(in);
        }

        @Override
        public InboxModel[] newArray(int size) {
            return new InboxModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected InboxModel(Parcel in) {
        this.senderId = in.readString();
        this.senderName = in.readString();
        this.msgBody = in.readString();
        this.msgDate = new Date(in.readLong());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.senderId);
        dest.writeString(this.senderName);
        dest.writeString(this.msgBody);
        dest.writeLong(this.msgDate.getTime());
    }
}
