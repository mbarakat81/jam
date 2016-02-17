package jam.mbarakat.com.myshares.modules;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MBARAKAT on 2/1/2016.
 */
public class Participants implements Parcelable {
    protected Participants(Parcel in) {
    }

    public static final Creator<Participants> CREATOR = new Creator<Participants>() {
        @Override
        public Participants createFromParcel(Parcel in) {
            return new Participants(in);
        }

        @Override
        public Participants[] newArray(int size) {
            return new Participants[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
