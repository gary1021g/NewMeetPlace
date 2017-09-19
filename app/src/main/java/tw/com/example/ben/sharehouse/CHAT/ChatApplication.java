package tw.com.example.ben.sharehouse.CHAT;

import com.firebase.client.Firebase;

/**
 * @author Jenny Tong (mimming)
 * @since 12/5/14
 *
 * Initialize Firebase with the application context. This must happen before the client is used.
 */
public class ChatApplication extends android.app.Application {

    private String KEY_TWO;             // delete house
    private String KEY_THREE;
    private String Change_House_Name;
    private int Flag_D;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }


    public void setFlag_D(int flag){
        this.Flag_D=flag;
    }
    public int getFlag_D(){ return this.Flag_D; }

    public void setKEY_TWO(String KEY) { this.KEY_TWO = KEY; }
    public String getKEY_TWO(){ return this.KEY_TWO; }

    public void setKEY_THREE(String KEY) { this.KEY_THREE = KEY; }
    public String getKEY_THREE(){ return this.KEY_THREE ; }

    public void setChange_House_Name(String name) {this.Change_House_Name = name;}
    public String getChange_House_Name(){ return this.Change_House_Name;}
}
