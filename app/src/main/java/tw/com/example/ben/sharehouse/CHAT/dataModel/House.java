package tw.com.example.ben.sharehouse.CHAT.dataModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ben on 16/8/8.
 */
public class House {

    private String message;
    private String name;
    private String thisUrl;
    private String roomTable;
    private String url;
    private String chat;
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public House() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference Ref= db.getReference("roomTables").push();
        this.roomTable = Ref.toString();
        this.name="房間 長按能更改名稱";
        DatabaseReference Ref2= db.getReference("houses").push();
        this.url=Ref2.toString();
        DatabaseReference Ref3= db.getReference("chat").push();
        this.chat=Ref3.toString();
    }
    public House(String url) {
        this.url = url;
    }
    public House(String name,String nothing ) {
        this.name=name;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference Ref= db.getReference("roomTables").push();
        this.roomTable = Ref.toString();
        this.name="123";
        DatabaseReference Ref2= db.getReference("houses").push();
        this.url=Ref2.toString();
    }
    public House(String name, String message,String thisUrl) {
        this.message = message;
        this.name = name;
        this.thisUrl=thisUrl;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference Ref= db.getReference("roomTables").push();
        this.roomTable = Ref.toString();
    }

    public String getMessage() {
        return message;
    }
    public String getName(){return name;}
    public String getThisUrl(){return thisUrl;}
    public String getRoomTable() {return roomTable;}
    public String getUrl(){return url;}
    public String getChat(){return chat;}
    public void setName(String name){ this.name=name;}
}
