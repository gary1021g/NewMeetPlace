package tw.com.example.ben.sharehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import tw.com.example.ben.sharehouse.CHAT.chat_main_layout_controler;
import tw.com.example.ben.sharehouse.CHAT.dataModel.MyUser;
import tw.com.example.ben.sharehouse.CHAT.house_list_controler;
import tw.com.example.ben.sharehouse.lib.TinyDB;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView accountView;
    //fragment 變數宣告


    private house_list_controler fragment03;
    private chat_main_layout_controler chat;
    private chat_main_layout_controler chat_fragment;

    //fragment 管理員 動態轉換頁面用
    private android.app.FragmentManager mFragmentMgr;
    //小型資料庫  放置使用者資料
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Share House");
        setContentView(R.layout.activity_main);
        //右上角的選單 初始
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //儲存USER資料
        tinydb = new TinyDB(this);
        MyUser myUser= (MyUser) tinydb.getObject("MyUser", MyUser.class);
        //顯示右上角的選單
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        //右下角浮動圖示
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //初始化frame   frameManager  class雖然是全域 但是還沒新增 要先new
        fragment03 = new house_list_controler();
        chat = new chat_main_layout_controler();
        chat_fragment =new chat_main_layout_controler();
        mFragmentMgr = getFragmentManager();
        //drawer初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //DRAWER結合actionbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        //顯示左選單
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //左選單內顯示帳號
        View header = navigationView.getHeaderView(0);
        accountView = (TextView) header.findViewById(R.id.accountView);
        accountView.setText(myUser.getAccount().toString());
        mFragmentMgr.beginTransaction().replace(R.id.container, fragment03, "fragment01").commit();
    }

    //複寫返回鍵觸發事件  當左邊功能選項打開時 返回建
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //右上角選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //右上角選單選取事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Intent login = new Intent(this,LoginActivity.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {      //設定
            return true;
        }
        else if (id == R.id.action_logOut) {    //登出
            new AlertDialog.Builder(this)
                    .setTitle("登出")
                    .setMessage("確定要登出？")
                    .setPositiveButton("登出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            //紀錄狀態為登出
                            tinydb.putInt("loginState",0);
                            //跳轉登入畫面
                            startActivity(login);
                            finish();
                        }
                    })
                    .setNeutralButton("取消", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")


    //側邊選項  按紐監聽器實作
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.chat) {
            //把 container layout 換成 fragment.class(使replace的方式) 最後要commit
            mFragmentMgr.beginTransaction().replace(R.id.container, fragment03, "fragment01").commit();
        }
        // 按了自動關閉 並回傳true
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
