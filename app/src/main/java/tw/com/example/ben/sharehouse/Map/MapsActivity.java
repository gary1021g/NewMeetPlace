package tw.com.example.ben.sharehouse.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import tw.com.example.ben.sharehouse.CHAT.FirebaseListAdapter;
import tw.com.example.ben.sharehouse.CHAT.dataModel.Chat;
import tw.com.example.ben.sharehouse.CHAT.dataModel.MyUser;
import tw.com.example.ben.sharehouse.LoginActivity;
import tw.com.example.ben.sharehouse.R;
import tw.com.example.ben.sharehouse.lib.TinyDB;
import tw.com.example.ben.sharehouse.ui.IconSmallerOnTouchListener;

import static android.os.SystemClock.sleep;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener,GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        GoogleMap.OnInfoWindowClickListener,GoogleMap.OnCameraIdleListener,GoogleMap.OnInfoWindowLongClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);//預設位置，偵測不到使用者位置時使用。
    private static final int DEFAULT_ZOOM = 15;//預設地圖Scale
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;//使用者目前位置
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    //0723
    private FirebaseAuth mAuth;
    //0724
    private DatabaseReference mLocReference;
    public ChildEventListener  mChildEventListener,mUMkraddChildEventListener ;
    public List<Marker> markersList,UmarkerList;
    public int arrsize;
    public LocationRequest mLocationRequest;
    //0729
    public int UMkrNumLimit;
    private DatabaseReference mUMkrReference;
    //0805
    String lastkey,lastemail;
    private int[] TollBarTitle = {R.string.app_name,R.string.RealtimeLoc_Switch,R.string.about};
    //0807
    private DrawerLayout DL;
    private FrameLayout FL;
    protected NavigationView NV,NP_NV;
    Toolbar toolbar;
    FirebaseUser currentUser;
    //0811
    int locreqflag;//是否同步自身位置
    //0814
    private Marker[] placeMarkers;
    private MarkerOptions[] places;
    String nxtstring;
    int page;
    //0815
    LatLng centerpoint;
    private Marker centermarker;
    private int nearradius;
    private DatabaseReference NearplaceReference;
    private FirebaseDatabase MapDatabase;
    //0820
    private ImageView send ,addpic ,chat;
    //0822
    private NavigationView NVr;
    private int NVrmenupage;
    public  List<String> chatmember;
    //0826
    public String Chatroom_Key;
    //0828
    private ListView LV;
    private boolean flag_chatlist;
    private String mUsername;
    private Firebase mFirebaseRef;
    private FirebaseListAdapter mChatListAdapter;
    private EditText mMessageEditText;
    //0829
    public  List<String> placetype;
    private String finplacetype;
    //0904
    private DatabaseReference CenterpointReference;
    //0913
    private DatabaseReference ManagerReferenece;
    public Boolean isManager,isfollowmode;
    private ValueEventListener mManagerValueEventListener;
    private int nearplacenum;
    int MAX_PLACES = 60;
    //0918
    private ChildEventListener mNearPlaceChildEventListener;
    private static final int RC_PHOTO_PICKER = 2 ;
    private StorageReference mChatPhotosStorageReferenece;
    private FirebaseStorage mFirebaseStorage;


    // TODO: OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        FirebaseApp app = FirebaseApp.getInstance("MapRtDb");
        MapDatabase = FirebaseDatabase.getInstance(app);
        //MapDatabase.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Intent it = getIntent();
        String FIREBASE_URL = it.getStringExtra("URL");
        Chatroom_Key=it.getStringExtra("roomkey");
        Log.i("key: ",Chatroom_Key);
        flag_chatlist = false;
        LV= (ListView)findViewById(R.id.chatlist);
        setupUsername();
        mFirebaseRef = new Firebase(FIREBASE_URL);
        mMessageEditText = (EditText) findViewById( R.id.messageInput) ;
        isfollowmode = false;
        isManager = false;
        if(currentUser==null ){
            Intent intent = new Intent();
            intent.setClass(MapsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        //0724
        else {
            //0819
            mLocReference = MapDatabase.getReference().child(Chatroom_Key).child("users");
            markersList = new ArrayList<>();
            arrsize = 0;
            //0729
            UMkrNumLimit = 1;
            mUMkrReference = MapDatabase.getReference().child(Chatroom_Key).child("Umarker");
            UmarkerList = new ArrayList<>();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_rightdrawer);
            FloatingActionButton fab_addusrmkr = (FloatingActionButton) findViewById(R.id.addusermkr);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DL.openDrawer(GravityCompat.END);
                }

            });
            fab_addusrmkr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UMkrNumLimit > 0) {
                        // [START_EXCLUDE]
                        final double[] CameraLat = new double[1];
                        final double[] CameraLon = new double[1];
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                CameraLat[0] = mMap.getCameraPosition().target.latitude;
                                CameraLon[0] = mMap.getCameraPosition().target.longitude;
                                submitUmkrlocation(Double.toString(CameraLat[0]), Double.toString(CameraLon[0]));
                            }
                        });

                    }
                    else{
                        Toast.makeText(MapsActivity.this, "標記已達上限，無法再增加!", Toast.LENGTH_LONG).show();
                    }
                }

            });
            //0807
            setUpToolBar();
            toolbar.setLogo(R.mipmap.ic_launcher);//设置app logo
            toolbar.setTitle(TollBarTitle[0]);//设置主標题
            locreqflag = 1;
            //0814

            placeMarkers = new Marker[MAX_PLACES];
            //0815
            nearradius = 300;
            NearplaceReference = MapDatabase.getReference().child(Chatroom_Key).child("Nearplace");
            ManagerReferenece = MapDatabase.getReference().child(Chatroom_Key).child("Manager");
            //0826
            check_manager();
            set_map_member_loc();
            set_map_customize_mkr();
            set_manager();
            set_usersetting();
            set_nearplace_mkr();
            //0904
            CenterpointReference = MapDatabase.getReference().child(Chatroom_Key).child("CenterPoint");
            mFirebaseStorage = FirebaseStorage.getInstance();
            mChatPhotosStorageReferenece = mFirebaseStorage.getReference().child("chat_photos");


        }
        //0820
        send = (ImageView) findViewById(R.id.sendButton);
        addpic = (ImageView) findViewById( R.id.pic);
        chat = (ImageView) findViewById( R.id.chatB);
        //0821
        addpic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        addpic.setOnTouchListener(new IconSmallerOnTouchListener());

        chat.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!flag_chatlist){
                    View mapv = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                    if (mapv != null) {
                        mapv.setAlpha(0.3f);
                    }
                    //LV.setVisibility(View.VISIBLE);
                    Log.i("onclick","y");
                    flag_chatlist = true;
                }
                else{
                    View mapv = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                    if (mapv != null) {
                        mapv.setAlpha(1.0f);
                    }
                    // LV.setVisibility(View.INVISIBLE);
                    Log.i("onclick","n");
                    flag_chatlist = false;
                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                String time;
                if ( minute < 10 ){
                    time = hour +":0";
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

        //0822
        NVr = (NavigationView) findViewById(R.id.Right_Navigation);
        NVrmenupage = 1;
        chatmember = new ArrayList<>();
        placetype = new ArrayList<>();

        mChatListAdapter = new FirebaseListAdapter(mFirebaseRef, this, mUsername,R.layout.chat_user1_item);
        //綁定adapter
        LV.setAdapter(mChatListAdapter);
        //如果資料有變 焦點到listview最下方

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //移到listview最下方
                LV.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    public void onPause(){
        super.onPause();

    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //0725
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // TODO: OnMapReady
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker marker)
            {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout)findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;

            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                return null;
            }

        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        //0730
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        //mMap.setOnCameraMoveStartedListener(this);
       // mMap.setOnCameraMoveListener(this);
        //mMap.setOnCameraMoveCanceledListener(this);

    }
    @Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getTag() == "nearplace")
            return;
        centerpoint = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        finplacetype = null;//reset finplacetype
        placetype.clear();//reset placetype
        getPlaceTypeSelected();
    }

    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        int size=UmarkerList.size();
        int flag=0;
        for(int m=0 ; m < size; ++m) {
            Marker mkr;
            mkr = UmarkerList.get(m);
            String tag = (String) mkr.getTag();
            String tagg = (String) marker.getTag();
            if(tag!=null &&tagg!=null)
                if (tag.equals(tagg)) {
                    flag=1;
                }
        }
        if(flag == 0){
            return ;
        }
        String t = marker.getTitle();
        final LatLng L = marker.getPosition();
        final View item = LayoutInflater.from(MapsActivity.this).inflate(R.layout.modify_mkrtitle, null);
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(t)
                .setCancelable(true)
                .setMessage(R.string.Latitude + L.latitude + "\n" + R.string.Longtitude + L.longitude)
                .setPositiveButton(R.string.Change_Title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Modify Marker Name")
                                .setView(item)
                                .setCancelable(true)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText e = (EditText) item.findViewById(R.id.edit_text);
                                        String s = e.getText().toString();
                                        if (TextUtils.isEmpty(s)) {
                                            e.setError("Name can not be empty");
                                        } else {
                                            marker.setTitle(e.getText().toString());
                                            final LatLng L = marker.getPosition();
                                            final String key = (String) marker.getTag();
                                            ValueEventListener postListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                                                    UserMkr umkr ;
                                                    if (usermkr != null) {
                                                        umkr = new UserMkr(Double.toString(L.latitude), Double.toString(L.longitude), marker.getTitle(), usermkr.Email);
                                                        mUMkrReference.child(key).setValue(umkr);
                                                        Toast.makeText(getApplicationContext(), R.string.success_modify, Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    // Getting Post failed, log a message
                                                    // ...
                                                }
                                            };
                                            mUMkrReference.child(key).addListenerForSingleValueEvent(postListener);

                                        }


                                    }
                                })
                                .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();

                    }
                })
                .setNegativeButton(R.string.del_mkr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = (String) marker.getTag();
                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                                if (usermkr != null) {
                                    lastemail = usermkr.Email;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                // ...
                            }
                        };
                        mUMkrReference.child(key).addListenerForSingleValueEvent(postListener);
                        mUMkrReference.child(key).removeValue();
                        lastkey=key;


                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // TODO: GetDeviceLocation
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            submitlocation(mLastKnownLocation);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mLastKnownLocation = null;
        }
    }
    //0723
    public void submitlocation(Location L) {
        if(currentUser!=null){
            String userId = getUid();
            String useremail = getEmail();
            User user = new User(useremail,Double.toString(L.getLatitude()),Double.toString(L.getLongitude()) );
            mLocReference.child(userId).setValue(user);
        }
    }
    public String getUid() {
        return currentUser.getUid();
    }
    public String getEmail() {
        return currentUser.getEmail();
    }
    @Override
    public void onStart() {
        super.onStart();
    }


    public void onDestroy(){
        super.onDestroy();
        if (mChildEventListener != null) {
            mLocReference.removeEventListener(mChildEventListener);
        }
        if(markersList!=null){
            for(int pm=0; pm<markersList.size(); pm++){
                if(markersList.get(pm) !=null)
                    markersList.get(pm).remove();
            }
        }
        if(mUMkraddChildEventListener!=null){
            mUMkrReference.removeEventListener(mUMkraddChildEventListener);
        }
        if(UmarkerList!=null){
            for(int pm=0; pm<UmarkerList.size(); pm++){
                if(UmarkerList.get(pm) !=null)
                    UmarkerList.get(pm).remove();
            }
        }
        if(chatmember!=null){
            for(int pm=0; pm<chatmember.size(); pm++){
                if(chatmember.get(pm) !=null){
                    chatmember.remove(pm);
                }
            }
        }
        if(mManagerValueEventListener!=null){
            ManagerReferenece.removeEventListener(mManagerValueEventListener);
        }
        if( mNearPlaceChildEventListener != null){
            NearplaceReference.removeEventListener(mNearPlaceChildEventListener);
        }

    }

    public void onStop() {
        super.onStop();
    }


    public void onResume(){super.onResume();}

    //0725
    @Override
    public void onLocationChanged(Location l) {
        if(locreqflag == 1){
            submitlocation(l);
        }
    }
    //0729
    private void submitUmkrlocation(String Le , String Lo ){
        String t = "Drag Marker";
        String e = getEmail();
        UserMkr umkr = new UserMkr(Le,Lo,t,e);
        mUMkrReference.push().setValue(umkr);
    }
    // TODO: Marker Drag
    //0730
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        final LatLng L =marker.getPosition();
        final Marker m =marker;
        final String key = (String) marker.getTag();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                UserMkr umkr ;
                if (usermkr != null && key != null) {
                    umkr = new UserMkr(Double.toString(L.latitude),Double.toString(L.longitude),m.getTitle(), usermkr.Email);
                    mUMkrReference.child(key).setValue(umkr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        if (key != null) {
            mUMkrReference.child(key).addListenerForSingleValueEvent(postListener);
        }

    }
    //0804
    @Override
    public boolean onMarkerClick(final Marker marker) {

        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return true;
    }

    //0807
    public void setContentView(@LayoutRes int layoutResID) {
        DL = (DrawerLayout) getLayoutInflater().inflate(R.layout.activtiy_nav_drawer,null);
        FL = (FrameLayout) DL.findViewById(R.id.content_frame);
        NV = (NavigationView)DL.findViewById(R.id.Left_Navigation);
        NP_NV = (NavigationView)DL.findViewById(R.id.Right_Navigation);
        LV = (ListView)DL.findViewById(R.id.chatlist);
        getLayoutInflater().inflate(layoutResID, FL, true);
        super.setContentView(DL);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpNavigation();
        setUpNpitem();
    }
    // TODO: nv_menu setting
    private void setUpNavigation() {
        // Set navigation item selected listener
        NV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.getcenterpoint:
                        getcenterpoint();
                        DL.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.hidenearplace:
                        del_nearplace_mkr();
                        NVr.getMenu().clear();
                        NVrmenupage = 1;
                        DL.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navRealtimeLoc_Switch:
                        if(locreqflag == 1){
                            locreqflag = -1;
                            change_usersetting(false);
                            Menu m = NV.getMenu();
                            MenuItem item = m.findItem(R.id.navRealtimeLoc_Switch);
                            item.setTitle(R.string.open_realtime_access);
                            Log.i("locreqflag",String.valueOf(locreqflag));

                        }
                        else{
                            locreqflag = 1;
                            change_usersetting(true);
                            Menu m = NV.getMenu();
                            MenuItem item = m.findItem(R.id.navRealtimeLoc_Switch);
                            item.setTitle(R.string.close_realtime_access);
                            Log.i("locreqflag",String.valueOf(locreqflag));
                        }





                        DL.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.followmode:
                        if(!isfollowmode){
                            isfollowmode = true;
                            Menu m = NV.getMenu();
                            MenuItem item = m.findItem(R.id.followmode);
                            item.setTitle(R.string.closefollowmode);
                        }
                        else{
                            isfollowmode = false;
                            Menu m = NV.getMenu();
                            MenuItem item = m.findItem(R.id.followmode);
                            item.setTitle(R.string.followmode);
                        }
                        DL.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.navItemAbout:
                           /* Intent intent2 = new Intent();
                            intent2.setClass(Navigation_BaseActivity.this, About.class);
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            finish();*/
                        break;
                    case R.id.navItemLogout:
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to Logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent();
                                        intent.setClass(MapsActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;
                }
                return false;

            }
        });

    }
    public void setUpToolBar() {//設置ToolBar
        //setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DL.openDrawer(GravityCompat.START);
            }
        });
        //設定當使用者點擊ToolBar中的Navigation Icon時，Icon會隨著轉動
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, DL, toolbar,R.string.open_navigation,R.string.close_navigation){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
                TextView Txv =(TextView)findViewById(R.id.navigation_header_userID);
                TextView Txv2 =(TextView)findViewById(R.id.nv_contact_name);
                String e = getEmail();
                Txv.setText(e);
                try {
                    if (NVrmenupage == 1) {
                        Txv2.setText(R.string.chat_member_list);
                        setchatmembermenu();
                    } else if (NVrmenupage == 2) {
                        Txv2.setText(R.string.nearplacestore);
                    }
                }
                catch (Exception ex){
                    Log.i("SetText fail","Textview is outside ");
                }

            }
        };
        DL.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    // TODO: Nearplace setting
    //0811
    private String getDirectionsUrl(LatLng centerpoint,String radius){

        Log.i("placetype",finplacetype);
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+centerpoint.latitude+","+centerpoint.longitude+
                "&radius="+radius+"&sensor=true" +
                "&types="+finplacetype+
                "&key=AIzaSyAwcdfH7kgP5AJwS2NZdvDyot7TLnLh-A8";

    }

    private  String nextpage(String nxt){

        return  "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                "pagetoken="+nxt+
                "&key=AIzaSyAwcdfH7kgP5AJwS2NZdvDyot7TLnLh-A8";
    }

    @Override
    public void onCameraIdle() {
        Log.i("idle","idle");
        if(isManager){
            final double[] CameraLat = new double[1];
            final double[] CameraLon = new double[1];
            final double[] CameraZoom = new double[1];
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraLat[0] = mMap.getCameraPosition().target.latitude;
                    CameraLon[0] = mMap.getCameraPosition().target.longitude;
                    CameraZoom[0] = mMap.getCameraPosition().zoom;
                    Manager m = new Manager(getEmail(),Double.toString(CameraLat[0]),Double.toString(CameraLon[0]),Double.toString(CameraZoom[0]));
                    ManagerReferenece.setValue(m);
                }
            });
        }
    }



    private class TransTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while(line!=null){
                    //Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d("JSON", s);
            parseJSON(s);
            if(nearradius>=1000 && page == 1 && places.length<10){
                nearradius += 1000;
                String url = getDirectionsUrl(centerpoint, Integer.toString(nearradius));
                new TransTask().execute(url);
                return;
                /*if(places.length < 1){
                    Toast.makeText(MapsActivity.this, "中心點附近無相關地點，請重新查詢", Toast.LENGTH_LONG).show();
                }*/

            }
            else if(page == 1 && places.length<10 )
            {
                nearradius += 250;
                String url = getDirectionsUrl(centerpoint, Integer.toString(nearradius));
                new TransTask().execute(url);
                return;
            }
            if(places!=null && placeMarkers!=null){
                Log.i("len",Integer.toString(places.length));
                int p ;
                int pagen = (page-1)*20;
                for(p=pagen; p<places.length+pagen && p<placeMarkers.length+pagen; p++){
                    //will be null if a value was missing
                    if(places[p-pagen]!=null){
                        placeMarkers[p]=mMap.addMarker(places[p-pagen]);
                        placeMarkers[p].setTag("nearplace");
                        NavigationView nv = (NavigationView) findViewById(R.id.Right_Navigation);
                        final Menu menu = nv.getMenu();
                        menu.add(0,p,p,placeMarkers[p].getTitle());
                        if(isManager){
                            Nearplace n = new Nearplace(places[p-pagen].getTitle(),places[p-pagen].getSnippet()
                                    ,String.valueOf(places[p-pagen].getPosition().latitude),String.valueOf(places[p-pagen].getPosition().longitude));
                            NearplaceReference.child(Integer.toString(p)).setValue(n);
                        }

                    }
                }
                nearplacenum=p-1;
            }
            while(nxtstring!=null){
                page++;
                sleep(2000);
                Log.i("Start next page","123");
                String nxtpgeurl =nextpage(nxtstring);
                nxtstring = null;
                new TransTask().execute(nxtpgeurl);
            }
            Log.i("Radius",Integer.toString(nearradius));
            if(nearradius<=750)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        centerpoint, DEFAULT_ZOOM));
            else if(nearradius ==1000)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        centerpoint, DEFAULT_ZOOM-1));
            else if(nearradius <=3000)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        centerpoint, DEFAULT_ZOOM-2));
            else if(nearradius <=6000)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        centerpoint, DEFAULT_ZOOM-3));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        centerpoint, DEFAULT_ZOOM-4));



        }
        private void parseJSON(String s) {
            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(s);
                JSONArray placesArray = resultObject.getJSONArray("results");

                try{
                    nxtstring = resultObject.getString("next_page_token");
                }
                catch(Exception e)
                {
                    String i="NuLL";
                    Log.i("No next!",i);
                }
                // Log.i("nxtstring: ",nxtstring);
                places = new MarkerOptions[placesArray.length()];
                if(placesArray.length() < 10 )
                    return;
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    boolean missingValue;
                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";
                    String rating="";
                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                        placeLL = new LatLng(
                                Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        JSONArray types = placeObject.getJSONArray("types");
                        for(int t=0; t<types.length(); t++){
                            String thisType=types.get(t).toString();
                            //what type is it
                            if(thisType.contains("food")){
                                //currIcon = foodIcon;
                                break;
                            }
                            else if(thisType.contains("bar")){
                                //currIcon = drinkIcon;
                                break;
                            }
                            else if(thisType.contains("store")){
                                //currIcon = shopIcon;
                                break;
                            }
                        }
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");
                        try{
                            rating = placeObject.getString("rating");
                        }
                        catch(Exception e){
                            rating = "";
                        }

                    }
                    catch(JSONException jse){
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    if(missingValue)
                        places[p]=null;

                    else{
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet("地址: "+vicinity+"\n"+"評分: "+rating);

                    }

                }



            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //0815

    public void getcenterpoint(){
        //final LatLng[] centerpoint = new LatLng[1];
        final double[] lat = {0.0};
        final double[] lon = {0.0};
        final double[] num = {0.0};

        ValueEventListener getcenterpointListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.v(TAG,""+ postSnapshot.getKey());
                    User user = postSnapshot.getValue(User.class);
                    if(user!= null){
                        lat[0] +=  Double.parseDouble(user.Lat);
                        Log.i("lat",user.Lat);
                        lon[0] +=  Double.parseDouble(user.Lon);
                        num[0] +=  1.0;
                    }
                }
                lat[0]/=num[0];
                lon[0]/=num[0];
                Log.i("Lat",Double.toString(lat[0]));
                centerpoint = new LatLng(lat[0],lon[0]);
                if(centermarker != null){
                    centermarker.remove();
                }
                centermarker = mMap.addMarker(new MarkerOptions().position(centerpoint)
                        .title("Center Point ")
                        .icon(BitmapDescriptorFactory.defaultMarker(100))
                        .draggable(false));
                CenterPoint cp = new CenterPoint(Double.toString(lat[0]),Double.toString(lon[0]));
                if(isManager){
                    CenterpointReference.setValue(cp);
                }
                finplacetype = null;//reset finplacetype
                placetype.clear();//reset placetype
                getPlaceTypeSelected();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(centerpoint.latitude,
                                centerpoint.longitude), DEFAULT_ZOOM)
                );
                centermarker.showInfoWindow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        mLocReference.addListenerForSingleValueEvent(getcenterpointListener);


    }
    // TODO: NV_r menu setting
    private void setUpNpitem(){

        NP_NV.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(NVrmenupage == 1){
                    //String s = (String) menuItem.getTitle();
                    int cp = menuItem.getGroupId();
                    if(cp == 0){
                        int itemid = menuItem.getItemId();
                        Marker mkr = markersList.get(itemid);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mkr.getPosition().latitude,
                                        mkr.getPosition().longitude), DEFAULT_ZOOM)
                        );
                        mkr.showInfoWindow();

                    }
                    else if(cp == 1){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(centerpoint.latitude,
                                        centerpoint.longitude), DEFAULT_ZOOM)
                        );
                        centermarker.showInfoWindow();

                    }
                    else if (cp == 2){
                        int itemid = menuItem.getItemId();
                        Marker mkr = UmarkerList.get(itemid);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mkr.getPosition().latitude,
                                        mkr.getPosition().longitude),DEFAULT_ZOOM)
                        );
                        mkr.showInfoWindow();
                    }
                    DL.closeDrawer(Gravity.END);
                }



                if (NVrmenupage == 2){
                    for (int p=0;p<nearplacenum;p++) {
                        //will be null if a value was missing
                        if (placeMarkers[p] != null) {
                            placeMarkers[p].setIcon(BitmapDescriptorFactory.defaultMarker(0));
                            placeMarkers[p].setAlpha(0.6f);
                        }
                    }
                    int id = menuItem.getItemId();
                    placeMarkers[id].setIcon(BitmapDescriptorFactory.defaultMarker(200));
                    placeMarkers[id].setAlpha(1.0f);
                    placeMarkers[id].showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(placeMarkers[id].getPosition().latitude,
                                    placeMarkers[id].getPosition().longitude),DEFAULT_ZOOM)
                    );
                    DL.closeDrawer(Gravity.END);
                }
                return false;
            }
        });
    }

    private void setchatmembermenu(){
        Menu menu = NVr.getMenu();
        menu.clear();
        for(int i = 0 ; i<chatmember.size();++i){
            menu.add(0,i,Menu.NONE,chatmember.get(i));
        }
        if(centermarker!=null){
            menu.add(1,0,Menu.NONE,"中心點");
        }
        if(UmarkerList.size()!=0){
            for(int i = 0 ; i<UmarkerList.size();++i){
                Marker mkr = UmarkerList.get(i);
                menu.add(2,i,Menu.NONE,mkr.getTitle());
            }


        }
    }

    public void set_map_member_loc(){
        //0724
        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildADD:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                if(user!=null){
                    String userKey = dataSnapshot.getKey();
                    // [START_EXCLUDE]
                    LatLng latLng = new LatLng(Double.parseDouble(user.Lat), Double.parseDouble(user.Lon));
                    //Log.i("Lat",user.Lat);
                    Marker marker=mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(user.email)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    marker.setTag(userKey);
                    markersList.add(marker);
                    chatmember.add(user.email);
                }
               /* Marker currentMarker = markersList.get(arrsize);
                                     LatLng lat = currentMarker.getPosition();
                                      Log.i("Lat",Double.toString(lat.latitude));
                                      arrsize++;
                                      */

                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);
                String userKey = dataSnapshot.getKey();
                LatLng latLng ;
                if (user != null) {
                    latLng = new LatLng(Double.parseDouble(user.Lat), Double.parseDouble(user.Lon));
                    int size=markersList.size();
                    // Log.i("size:",String.valueOf(size));
                    for(int m=0 ; m < size; ++m) {
                        Marker mkr;
                        mkr = markersList.get(m);
                        String tag= String.valueOf(mkr.getTag());
                        Log.i("tag:",tag);
                        if(tag.equals(userKey))
                        {
                            mkr.setPosition(latLng);
                            //Log.i("size:",String.valueOf(size));
                        }
                    }
                }
                // [START_EXCLUDE]

                //Log.i("Lat",user.Lat);
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
        };

        mLocReference.addChildEventListener(childEventListener);
        mChildEventListener = childEventListener;
    }
    public void set_map_customize_mkr(){
        //0730
        ChildEventListener UmarkerchildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                String key = dataSnapshot.getKey();
                UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                String email=getEmail();
                // [START_EXCLUDE]
                LatLng latLng ;
                if (usermkr != null) {
                    latLng = new LatLng(Double.parseDouble(usermkr.Lat), Double.parseDouble(usermkr.Lon));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(usermkr.Title)
                            .icon(BitmapDescriptorFactory.defaultMarker(60))
                            .draggable(true));
                    marker.setTag(key);
                    UmarkerList.add(marker);
                    if(email.equals(usermkr.Email)){
                        UMkrNumLimit--;
                    }
                }
                //Log.i("Lat",user.Lat);

               /* Marker currentMarker = markersList.get(arrsize);
                                     LatLng lat = currentMarker.getPosition();
                                      Log.i("Lat",Double.toString(lat.latitude));
                                      arrsize++;
                                      */
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                String key = dataSnapshot.getKey();
                LatLng latLng ;
                if (usermkr != null) {
                    latLng = new LatLng(Double.parseDouble(usermkr.Lat), Double.parseDouble(usermkr.Lon));
                    if(usermkr.Email.equals(getEmail())){
                        Log.i("NUM: ",Integer.toString(UMkrNumLimit));
                    }
                    // [START_EXCLUDE]
                    int size=UmarkerList.size();
                    // Log.i("size:",String.valueOf(size));
                    for(int m=0 ; m < size; ++m) {
                        Marker mkr;
                        mkr = UmarkerList.get(m);
                        String tag= String.valueOf(mkr.getTag());
                        if (tag.equals(key)) {
                            mkr.setPosition(latLng);
                        }

                    }
                }
                //Log.i("Lat",user.Lat);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                UserMkr usermkr = dataSnapshot.getValue(UserMkr.class);
                String key = dataSnapshot.getKey();
                String email;
                String e =getEmail();
                if (usermkr != null) {
                    email = usermkr.Email;
                    int size=UmarkerList.size();
                    for (int m = 0; m < size; ++m) {
                        Marker mkr;
                        mkr = UmarkerList.get(m);
                        if (key.equals(mkr.getTag())) {
                            mkr.remove();
                            UmarkerList.remove(m);
                        }
                    }
                    if (e.equals(email)) {
                        UMkrNumLimit += 1;
                    }
                }




            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUMkrReference.addChildEventListener(UmarkerchildEventListener);
        mUMkraddChildEventListener = UmarkerchildEventListener;

    }

    private void setupUsername() {
        TinyDB tinydb;
        tinydb = new TinyDB(this);
        MyUser myUser = (MyUser) tinydb.getObject("MyUser",MyUser.class);
        mUsername=myUser.getAccount();
    }

    private void getPlaceTypeSelected(){

        final View item = LayoutInflater.from(MapsActivity.this).inflate(R.layout.placetypeselected, null);
        for(int i = 0;i<4;i++){
            placetype.add("null");
        }
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("選擇地點類型")
                .setView(item)
                .setCancelable(true)
                .setPositiveButton("確認", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0 ; i<placetype.size();++i){
                            if(!Objects.equals(placetype.get(i), "null") && placetype.get(i) != null) {
                                if (finplacetype != null) {
                                    finplacetype = finplacetype + "|";
                                    finplacetype = finplacetype + placetype.get(i);
                                }
                                else{
                                    finplacetype = placetype.get(i);
                                }

                            }

                        }
                        NearplaceReference.removeValue();
                        del_nearplace_mkr();//Clear exist nearplace Marker
                        NVr.getMenu().clear();//Clear right menu list
                        NVrmenupage = 2;//set menu page: nearplace list
                        page = 1;//reset result page
                        nearradius = 250 ; //default search radius
                        String url = getDirectionsUrl(centerpoint,"250");//get search url
                        new TransTask().execute(url);//search nearplace
                    }

                })
                .setNegativeButton("取消", null)
                .show();

        CheckBox c1 = (CheckBox)item.findViewById(R.id.cb_restaurant);
        CheckBox c2 = (CheckBox)item.findViewById(R.id.cb_coffee);
        CheckBox c3 = (CheckBox)item.findViewById(R.id.cb_shoppingmall);
        CheckBox c4 = (CheckBox)item.findViewById(R.id.cb_convenience_store);
        c1.setOnCheckedChangeListener(mOnCheckedChangeListener);
        c2.setOnCheckedChangeListener(mOnCheckedChangeListener);
        c3.setOnCheckedChangeListener(mOnCheckedChangeListener);
        c4.setOnCheckedChangeListener(mOnCheckedChangeListener);




    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.cb_restaurant:
                    if(isChecked) {
                        placetype.add(0,"restaurant");
                    } else{
                        placetype.set(0,"null");
                    }
                    break;
                case R.id.cb_coffee:
                    if(isChecked) {
                        placetype.add(1,"cafe");
                    } else{
                        placetype.set(1,"null");
                    }
                    break;
                case R.id.cb_shoppingmall:
                    if(isChecked) {
                        placetype.add(2,"shopping_mall");

                    } else{
                        placetype.set(2,"null");
                    }
                    break;
                case R.id.cb_convenience_store:
                    if(isChecked) {
                        placetype.add(3,"convenience_store");

                    } else{
                        placetype.set(3,"null");
                    }
                    break;


            }
        }

    };


    private void del_nearplace_mkr() {
        if (placeMarkers != null) {
            for (Marker placeMarker : placeMarkers) {
                if (placeMarker != null)
                        placeMarker.remove();
            }
        }
        placeMarkers = new Marker[MAX_PLACES];
    }

    private void set_manager(){

        ValueEventListener ManagerEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Manager m = dataSnapshot.getValue(Manager.class);
                if(!isManager && isfollowmode && m!=null){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(m.Camerapos_lat),
                                    Double.parseDouble(m.Camerapos_lon)), Float.parseFloat(m.Camerapos_zoom)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        ManagerReferenece.addValueEventListener(ManagerEventListener);
        mManagerValueEventListener = ManagerEventListener;


    }

    private void check_manager(){
        ValueEventListener ManagerValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Manager m = dataSnapshot.getValue(Manager.class);
                if (m == null){
                    m = new Manager(getEmail(),"0.0","0.0","15");
                    ManagerReferenece.setValue(m);
                    isManager = true;
                    Menu menu = NV.getMenu();
                    MenuItem item = menu.findItem(R.id.followmode);
                    item.setVisible(false);
                }
                else {
                    if( getEmail().equals(m.manager_email)) {
                        isManager = true;
                        Toast.makeText(getApplicationContext(),"Manger", Toast.LENGTH_SHORT).show();
                        Menu menu = NV.getMenu();
                        MenuItem item = menu.findItem(R.id.followmode);
                        item.setVisible(false);

                    }
                    else{
                        isManager = false;
                        Toast.makeText(getApplicationContext(),"Not Manger", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        ManagerReferenece.addListenerForSingleValueEvent(ManagerValueEventListener);

    }
    public  void  set_usersetting(){
        TinyDB tinydb;
        tinydb = new TinyDB(this);
        try{
            User_MapSettings UserMapSettings = (User_MapSettings) tinydb.getObject("UserMapSettings",User_MapSettings.class);
            String locreq =UserMapSettings.getIslocreq();
            if(locreq !=null){
                if (Objects.equals(locreq, "true")){
                    locreqflag = 1;
                    Menu m = NV.getMenu();
                    MenuItem item = m.findItem(R.id.navRealtimeLoc_Switch);
                    item.setTitle(R.string.close_realtime_access);
                    Log.i("set locreqflag",String.valueOf(locreqflag));
                }
                else {
                    locreqflag = -1;
                    Menu m = NV.getMenu();
                    MenuItem item = m.findItem(R.id.navRealtimeLoc_Switch);
                    item.setTitle(R.string.open_realtime_access);
                    Log.i("set locreqflag",String.valueOf(locreqflag));
                }

            }
        }
        catch (Exception e){
            User_MapSettings UserMapSettings = new User_MapSettings(getEmail(),"false");
            tinydb.putObject("UserMapSettings",UserMapSettings);
        }

    }
    public void change_usersetting(boolean b){
        TinyDB tinydb;
        tinydb = new TinyDB(this);

        if(b){
            User_MapSettings UserMapSettings = new User_MapSettings(getEmail(),"true");
            tinydb.putObject("UserMapSettings",UserMapSettings);
        }
        else{
            User_MapSettings UserMapSettings = new User_MapSettings(getEmail(),"false");
            tinydb.putObject("UserMapSettings",UserMapSettings);
        }
    }

    private void set_nearplace_mkr() {
        ChildEventListener NearplaceEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(isfollowmode){
                    String key = dataSnapshot.getKey();
                    Nearplace np = dataSnapshot.getValue(Nearplace.class);
                    if(np!=null){
                         LatLng l = new LatLng(Double.parseDouble(np.lat),Double.parseDouble(np.lon));
                        int ky =Integer.parseInt(key);
                        placeMarkers[ky]=mMap.addMarker(new MarkerOptions().position(l)
                            .title(np.title)
                            .snippet(np.vicinity)
                            .icon(BitmapDescriptorFactory.defaultMarker(0))
                            .draggable(false));
                        placeMarkers[ky].setTag("nearplace");
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                del_nearplace_mkr();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        NearplaceReference.addChildEventListener(NearplaceEventListener);
        mNearPlaceChildEventListener = NearplaceEventListener;
    }

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



