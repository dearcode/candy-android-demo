package net.dearcode.candy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.dearcode.candy.controller.ServiceBinder;
import net.dearcode.candy.model.User;

public class CandyActivity extends AppCompatActivity {
    private long id;
    private byte[] avatar;
    private String user;
    private String pass;
    private String nickname;
    private TextView tvUserName;
    private TextView tvUserID;
    private static SQLiteDatabase db;

    private static ServiceBinder conn;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final int waitLogin = 1;
    private static final int waitRegister = 2;

    public User getUser() {
        return new User(id, avatar, user, nickname);
    }

    private void dbInit() {
        db = openOrCreateDatabase("candy.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, user TEXT, pass TEXT, nickname TEXT, avatar TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS friend(id INTEGER PRIMARY KEY)");

        Cursor c = db.rawQuery("SELECT id, user,pass,avatar FROM user limit 1", null);
        if (c.moveToNext()) {
            id = c.getLong(0);
            user = c.getString(1);
            pass = c.getString(2);
//            nickname = c.getString(3);
            //           avatar = c.getString(4);
        }
        c.close();
    }

    public static boolean isFriend(long id) {
        Cursor c = db.rawQuery("SELECT id FROM friend where id= " + id + " limit 1", null);
        if (c.moveToNext()) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public static void addFriend(long id) {
        db.execSQL("insert into friend set id= " + id);
    }


    public static CandyMessage getCandy() {
        return conn.getCandy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn = new ServiceBinder(CandyActivity.this);
        setContentView(R.layout.activity_candy);

        dbInit();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //如果没有保存的账号，就让他登录
        if (user == null || pass == null || user.isEmpty() || pass.isEmpty()) {
            Intent i = new Intent(CandyActivity.this, LoginActivity.class);
            startActivityForResult(i, waitLogin);
        } else {
            Intent i = new Intent(CandyActivity.this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putString("user", user);
            b.putString("pass", pass);
            b.putBoolean("Register", true);
            i.putExtras(b);
            startActivityForResult(i, waitLogin);


        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        Bundle b = data.getExtras();
        long id;

        switch (requestCode) {
            case waitLogin:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Register")) {
                    Intent i = new Intent(CandyActivity.this, RegisterActivity.class);
                    startActivityForResult(i, waitRegister);
                    return;
                }
                user = b.getString("user");
                pass = b.getString("pass");
                break;
            case waitRegister:
                if (b.getBoolean("Redirect") && b.getString("RedirectTo").equals("Login")) {
                    Intent i = new Intent(CandyActivity.this, LoginActivity.class);
                    startActivityForResult(i, waitLogin);
                    return;
                }
                id = b.getLong("id");
                db.execSQL("insert into user (id, user,pass) values (?,?,?)", new Object[]{id, b.getString("user"), b.getString("pass")});
                Intent i = new Intent(CandyActivity.this, LoginActivity.class);
                i.putExtras(b);
                startActivityForResult(i, waitLogin);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_candy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent bintent = new Intent(CandyActivity.this, LoginActivity.class);
            startActivityForResult(bintent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final String[] items = {"联系人", "会话", "朋友圈", "设置"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragment.create(position);
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items[position];
        }
    }


    /*
    private NotificationManager manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    //command是自定义用来区分各种点击事件的
    private void sendCustomerNotification(int command){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Notification");
        builder.setContentText("自定义通知栏示例");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.push));
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);
        /*
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_template_customer);
        remoteViews.setTextViewText(R.id.title,"Notification");
        remoteViews.setTextViewText(R.id.text,"song"+index);
        if(command==CommandNext){
            remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_pause_white);
        }else if(command==CommandPlay){
            if(playerStatus==StatusStop){
                remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_pause_white);
            }else{
                remoteViews.setImageViewResource(R.id.btn1,R.drawable.ic_play_arrow_white_18dp);
            }
        }
        Intent Intent1 = new Intent(this,MediaService.class);
        Intent1.putExtra("command",CommandPlay);
        //getService(Context context, int requestCode, @NonNull Intent intent, @Flags int flags)
        //不同控件的requestCode需要区分开 getActivity broadcoast同理
        PendingIntent PIntent1 =  PendingIntent.getService(this,5,Intent1,0);
        remoteViews.setOnClickPendingIntent(R.id.btn1,PIntent1);

        Intent Intent2 = new Intent(this,MediaService.class);
        Intent2.putExtra("command",CommandNext);
        PendingIntent PIntent2 =  PendingIntent.getService(this,6,Intent2,0);
        remoteViews.setOnClickPendingIntent(R.id.btn2,PIntent2);

        Intent Intent3 = new Intent(this,MediaService.class);
        Intent3.putExtra("command",CommandClose);
        PendingIntent PIntent3 =  PendingIntent.getService(this,7,Intent3,0);
        remoteViews.setOnClickPendingIntent(R.id.btn3,PIntent3);

        builder.setContent(remoteViews);
        Notification notification = builder.build();
        manger.notify(0,notification);
    }
        */


}
