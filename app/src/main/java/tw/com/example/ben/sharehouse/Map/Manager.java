package tw.com.example.ben.sharehouse.Map;

/**
 * Created by Nightingale on 2017/9/13.
 */

public class Manager {

    public String manager_email;
    public String Camerapos_lat;
    public String Camerapos_lon;
    public String Camerapos_zoom;

    public Manager(){}
    public Manager(String s,String lat,String lon,String zoom){
        this.manager_email = s;
        this.Camerapos_lat = lat;
        this.Camerapos_lon = lon;
        this.Camerapos_zoom = zoom;
    }

}
