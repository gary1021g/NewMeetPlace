package tw.com.example.ben.sharehouse.CHAT;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import tw.com.example.ben.sharehouse.CHAT.dataModel.Chat;
import tw.com.example.ben.sharehouse.CHAT.dataModel.MyUser;
import tw.com.example.ben.sharehouse.Map.MapsActivity;
import tw.com.example.ben.sharehouse.R;
import tw.com.example.ben.sharehouse.lib.TinyDB;
import tw.com.example.ben.sharehouse.ui.IconSmallerOnTouchListener;

public class chat_main_layout_controler extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2 ;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000 ;
    private String FIREBASE_URL;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private FirebaseListAdapter mChatListAdapter;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReferenece;
    private ImageView send ,pic ,map;
    private EditText mMessageEditText;
    ListView listView;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main_layout);



        mFirebaseStorage = FirebaseStorage.getInstance();

        mChatPhotosStorageReferenece = mFirebaseStorage.getReference().child("chat_photos");

        send = (ImageView) findViewById(R.id.sendButton);
        pic = (ImageView) findViewById( R.id.pic);
        map = (ImageView) findViewById( R.id.mapB);
        mMessageEditText = (EditText) findViewById( R.id.messageInput) ;

        Intent it = getIntent();
        FIREBASE_URL=it.getStringExtra("網址");
        setTitle(it.getStringExtra("名稱"));
        listView = (ListView)findViewById(R.id.chatList);
        //用亂數產生本位使用者亂碼
        setupUsername();
        //設定標題

        //設定FIREBAS網址
        mFirebaseRef = new Firebase(FIREBASE_URL);
        //按下虛擬鍵盤的ENTER也可送出資料 不過現在似乎有BUG無法送出
        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    send.setEnabled(true);
                } else {
                    send.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        //按下發射建 產生的動作 送出訊息


        pic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        pic.setOnTouchListener(new IconSmallerOnTouchListener());

        map.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mapM = new Intent();
                mapM.putExtra("roomkey",key);
                mapM.putExtra("URL",FIREBASE_URL);
                mapM.setClass(view.getContext(), MapsActivity.class);
                startActivity(mapM);
            }
        });
        map.setOnTouchListener(new IconSmallerOnTouchListener());


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                String time;
                if ( minute < 10 ){
                    time = hour +":0"+minute;
                }
                else{
                    time = hour +":"+minute;
                }
                //sendMessage();
                // TODO: Send messages on click
                Chat friendlyMessage = new Chat(mMessageEditText.getText().toString(), mUsername, null,time);
                // Clear input box
                mFirebaseRef.push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
        send.setOnTouchListener(new IconSmallerOnTouchListener());

        mChatListAdapter = new FirebaseListAdapter(mFirebaseRef.limit(50), this, mUsername,R.layout.chat_user1_item);
        //綁定adapter
        listView.setAdapter(mChatListAdapter);
        //如果資料有變 焦點到listview最下方

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //移到listview最下方
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        mFirebaseRef.addListenerForSingleValueEvent(postListener);


    }
    private void setupUsername() {
        TinyDB tinydb;
        tinydb = new TinyDB(this);
        MyUser myUser = (MyUser) tinydb.getObject("MyUser",MyUser.class);
        mUsername=myUser.getAccount();
    }
    /*private void sendMessage() {
        //取得出入欄資料
        EditText inputText = (EditText)findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        //空字串檢查
        if (!input.equals(""))
        {
            // 先將資料存入chat 類別
            Chat chat = new Chat(input, mUsername,"123");
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK ){
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mChatPhotosStorageReferenece.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    String time;
                    if ( minute < 10 ){
                        time = hour +":0"+minute;
                    }
                    else{
                        time = hour +":"+minute;
                    }
                    Chat friendlyMessage = new Chat(null, mUsername , downloadUrl.toString(),time);
                    mFirebaseRef.push().setValue(friendlyMessage);
                }
            });
        }
    }
}
