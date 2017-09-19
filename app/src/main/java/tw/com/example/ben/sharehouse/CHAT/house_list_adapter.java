package tw.com.example.ben.sharehouse.CHAT;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tw.com.example.ben.sharehouse.CHAT.dataModel.House;
import tw.com.example.ben.sharehouse.R;

/**
 * Created by Ben on 16/8/8.
 */
public class house_list_adapter extends BaseAdapter{
    private ChildEventListener mListener;
    Activity context;
    private List<tw.com.example.ben.sharehouse.CHAT.dataModel.House> mModels;
    private List<String> mKeys;
    private List<House> Houses;
    House House;
    Query mRef;
    public house_list_adapter(Activity activity, Query mRef)
    {
        this.mRef = mRef;
        this.context = activity;
        mModels = new ArrayList<House>();
        mKeys = new ArrayList<String>();
       // House = new House("Ben","123",);
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                House model = dataSnapshot.getValue(House.class);
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
                House newModel = dataSnapshot.getValue(House.class);
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
                House newModel = dataSnapshot.getValue(House.class);
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
    public View getView(int i, View view, ViewGroup viewGroup){
        House model = mModels.get(i);
        if(view== null) {
            view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, null, false);
        }
        populateView(view, model);
        return view;
    }
    protected void populateView(final View view, final House House) {

        String houseUrl = House.getUrl();
        String name;
        DatabaseReference Database;

        Database = FirebaseDatabase.getInstance().getReferenceFromUrl(houseUrl);

        Database.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        House house = dataSnapshot.getValue(House.class);
                        TextView nameView = (TextView) view.findViewById(R.id.name);
                        nameView.setText(house.getName().toString());
                        TextView chat = (TextView) view.findViewById(R.id.chat);
                        chat.setText(house.getChat());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                    }
                });


    }
    public void cleanup() {
        //移除監聽器、container
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }
}
