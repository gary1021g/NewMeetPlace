package tw.com.example.ben.sharehouse.CHAT.dataModel;

/**
 * Created by Ben on 16/8/16.
 */
public class Room {
    private String message;
    private String author;
    private String number;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Room() {
    }

    public Room(String message, String author, String number) {
        this.message = message;
        this.author = author;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }
    public String getNumber() {
        return number;
    }

    public String getAuthor() {
        return author;
    }
}
