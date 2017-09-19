package tw.com.example.ben.sharehouse.Map;

/**
 * Created by Nightingale on 2017/9/18.
 */

public class User_MapSettings {
    private String islocreq ;
    private String useremail;

    public User_MapSettings(){

    }
    public User_MapSettings(String useremail,String islocreq){
        this.islocreq = islocreq;
        this.useremail=useremail;
    }
    public String getIslocreq() {return islocreq;}
    public String getUseremail(){return  useremail;}



}
