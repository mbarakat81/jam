package jam.mbarakat.com.myshares;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MBARAKAT on 1/31/2016.
 */
public class JamModel implements Parcelable {

    String jId;
    String jName;
    String jOwnerId;
    String jOwnerName;
    String jAmount;
    String jDate;
    String jIsPublic;
    String obs;
    String jStatus;
    String jPeriod;
    List<SharesModel> sharesModel = new ArrayList<>();
    String sharesNo;
    ArrayList<Participants> jParticipants = null;

    public JamModel() {

    }

    public JamModel(String name, String ownerId, String amount, String period, String date, String sharesNo, String isPublic) {
        setjName(name);
        setjOwnerId(ownerId);
        setjAmount(amount);
        setjStatus("true");
        setjPeriod(period);
        setjDate(date);
        setSharesNo(sharesNo);
        setjIsPublic(isPublic);
        setObs("false");
    }


    public String getjOwnerName() {
        return jOwnerName;
    }

    public void setjOwnerName(String jOwnerName) {
        this.jOwnerName = jOwnerName;
    }

    public String getjId() {
        return jId;
    }

    public void setjId(String jId) {
        this.jId = jId;
    }

    public List<SharesModel> getSharesModel() {
        return sharesModel;
    }

    public void setSharesModel(List<SharesModel> sharesModel) {
        this.sharesModel = sharesModel;
    }

    public String getjStatus() {
        return jStatus;
    }

    public String getjIsPublic() {
        return jIsPublic;
    }

    public String getjName() {
        return jName;
    }

    public void setjName(String jName) {
        this.jName = jName;
    }

    public String getjOwnerId() {
        return jOwnerId;
    }

    public void setjOwnerId(String jOwnerId) {
        this.jOwnerId = jOwnerId;
    }

    public String getjAmount() {
        return jAmount;
    }

    public void setjAmount(String jAmount) {
        this.jAmount = jAmount;
    }

    public String getjDate() {
        return jDate;
    }

    public void setjDate(String jDate) {
        this.jDate = jDate;
    }

    public String isjIsPublic() {
        return jIsPublic;
    }

    public void setjIsPublic(String jIsPublic) {
        this.jIsPublic = jIsPublic;
    }

    public String isObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String isjStatus() {
        return jStatus;
    }

    public void setjStatus(String jStatus) {
        this.jStatus = jStatus;
    }

    public String getjPeriod() {
        return jPeriod;
    }

    public void setjPeriod(String jPeriod) {
        this.jPeriod = jPeriod;
    }

    public ArrayList<Participants> getjParticipants() {
        return jParticipants;
    }

    public void setjParticipants(ArrayList<Participants> jParticipants) {
        this.jParticipants = jParticipants;
    }

    protected JamModel(Parcel in) {
        this.jName = in.readString();
        this.jOwnerId = in.readString();
        this.jAmount = in.readString();
        this.jDate = in.readString();
        this.jIsPublic = in.readString();
        this.obs = in.readString();
        this.jStatus = in.readString();
        this.jPeriod = in.readString();
        this.sharesNo = in.readString();
        this.jId = in.readString();
        this.sharesModel = new ArrayList<>();
        in.readTypedList(this.sharesModel, SharesModel.CREATOR);
        this.jOwnerName = in.readString();
       // this.sharesModel = in.r
        //ArrayList<Participants> jParticipants = null;

    }

    public static final Creator<JamModel> CREATOR = new Creator<JamModel>() {
        @Override
        public JamModel createFromParcel(Parcel in) {
            return new JamModel(in);
        }

        @Override
        public JamModel[] newArray(int size) {
            return new JamModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(this.jName);
        pc.writeString(this.jOwnerId);
        pc.writeString(this.jAmount);
        pc.writeString(this.jDate);
        pc.writeString(this.jIsPublic);
        pc.writeString(this.obs);
        pc.writeString(this.jStatus);
        pc.writeString(this.jPeriod);
        pc.writeString(this.sharesNo);
        pc.writeString(this.jId);
        pc.writeTypedList(this.sharesModel);
        pc.writeString(this.jOwnerName);

    }


    public void setSharesNo(String sharesNo) {
        this.sharesNo = sharesNo;
    }

    public String getSharesNo() {
        return sharesNo;
    }

    public String getObs() {
        return obs;
    }

    public String getIsPublic() {
        return jIsPublic;
    }

    public String getJStatus() {
        return jStatus;
    }

/** helpers methods**/
    public boolean isMysJam(String currentUserId){
        return getjOwnerId().equals(currentUserId.toString());
    }

    /**public boolean isDelivered(){
        return false;//getjDate()
    }
    public boolean isCurrentJam(){
        Date date = HelperClass.getDate(getjDate());
        Date currentDate = new Date();
        if(date.get)

    }**/
    public String getNextJamOwner(){

        for(SharesModel sharesModel:getSharesModel()){

        }
        return "next Jam Owner";
    }

    public String getNextJamDate() {
        String shareDate = "not set yet";
        List<SharesModel> sharesModelList = getSharesModel();
        for(int i =0; i< sharesModelList.size(); i++){
            if(sharesModelList.get(i).isShareDelivered()){
                shareDate = sharesModelList.get(i).getStartDay();
                return shareDate;
            }
        }
        return shareDate;
    }
}
