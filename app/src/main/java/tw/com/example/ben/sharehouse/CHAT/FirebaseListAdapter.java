package tw.com.example.ben.sharehouse.CHAT;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;

import tw.com.example.ben.sharehouse.CHAT.dataModel.Chat;
import tw.com.example.ben.sharehouse.R;

import static tw.com.example.ben.sharehouse.R.id.photoImageView;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item mLayout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the given Firebase location
 */
public class FirebaseListAdapter extends BaseAdapter {

    private String mUsername;
    private Query mRef;
    private Chat mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    private List<Chat> mModels;
    private List<String> mKeys;
    private ChildEventListener mListener;
    private Activity context;
    //建構子
    public FirebaseListAdapter(Query mRef,Activity activity,String mUsername,int mLayout) {
        this.mUsername= mUsername;
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        this.context=activity;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<Chat>();
        mKeys = new ArrayList<String>();
        //監聽網路資料變化並塞到ArratList中
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Chat model = dataSnapshot.getValue(Chat.class);
                String key = dataSnapshot.getKey();

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    mModels.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(model);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // One of the mModels changed. Replace it in our list and name mapping
                String key = dataSnapshot.getKey();
                Chat newModel = dataSnapshot.getValue(Chat.class);
                int index = mKeys.indexOf(key);

                mModels.set(index, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mModels.remove(index);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                Chat newModel = dataSnapshot.getValue(Chat.class);
                int index = mKeys.indexOf(key);
                mModels.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Chat model = mModels.get(i);
        String author = model.getAuthor();
        Flag flag;

        boolean isPhoto = model.getPhotoUrl() != null;

            if (author != null && author.equals(mUsername)) {
                if(view == null||((Flag) view.getTag()).flag!=2)
                {

                    view = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);
                    TextView messageTextView = (TextView) view.findViewById( R.id.message );
                    ImageView photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
                    if (isPhoto){
                        messageTextView.setVisibility(View.GONE);
                        photoImageView.setVisibility(View.VISIBLE);
                        Glide.with(photoImageView.getContext()).load(model.getPhotoUrl()).into(photoImageView);
                    }else{
                        messageTextView.setVisibility(View.VISIBLE);
                        photoImageView.setVisibility(View.GONE);
                        messageTextView.setText(model.getMessage());
                    }

                    flag= new Flag(2);
                    view.setTag(flag);
                }
                populateView(view, model);
            } else if (author != null && !author.equals(mUsername)) {

                if(view == null||((Flag) view.getTag()).flag!=1)
                {
                    view = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                    TextView messageTextView = (TextView) view.findViewById( R.id.message );
                    ImageView photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
                    if (isPhoto){
                        messageTextView.setVisibility(View.GONE);
                        photoImageView.setVisibility(View.VISIBLE);
                        Glide.with(photoImageView.getContext()).load(model.getPhotoUrl()).into(photoImageView);
                    }else{
                        messageTextView.setVisibility(View.VISIBLE);
                        photoImageView.setVisibility(View.GONE);
                        messageTextView.setText(model.getMessage());
                    }
                    flag= new Flag(1);
                    view.setTag(flag);
                }
                populateView(view, model);
            }

        return view;
    }
    class Flag {
        int flag;
        public Flag(int flag)
        {
            this.flag=flag;
        }
        public int getFlag()
        {
            return flag;
        }

    }

    protected void populateView(View view, Chat chat) {
        ((TextView) view.findViewById(R.id.author)).setText(chat.getAuthor());
        ((TextView) view.findViewById(R.id.message)).setText(chat.getMessage());
        ((TextView) view.findViewById(R.id.time_text)).setText(chat.getTime());
    }
}
