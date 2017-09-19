package tw.com.example.ben.sharehouse.CHAT.dataModel;

/**
 * Created by Ben on 16/7/24.
 */
public class Chat {

    private String message;
    private String author;
    private String photoUrl;
    private String time;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String author, String photoUrl,String time) {
        this.message = message;
        this.author = author;
        this.photoUrl = photoUrl;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }
    public String getPhotoUrl(){ return photoUrl;}
    public String getAuthor() {
        return author;
    }
    public String getTime(){ return time;}
    public void setMessage(String message) { this.message = message;}
    public void setAuthor(String author) { this.author = author ;}
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl;}
    public void setTime(String time){ this.time = time;}
}
