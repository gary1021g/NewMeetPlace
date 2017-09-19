package tw.com.example.ben.sharehouse.CHAT;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

import tw.com.example.ben.sharehouse.CHAT.dataModel.House;
import tw.com.example.ben.sharehouse.CHAT.dataModel.MyUser;
import tw.com.example.ben.sharehouse.Map.MapsActivity;
import tw.com.example.ben.sharehouse.R;
import tw.com.example.ben.sharehouse.edit_house;
import tw.com.example.ben.sharehouse.lib.TinyDB;

/**
 * Created by Ben on 16/8/8.
 */
public class house_list_controler extends Fragment {

    private static String FIREBASE_URL;
    private Firebase mFirebaseRef;

    ChatApplication GV;
    TinyDB tinydb;
    String uid;

    house_list_adapter chatContactAdapter;
    FloatingActionButton fab;
    ListView listView;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        GV = ((ChatApplication) getActivity().getApplicationContext());
        //將XML轉換成VIEW
        View view =inflater.inflate(R.layout.chat_house_list_layout,container,false);
        listView = (ListView) view.findViewById(R.id.contactList);
        //短按進入聊天室中
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                TextView chat = (TextView) view.findViewById(R.id.chat);
                final String Tochat = chat.getText().toString();//聊天室url
                Firebase chatroomfirebaseurl = new Firebase(Tochat);
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        Intent mapM = new Intent();
                        mapM.putExtra("roomkey",key);
                        mapM.putExtra("URL",Tochat);
                        mapM.setClass(view.getContext(), MapsActivity.class);
                        startActivity(mapM);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                };
                chatroomfirebaseurl.addListenerForSingleValueEvent(postListener);
                /*Log.i("uri",Tochat);
                String name ="聊天室";
                goChat.putExtra("網址",Tochat);
                goChat.putExtra("名稱",name);*/

            }
        });
        //長按進入選單 可以編輯資料
       listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
               final String[] dinner = {"編輯","刪除","結束"};
               AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
               //彈出視窗標題
               TextView nameView= (TextView) view.findViewById(R.id.name);
               String name = nameView.getText().toString();
               //TextView thisUrlView= (TextView) view.findViewById(R.id.thisUrl);
               //final String thisUrl = thisUrlView.getText().toString();
               dialog_list.setTitle(name);
               dialog_list.setItems(dinner, new DialogInterface.OnClickListener(){
                   @Override
                   //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                   public void onClick(DialogInterface dialog, int which) {
                       if(which == 1)
                       {
                           //刪除聊天記錄
                           TextView chat = (TextView) view.findViewById(R.id.chat);
                           String Key = chat.getText().toString();
                           new Firebase(Key).removeValue();
                           Log.e("ChatDelete", "ChatDelete"+Key);
                           FirebaseDatabase db = FirebaseDatabase.getInstance();
                           final DatabaseReference Ref= db.getReference();
                           Query deleteQuery = Ref.child("houses").orderByChild("chat").equalTo(Key);
                           deleteQuery.addChildEventListener(new ChildEventListener() {
                               @Override
                               public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                   if (dataSnapshot.exists()){
                                       String Key2 = dataSnapshot.getRef().getKey().toString();
                                       Key2 = "https://sharehousetest.firebaseio.com/houses/"+Key2;
                                       GV.setKEY_TWO(Key2);
                                       MyUser myUser= (MyUser) tinydb.getObject("MyUser", MyUser.class);
                                       String UserKey = myUser.getNickname();


                                       Query delete2Query = Ref.child("userHouseTables").child(UserKey).orderByChild("url").equalTo(Key2);
                                       Log.e("ChatDelete2", "ChatDelete2"+Key2);
                                       delete2Query.addChildEventListener(new ChildEventListener() {
                                           @Override
                                           public void onChildAdded(DataSnapshot dataSnapshot2, String s2) {
                                               if (dataSnapshot2.exists()){
                                                   String Key3 = dataSnapshot2.getRef().getKey().toString();
                                                   Log.e("ChatDelete3", "ChatDelete3"+Key3);
                                                   Query delete3Query = Ref.child("userHouseTables").orderByChild("url").limitToLast(1).equalTo(GV.getKEY_TWO());
                                                   delete3Query.addChildEventListener(new ChildEventListener() {
                                                       @Override
                                                       public void onChildAdded(DataSnapshot dataSnapshot3, String s) {
                                                           if ( dataSnapshot3.exists() ){
                                                               //dataSnapshot3.getRef().setValue(null);
                                                           }
                                                       }

                                                       @Override
                                                       public void onChildChanged(DataSnapshot dataSnapshot, String s) {                     }

                                                       @Override
                                                       public void onChildRemoved(DataSnapshot dataSnapshot) {       }

                                                       @Override
                                                       public void onChildMoved(DataSnapshot dataSnapshot, String s) {       }

                                                       @Override
                                                       public void onCancelled(DatabaseError databaseError) {   }
                                                   });

                                                   dataSnapshot2.getRef().setValue(null);
                                                   //new Firebase(GV.getKEY_TWO()).removeValue();
                                               }
                                           }
                                           @Override
                                           public void onChildChanged(DataSnapshot dataSnapshot2, String s2) {}
                                           @Override
                                           public void onChildRemoved(DataSnapshot dataSnapshot2) {}
                                           @Override
                                           public void onChildMoved(DataSnapshot dataSnapshot2, String s2) {}
                                           @Override
                                           public void onCancelled(DatabaseError databaseError2) {}
                                       });
                                   }
                               }
                               @Override
                               public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                               @Override
                               public void onChildRemoved(DataSnapshot dataSnapshot) {}
                               @Override
                               public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                               @Override
                               public void onCancelled(DatabaseError databaseError) {}
                           });


                       }
                       else if(which == 0)
                       {
                           TextView chat = (TextView) view.findViewById(R.id.chat);
                           String Key = chat.getText().toString();
                           FirebaseDatabase db = FirebaseDatabase.getInstance();
                           final DatabaseReference Ref= db.getReference();
                           Query deleteQuery = Ref.child("houses").orderByChild("chat").equalTo(Key);
                           deleteQuery.addChildEventListener(new ChildEventListener() {
                               @Override
                               public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                   if (dataSnapshot.exists()){
                                       GV.setChange_House_Name(dataSnapshot.getRef().toString());
                                   }
                               }

                               @Override
                               public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                               }

                               @Override
                               public void onChildRemoved(DataSnapshot dataSnapshot) {

                               }

                               @Override
                               public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                           startActivity(new Intent(getActivity(),edit_house.class));
                       }
                       Toast.makeText(getActivity(), "你選的是" + dinner[which], Toast.LENGTH_SHORT).show();
                   }
               });
               dialog_list.show();
               //不再處理短按事件
               return true;
           }
       });
        //右下角浮動圖示 增加群組
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //創建一個新的房屋
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                final DatabaseReference Ref= db.getReference("houses").push();
                final View item = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_house, null);
                final House house = new House();
                Ref.setValue(house);

                final Intent search =new Intent(getActivity(), tw.com.example.ben.sharehouse.CHAT.search.class);
                startActivity(search);
                //將房屋加入房屋表
                Firebase ref= mFirebaseRef.push();
                House house2 =new House(Ref.toString());
                tinydb.putObject("newchatroom",house2);
                ref.setValue(house2);
            }
        });

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //取出使用者資料 取houseTable_url
        tinydb=new TinyDB(getActivity());
        MyUser myUser= (MyUser) tinydb.getObject("MyUser", MyUser.class);
        FIREBASE_URL=myUser.getHouseTable();
        uid=myUser.getNickname();
        //將資料位置轉換成Query
        mFirebaseRef = new Firebase(FIREBASE_URL);
        //設定標題
        getActivity().setTitle("聊天室");
    }
    @Override
    public void onStart() {
        super.onStart();
        //資料塞入listView
        chatContactAdapter = new house_list_adapter(getActivity(),mFirebaseRef);
        listView.setAdapter(chatContactAdapter);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
}
