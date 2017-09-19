package tw.com.example.ben.sharehouse.Map;

import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by Nightingale on 2017/7/29.
 */

public class UserMkr {

    public String Title;
    public String Lat;
    public String Lon;
    public String Email;

    public UserMkr() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserMkr(String Lat,String Lon,String Title,String Email) {
        this.Lat = Lat;
        this.Lon = Lon;
        this.Title = Title;
        this.Email = Email;
    }




}
