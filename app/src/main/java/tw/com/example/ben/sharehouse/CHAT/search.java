package tw.com.example.ben.sharehouse.CHAT;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import tw.com.example.ben.sharehouse.CHAT.dataModel.House;
import tw.com.example.ben.sharehouse.CHAT.dataModel.MyUser;
import tw.com.example.ben.sharehouse.R;
import tw.com.example.ben.sharehouse.lib.TinyDB;

public class search extends AppCompatActivity implements AdapterView.OnItemClickListener{
    TextView textView;
    ListView listView;
    private Firebase mFirebaseRef,connect;
    TinyDB tinyDB ;
    search_list_adapter adapter;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        tinyDB =new TinyDB(this);
        MyUser user= (MyUser) tinyDB.getObject("MyUser", MyUser.class);
        String table = user.getHouseTable();
        setTitle("搜尋帳號");
        listView = (ListView) findViewById(R.id.list);
        mFirebaseRef =new Firebase("https://sharehousetest.firebaseio.com/users");

        editText = (EditText) findViewById(R.id.text);
        listView.setOnItemClickListener(this);
        adapter= new search_list_adapter(this,mFirebaseRef);
        listView.setAdapter(adapter);

    }
    public void search(View view)
    {
        String searchWord =  editText.getText().toString();
        //Log.v("searchWord:",searchWord);
        adapter= new search_list_adapter(this, mFirebaseRef.orderByChild("account").equalTo(searchWord));
        listView.setAdapter(adapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        String searchWord =  editText.getText().toString();
        adapter= new search_list_adapter(this, mFirebaseRef.orderByChild("account").equalTo(searchWord));
        listView.setAdapter(adapter);

       // Log.v("onstart","%%%%");
    }
     public void add(View v)
     {
         String rr=  tinyDB.getString("friend");
         String friendtable=  tinyDB.getString("friendtable");
         connect= new Firebase(friendtable);
         House house= (House) tinyDB.getObject("newchatroom",House.class);
         connect.push().setValue(house);
         Log.v("444",friendtable);
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("444","444444444");
    }
}
