package tw.com.example.ben.sharehouse.Map;


import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String Lat;
    public String Lon;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email,String Lat,String Lon) {

        this.email = email;
        this.Lat = Lat;
        this.Lon = Lon;
    }

    public User(String Lat,String Lon) {

        this.Lat = Lat;
        this.Lon = Lon;
    }


}