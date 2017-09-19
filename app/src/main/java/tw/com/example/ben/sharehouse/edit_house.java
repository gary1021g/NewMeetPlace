package tw.com.example.ben.sharehouse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tw.com.example.ben.sharehouse.CHAT.ChatApplication;
import tw.com.example.ben.sharehouse.CHAT.dataModel.House;

public class edit_house extends AppCompatActivity {
    House house;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_house);
        house =new House();
    }
    public void finish(View view)
    {
        ChatApplication GV = (ChatApplication) this.getApplicationContext();
        final EditText edt= (EditText) findViewById(R.id.name);
        final String housename = edt.getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        new Firebase(GV.getChange_House_Name()).child("name").setValue(housename);
    }
}
