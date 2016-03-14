package jam.mbarakat.com.myshares.helpers;

import com.parse.ParseUser;

public class SessionUser {
    private String userName;
    private String userId;
    private String userPhone;
    private String userEmail;

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }


    public String getUserPhone() {
        return userPhone;
    }


    public String getUserEmail() {
        return userEmail;
    }


    private static SessionUser user = null;

    protected SessionUser() {
        ParseUser parseUser = ParseUser.getCurrentUser();

        this.userName = parseUser.getUsername();
        this.userId = parseUser.getObjectId();
        this.userEmail = parseUser.getEmail();
        this.userPhone = parseUser.getString("phone");
    }

    public static SessionUser getUser() {
        if(user == null) {
            user = new SessionUser();
        }
        return user;
    }
}
